package uz.ttpu.movieshelf.domain.usecase

import uz.ttpu.movieshelf.domain.model.Movie
import uz.ttpu.movieshelf.domain.model.MoviesResult
import uz.ttpu.movieshelf.domain.repository.MovieRepository

// IZOH (5-vazifa, ataylab qilingan nuqson - 6-vazifada TUZATILGAN): avval GetMoviesUseCase (DOMEN) MovieRepositoryImpl
// (MA'LUMOTLAR qatlami) klassiga bog'liq: u data.repository paketidagi MovieRepositoryImpl'ni
// import qilgan edi. Demak, domen (ichki qatlam) ma'lumotlar qatlamidan (tashqi qatlam) klass import
// qilyapti - bu Dependency Rule'ga zid: manba-kod bog'liqliklari faqat ICHKARIGA qarab yo'nalishi
// kerak, tashqi qatlam ichkini biladi, aksincha emas. Natijada domenni ma'lumotlar qatlamisiz
// kompilyatsiya qilib ham, repository'ni soxtasi bilan almashtirib ham bo'lmaydi.
// Tuzatish (6-vazifa): Extract Interface - endi use case domen qatlamidagi MovieRepository
// interfeysiga bog'liq, MovieRepositoryImpl esa shu interfeysni amalga oshiradi (Dependency Inversion).
class GetMoviesUseCase(private val repository: MovieRepository) {

    // Qoida: eng yuqori reytingdan boshlab; teng reytingda sarlavha bo'yicha alifbo tartibida.
    suspend operator fun invoke(): MoviesResult {
        val result = repository.getMovies()
        return result.copy(
            movies = result.movies.sortedWith(
                compareByDescending<Movie> { it.rating }.thenBy { it.title }
            )
        )
    }
}
