package uz.ttpu.movieshelf.domain.usecase

import uz.ttpu.movieshelf.domain.model.MoviesResult

// Mavjud GetMoviesUseCase'dan foydalanadi (saralash qoidasi ham o'sha yerdan keladi);
// repository'ga tegmaydi, faqat sevimli kinolarni qoldiradi.
class GetFavoriteMoviesUseCase(private val getMovies: GetMoviesUseCase) {

    suspend operator fun invoke(): MoviesResult {
        val result = getMovies()
        return result.copy(movies = result.movies.filter { it.isFavorite })
    }
}
