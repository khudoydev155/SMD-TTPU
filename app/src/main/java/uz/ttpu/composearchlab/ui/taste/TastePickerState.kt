package uz.ttpu.composearchlab.ui.taste

const val REQUIRED_LIKES = 3

data class Artist(val id: Int, val name: String, val genre: String)

val seedArtists = listOf(
    Artist(1, "Nilufar Skye", "Pop"),
    Artist(2, "Amir Neon", "Pop"),
    Artist(3, "Zarina Bloom", "Pop"),
    Artist(4, "Iron Caravan", "Rock"),
    Artist(5, "Qora Tun", "Rock"),
    Artist(6, "Steel Orchard", "Rock"),
    Artist(7, "Midnight Samarkand", "Jazz"),
    Artist(8, "Blue Registan", "Jazz"),
    Artist(9, "Saida Quartet", "Jazz"),
    Artist(10, "Silk Road Duo", "Folk"),
    Artist(11, "Navruz Ensemble", "Folk"),
    Artist(12, "Chor Minor", "Folk"),
)

// Butun ekran bitta o'zgarmas (immutable) holat: yagona haqiqat manbai (single source of truth).
// Boshqa holatdan hisoblab topish mumkin bo'lgan narsalar (genres, visibleArtists, likedCount,
// canContinue) SAQLANMAYDI, balki getter orqali hisoblanadi - shuning uchun ular hech qachon
// nosinxron bo'lib qolmaydi. selectedGenre == null "All" degani.
//
// IZOH: likedIds nima uchun faqat o'qiladigan Set<Int>, holatni nima uchun copy bilan yangilaymiz?
// UI holatni hech qachon to'g'ridan-to'g'ri o'zgartirmaydi: o'zgarish faqat hodisa orqali, bitta
// joyda (ViewModel) sodir bo'ladi. O'zgarmas (immutable) qiymatlar parallelizm (bir vaqtda
// o'zgartirish) muammolarining oldini oladi: bir oqimda o'qilayotgan holatni boshqa oqim "yarim"
// o'zgartira olmaydi. Har bir o'zgarish yangi nusxa (copy) beradi, Compose esa eski va yangi
// nusxani taqqoslab, nima o'zgarganini aniq biladi (ma'ruza: "Events in Compose").
data class TastePickerState(
    val artists: List<Artist>,
    val selectedGenre: String? = null,
    val likedIds: Set<Int> = emptySet(),
) {
    /** Takrorlanmas janrlar, ijrochilarda uchrash tartibida. */
    val genres: List<String>
        get() = artists.map { it.genre }.distinct()

    /** Barcha ijrochilar yoki faqat tanlangan janrdagilar. */
    val visibleArtists: List<Artist>
        get() = if (selectedGenre == null) artists else artists.filter { it.genre == selectedGenre }

    val likedCount: Int
        get() = likedIds.size

    val canContinue: Boolean
        get() = likedCount >= REQUIRED_LIKES
}
