package uz.ttpu.movieshelf.data.repository

import java.io.IOException
import uz.ttpu.movieshelf.data.local.MovieLocalDataSource
import uz.ttpu.movieshelf.data.mapper.toDomain
import uz.ttpu.movieshelf.data.remote.MovieRemoteDataSource
import uz.ttpu.movieshelf.domain.model.MoviesResult

// Repository ikki manbani birlashtiradi va ziddiyatni hal qiladi:
// yangi (remote) ma'lumot ustun; tarmoq ishlamasa, kesh (cache) zaxira bo'ladi.
// Hozircha interfeysni amalga oshirmaydi - bu ataylab (6-vazifada tuzatiladi).
class MovieRepositoryImpl(
    private val remote: MovieRemoteDataSource,
    private val local: MovieLocalDataSource,
) {

    suspend fun getMovies(): MoviesResult {
        // Sevimlilar uchun haqiqat manbai - lokal xotira.
        val favoriteIds = local.getFavoriteIds()
        return try {
            val dtos = remote.fetchMovies()
            local.saveMovies(dtos)
            MoviesResult(
                movies = dtos.map { it.toDomain(isFavorite = it.id in favoriteIds) },
                isFromCache = false,
            )
        } catch (e: IOException) {
            // Faqat IOException ushlanadi; CancellationException hech qachon yutib yuborilmaydi.
            val cached = local.getCachedMovies() ?: throw e
            MoviesResult(
                movies = cached.map { it.toDomain(isFavorite = it.id in favoriteIds) },
                isFromCache = true,
            )
        }
    }

    suspend fun toggleFavorite(movieId: Int): Boolean {
        val newValue = movieId !in local.getFavoriteIds()
        local.setFavorite(movieId, newValue)
        return newValue
    }
}
