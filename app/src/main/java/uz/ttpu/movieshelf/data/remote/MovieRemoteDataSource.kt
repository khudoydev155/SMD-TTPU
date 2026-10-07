package uz.ttpu.movieshelf.data.remote

data class MovieDto(
    val id: Int,
    val title: String,
    val releaseYear: Int,
    val score: Double,
)

// IZOH: nima uchun barcha ma'lumot manbai funksiyalari suspend?
// Tarmoq, fayl va lokal ma'lumotlar bazasi bilan ishlash kirish-chiqish (I/O) amallari: ular sekin
// bo'lishi mumkin. Agar ular oddiy (bloklovchi) funksiya bo'lsa, asosiy (UI) oqimda chaqirilganda
// ekranni qotirib qo'yadi. suspend funksiya esa oqimni BLOKLAMASDAN to'xtab turadi va natija
// tayyor bo'lganda davom etadi, shuning uchun I/O asosiy oqimni bloklamaydi.
interface MovieRemoteDataSource {
    suspend fun fetchMovies(): List<MovieDto>
}
