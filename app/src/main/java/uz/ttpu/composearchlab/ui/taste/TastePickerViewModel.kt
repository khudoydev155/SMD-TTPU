package uz.ttpu.composearchlab.ui.taste

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TastePickerViewModel : ViewModel() {

    private val _state = MutableStateFlow(TastePickerState(artists = seedArtists))
    val state: StateFlow<TastePickerState> = _state.asStateFlow()

    // Tanlangan janrni qayta bosish filtrni tozalaydi (selectedGenre = null).
    fun onGenreClick(genre: String) {
        _state.update { current ->
            current.copy(selectedGenre = if (current.selectedGenre == genre) null else genre)
        }
    }

    // id yo'q bo'lsa qo'shadi, bor bo'lsa olib tashlaydi. Mavjud to'plam hech qachon o'zgartirilmaydi:
    // Set uchun + / - operatorlari yangi to'plam qaytaradi.
    fun onArtistLikeToggled(id: Int) {
        _state.update { current ->
            val newLikedIds = if (id in current.likedIds) current.likedIds - id else current.likedIds + id
            current.copy(likedIds = newLikedIds)
        }
    }
}
