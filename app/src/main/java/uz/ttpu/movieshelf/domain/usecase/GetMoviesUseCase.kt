package uz.ttpu.movieshelf.domain.usecase

import uz.ttpu.movieshelf.data.repository.MovieRepositoryImpl
import uz.ttpu.movieshelf.domain.model.Movie
import uz.ttpu.movieshelf.domain.model.MoviesResult

// IZOH (5-vazifa, ataylab qilingan nuqson): hozir GetMoviesUseCase (DOMEN) MovieRepositoryImpl
// (MA'LUMOTLAR qatlami) klassiga bog'liq: u uz.ttpu.movieshelf.data.repository.MovieRepositoryImpl
// ni import qiladi. Demak, domen (ichki qatlam) ma'lumotlar qatlamidan (tashqi qatlam) klass import
// qilyapti - bu Dependency Rule'ga zid: manba-kod bog'liqliklari faqat ICHKARIGA qarab yo'nalishi
// kerak, tashqi qatlam ichkini biladi, aksincha emas. Natijada domenni ma'lumotlar qatlamisiz
// kompilyatsiya qilib ham, repository'ni soxtasi bilan almashtirib ham bo'lmaydi.
// Tuzatish - 6-vazifada: Extract Interface (Dependency Inversion).
class GetMoviesUseCase(private val repository: MovieRepositoryImpl) {

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
