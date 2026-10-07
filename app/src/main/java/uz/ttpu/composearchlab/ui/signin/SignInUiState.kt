package uz.ttpu.composearchlab.ui.signin

// IZOH: nima uchun bu yerda sealed interface enum'dan mosroq?
// Holatlar turli MA'LUMOT tashiydi: Error xabar matnini (message), SignedIn esa email'ni tashiydi.
// Enum konstantalari esa faqat nom, ular har xil qiymat ko'tara olmaydi (barcha holatlar bir xil
// shaklda). Sealed interface esa har bir holatga o'z maydonlarini beradi, "yuklanish va xato bir
// vaqtda" kabi mumkin bo'lmagan kombinatsiyani ifodalab bo'lmaydi va when (uiState) ifodasi
// barcha holatlar ko'rib chiqilganini kompilyator darajasida tekshiradi (exhaustive).
sealed interface SignInUiState {
    data object SignedOut : SignInUiState
    data object InProgress : SignInUiState
    data class Error(val message: String) : SignInUiState
    data class SignedIn(val email: String) : SignInUiState
}
