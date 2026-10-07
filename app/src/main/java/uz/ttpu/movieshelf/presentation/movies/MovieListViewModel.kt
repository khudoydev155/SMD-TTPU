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
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.ToggleFavoriteUseCase

sealed interface MovieListUiState {
    data object Loading : MovieListUiState
    data class Success(
        val movies: List<Movie>,
        val isFromCache: Boolean,
    ) : MovieListUiState
    data class Error(val message: String) : MovieListUiState
}

// Taqdimot qatlami faqat DOMEN bilan gaplashadi: bu faylda data paketidan hech narsa import qilinmaydi.
class MovieListViewModel(
    private val getMovies: GetMoviesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<MovieListUiState>(MovieListUiState.Loading)
    val state: StateFlow<MovieListUiState> = _state.asStateFlow()

    private var loadJob: Job? = null

    init { load() }

    fun onRefresh() = load()

    fun onFavoriteClick(movieId: Int) {
        viewModelScope.launch {
            val isNowFavorite = toggleFavorite(movieId)
            // Faqat shu kinoning isFavorite belgisi o'zgaradi; tarmoqdan qayta yuklash yo'q.
            _state.update { current ->
                if (current is MovieListUiState.Success) {
                    current.copy(
                        movies = current.movies.map { movie ->
                            if (movie.id == movieId) movie.copy(isFavorite = isNowFavorite) else movie
                        }
                    )
                } else {
                    current
                }
            }
        }
    }

    private fun load() {
        loadJob?.cancel()   // ketma-ket Refresh bosilsa, eski so'rov bekor qilinadi
        _state.value = MovieListUiState.Loading
        loadJob = viewModelScope.launch {
            try {
                val result = getMovies()
                _state.value = MovieListUiState.Success(result.movies, result.isFromCache)
            } catch (e: IOException) {
                _state.value = MovieListUiState.Error(
                    "Can't load movies. Check your connection and try again."
                )
            }
        }
    }
}
