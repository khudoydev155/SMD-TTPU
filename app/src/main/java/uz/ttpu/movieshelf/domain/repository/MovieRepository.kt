package uz.ttpu.movieshelf.domain.repository

import uz.ttpu.movieshelf.domain.model.MoviesResult

// C interfeysi domen qatlamiga tegishli: A (use case) C'ga bog'liq, B (MovieRepositoryImpl) C'ni
// amalga oshiradi. Endi bog'liqlik o'qi ma'lumotlar qatlamidan domen tomon, ICHKARIGA qaraydi.
interface MovieRepository {
    suspend fun getMovies(): MoviesResult
    suspend fun toggleFavorite(movieId: Int): Boolean
}
