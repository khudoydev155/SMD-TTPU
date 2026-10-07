package uz.ttpu.movieshelf.data.local

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.ttpu.movieshelf.data.remote.MovieDto

// Doimiy lokal manba: sevimlilar SharedPreferences'da "favorites" kaliti ostida Set<String> ko'rinishida
// saqlanadi (ilova jarayoni to'xtasa ham yo'qolmaydi). Kinolar keshi esa private xotira maydonida turadi.
class SharedPrefsMovieLocalDataSource(context: Context) : MovieLocalDataSource {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var cached: List<MovieDto>? = null

    override suspend fun getCachedMovies(): List<MovieDto>? = cached

    override suspend fun saveMovies(movies: List<MovieDto>) {
        cached = movies
    }

    override suspend fun getFavoriteIds(): Set<Int> = withContext(Dispatchers.IO) {
        readFavorites().mapNotNull { it.toIntOrNull() }.toSet()
    }

    override suspend fun setFavorite(id: Int, favorite: Boolean) {
        withContext(Dispatchers.IO) {
            // getStringSet qaytargan to'plamni o'zgartirib bo'lmaydi: avval nusxa olamiz.
            val updated = readFavorites().toMutableSet()
            if (favorite) updated.add(id.toString()) else updated.remove(id.toString())
            prefs.edit().putStringSet(KEY_FAVORITES, updated).apply()
        }
    }

    private fun readFavorites(): Set<String> =
        prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()

    private companion object {
        const val PREFS_NAME = "movieshelf"
        const val KEY_FAVORITES = "favorites"
    }
}
