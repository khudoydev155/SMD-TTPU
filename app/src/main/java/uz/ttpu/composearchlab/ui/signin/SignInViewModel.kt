package uz.ttpu.composearchlab.ui.signin

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {

    // IZOH: _uiState private va o'zgaruvchan (MutableState), uiState esa faqat o'qiladigan State.
    // Shunday qilib holatni FAQAT ViewModel o'zgartira oladi (holat inkapsulyatsiyasi): UI qatlami
    // holatni to'g'ridan-to'g'ri yoza olmaydi, faqat hodisa (onSignIn) yuboradi. Holat bitta joyda
    // o'zgargani uchun UI izchil bo'ladi va xatolarni topish osonlashadi.
    private val _uiState = mutableStateOf<SignInUiState>(SignInUiState.SignedOut)
    val uiState: State<SignInUiState>
        get() = _uiState

    fun onSignIn(email: String, password: String) {
        // 1) Kirish jarayoni allaqachon ketayotgan bo'lsa, chaqiruvni e'tiborsiz qoldiramiz
        //    (tugma ikki marta bosilishidan himoya).
        if (_uiState.value is SignInUiState.InProgress) return

        // 2) Holatni InProgress'ga o'rnatamiz va viewModelScope'da korutina ishga tushiramiz.
        _uiState.value = SignInUiState.InProgress
        viewModelScope.launch {
            // 3) Tarmoq so'rovini simulyatsiya qilamiz.
            delay(1500)
            // 4) Parol faqat simulyatsiya uchun kodga yozilgan; haqiqiy ilovada bunday qilinmaydi.
            _uiState.value = if (password == "kotlin123") {
                SignInUiState.SignedIn(email)
            } else {
                SignInUiState.Error("Wrong email or password")
            }
        }
    }
}
