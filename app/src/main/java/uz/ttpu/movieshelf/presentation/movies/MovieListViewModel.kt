/*
 * 15-VAZIFA: bitta harakat ("yurak" bosilishi) barcha qatlamlar bo'ylab
 *
 * (1) CHAQIRUVLAR TARTIBI (boshqaruv oqimi: bosish ICHKARIGA boradi):
 *     1. UI (taqdimot)          MovieItem: yurak IconButton'i bosiladi
 *     2. ViewModel (taqdimot)   MovieListViewModel.onFavoriteClick(movieId)
 *     3. Use case (domen)       ToggleFavoriteUseCase.invoke(movieId)
 *     4. Repository (data)      MovieRepositoryImpl.toggleFavorite(movieId)
 *     5. Ma'lumot manbai (data) SharedPrefsMovieLocalDataSource.setFavorite(id, favorite)
 *     Javob (yangi isFavorite qiymati) teskari yo'nalishda TASHQARIGA qaytadi: manba -> repository ->
 *     use case -> ViewModel holatni yangilaydi (StateFlow) -> UI qayta chiziladi.
 *
 * (2) MANBA-KOD BOG'LIQLIKLARI (o'q "kim kimni import qiladi/amalga oshiradi"):
 *     MovieItem          - ViewModel'ni IMPORT QILMAYDI: faqat onFavoriteClick lambdasini oladi;
 *                          ularni MainActivity (composition root) ulaydi.
 *     MovieListViewModel --> ToggleFavoriteUseCase                 (taqdimot -> domen: ichkariga)
 *     ToggleFavoriteUseCase --> MovieRepository (interfeys)        (domen ichida)
 *     MovieRepositoryImpl --> MovieRepository (interfeys)          (data -> domen: ichkariga)
 *     MovieRepositoryImpl --> MovieLocalDataSource (interfeys)     (data ichida)
 *     SharedPrefsMovieLocalDataSource --> MovieLocalDataSource     (data ichida)
 *     CHAQIRUVGA TESKARI o'q: runtime'da use case MovieRepositoryImpl'ni chaqiradi (ichkaridan
 *     tashqariga), lekin manba-kodda o'q MovieRepositoryImpl'dan domendagi MovieRepository
 *     interfeysiga qaraydi. Sababi - Dependency Inversion: interfeys domenga tegishli, shuning
 *     uchun domen ma'lumotlar qatlamini bilmaydi, aksincha, data domenga bog'lanadi.
 *     (Chaqiruv yo'nalishi va bog'liqlik yo'nalishi bir xil bo'lishi shart emas.)
 *
 * (3) NIMA UCHUN use case Log.d'ni chaqira olmaydi va domendan loglashning toza usuli:
 *     Domen sof Kotlin moduli: uning classpath'ida Android yo'q, android.util.Log mavjud emas
 *     (import qizaradi) va Dependency Rule domenni Android kabi tashqi tafsilotlardan mustaqil
 *     saqlashni talab qiladi. Toza usul - yana Dependency Inversion: domenda
 *     `interface Logger { fun d(tag: String, message: String) }` e'lon qilinadi, use case uni
 *     konstruktor orqali oladi; tashqi qatlamda (app) `AndroidLogger : Logger` Log.d bilan
 *     amalga oshiriladi va composition root (AppContainer) use case'ga uzatadi. Testda esa
 *     soxta Logger berish mumkin.
 */
package uz.ttpu.movieshelf.presentation.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.IOException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.ttpu.movieshelf.domain.model.Movie
import uz.ttpu.movieshelf.domain.usecase.GetFavoriteMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.ToggleFavoriteUseCase

sealed interface MovieListUiState {
    data object Loading : MovieListUiState
    data class Success(
        val movies: List<Movie>,
        val isFromCache: Boolean,
        val favoritesOnly: Boolean = false,
    ) : MovieListUiState
    data class Error(val message: String) : MovieListUiState
}

// Taqdimot qatlami faqat DOMEN bilan gaplashadi: bu faylda data paketidan hech narsa import qilinmaydi.
class MovieListViewModel(
    private val getMovies: GetMoviesUseCase,
    private val getFavoriteMovies: GetFavoriteMoviesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<MovieListUiState>(MovieListUiState.Loading)
    val state: StateFlow<MovieListUiState> = _state.asStateFlow()

    private var loadJob: Job? = null

    // "Favorites only" belgisi ViewModel'da saqlanadi va har bir qayta yuklashda ishlatiladi.
    private var favoritesOnly = false

    init { load() }

    fun onRefresh() = load()

    fun onFavoritesOnlyChange(enabled: Boolean) {
        favoritesOnly = enabled
        load()   // to'g'ri use case bilan qayta yuklaydi
    }

    fun onFavoriteClick(movieId: Int) {
        viewModelScope.launch {
            val isNowFavorite = toggleFavorite(movieId)
            // Faqat shu kinoning isFavorite belgisi o'zgaradi; tarmoqdan qayta yuklash yo'q.
            _state.update { current ->
                if (current is MovieListUiState.Success) {
                    val updated = current.movies.map { movie ->
                        if (movie.id == movieId) movie.copy(isFavorite = isNowFavorite) else movie
                    }
                    current.copy(
                        // Favorites ko'rinishida sevimlilikdan chiqarilgan kino darhol yo'qoladi.
                        movies = if (current.favoritesOnly) updated.filter { it.isFavorite } else updated
                    )
                } else {
                    current
                }
            }
        }
    }

    private fun load() {
        loadJob?.cancel()   // ketma-ket Refresh bosilsa, eski so'rov bekor qilinadi
        val onlyFavorites = favoritesOnly
        _state.value = MovieListUiState.Loading
        loadJob = viewModelScope.launch {
            try {
                val result = if (onlyFavorites) getFavoriteMovies() else getMovies()
                _state.value = MovieListUiState.Success(
                    movies = result.movies,
                    isFromCache = result.isFromCache,
                    favoritesOnly = onlyFavorites,
                )
            } catch (e: IOException) {
                _state.value = MovieListUiState.Error(
                    "Can't load movies. Check your connection and try again."
                )
            }
        }
    }
}
