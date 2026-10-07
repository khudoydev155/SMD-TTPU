package uz.ttpu.movieshelf

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import uz.ttpu.movieshelf.domain.model.Movie
import uz.ttpu.movieshelf.domain.model.MoviesResult
import uz.ttpu.movieshelf.domain.usecase.GetFavoriteMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase

class GetFavoriteMoviesUseCaseTest {

    @Test
    fun returns_only_favorite_movies_in_the_usual_order() = runBlocking<Unit> {
        val movies = listOf(
            Movie(1, "B", 2020, 8.0, isFavorite = true),
            Movie(2, "X", 2021, 9.5, isFavorite = false),
            Movie(3, "A", 2022, 8.0, isFavorite = true),
        )
        val useCase = GetFavoriteMoviesUseCase(
            GetMoviesUseCase(FakeMovieRepository(MoviesResult(movies, isFromCache = false)))
        )

        val result = useCase()

        assertEquals(listOf("A", "B"), result.movies.map { it.title })
    }

    @Test
    fun cache_flag_is_preserved() = runBlocking<Unit> {
        val movies = listOf(Movie(1, "A", 2020, 8.0, isFavorite = true))
        val useCase = GetFavoriteMoviesUseCase(
            GetMoviesUseCase(FakeMovieRepository(MoviesResult(movies, isFromCache = true)))
        )

        assertEquals(true, useCase().isFromCache)
    }
}
