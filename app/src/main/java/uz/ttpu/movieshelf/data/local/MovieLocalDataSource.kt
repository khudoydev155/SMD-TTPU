package uz.ttpu.movieshelf.data.local

import uz.ttpu.movieshelf.data.remote.MovieDto

// Funksiyalar suspend: sabab MovieRemoteDataSource'dagi izohda (I/O asosiy oqimni bloklamasligi kerak).
interface MovieLocalDataSource {
    suspend fun getCachedMovies(): List<MovieDto>?   // null = nothing cached yet
    suspend fun saveMovies(movies: List<MovieDto>)
    suspend fun getFavoriteIds(): Set<Int>
    suspend fun setFavorite(id: Int, favorite: Boolean)
}
