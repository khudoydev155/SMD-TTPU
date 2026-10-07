package uz.ttpu.movieshelf.data.mapper

import uz.ttpu.movieshelf.data.remote.MovieDto
import uz.ttpu.movieshelf.domain.model.Movie

// IZOH: nima uchun mapper domenga emas, ma'lumotlar qatlamiga tegishli?
// MovieDto server JSON'ini aks ettiradi (releaseYear, score) - bu ma'lumotlar qatlamining ichki
// tafsiloti. Domen qatlami DTO'lar haqida BILMASLIGI kerak (Dependency Rule: ichki qatlam tashqisini
// bilmaydi). Shuning uchun DTO -> Movie moslashtirish chegarada, ya'ni ma'lumotlar qatlamida bajariladi.
// Server maydon nomini o'zgartirsa, faqat shu mapper o'zgaradi, o'zgarish domen va UI'ga yetmaydi.
fun MovieDto.toDomain(isFavorite: Boolean): Movie = Movie(
    id = id,
    title = title,
    year = releaseYear,
    rating = score,
    isFavorite = isFavorite,
)
