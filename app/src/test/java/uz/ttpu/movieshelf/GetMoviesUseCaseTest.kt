package uz.ttpu.movieshelf

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import uz.ttpu.movieshelf.domain.model.Movie
import uz.ttpu.movieshelf.domain.model.MoviesResult
import uz.ttpu.movieshelf.domain.repository.MovieRepository
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase

// Stub: MovieRepository interfeys bo'lgani uchun use case'ni Android, tarmoq yoki bazasiz test qilamiz.
class FakeMovieRepository(
    private val result: MoviesResult,
) : MovieRepository {
    override suspend fun getMovies() = result
    override suspend fun toggleFavorite(movieId: Int) = true
}

class GetMoviesUseCaseTest {

    @Test
    fun movies_are_sorted_by_rating_desc_then_title() = runBlocking<Unit> {
        // Arrange: B (8.0), A (8.0), C (9.1)
        val b = Movie(id = 1, title = "B", year = 2020, rating = 8.0)
        val a = Movie(id = 2, title = "A", year = 2021, rating = 8.0)
        val c = Movie(id = 3, title = "C", year = 2022, rating = 9.1)
        val useCase = GetMoviesUseCase(
            FakeMovieRepository(MoviesResult(listOf(b, a, c), isFromCache = false))
        )

        // Act
        val result = useCase()

        // Assert: the titles come out as C, A, B
        assertEquals(listOf("C", "A", "B"), result.movies.map { it.title })
    }

    @Test
    fun is_from_cache_flag_is_passed_through() = runBlocking<Unit> {
        val movie = Movie(id = 1, title = "A", year = 2020, rating = 8.0)

        val fromCache = GetMoviesUseCase(
            FakeMovieRepository(MoviesResult(listOf(movie), isFromCache = true))
        )()
        val fresh = GetMoviesUseCase(
            FakeMovieRepository(MoviesResult(listOf(movie), isFromCache = false))
        )()

        assertEquals(true, fromCache.isFromCache)
        assertEquals(false, fresh.isFromCache)
    }
}
