package uz.ttpu.composearchlab.ui.taste

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

// StateFlow'ni State'ga aylantiradi. collectAsStateWithLifecycle() oqimni faqat ekran kamida
// STARTED holatida bo'lganda kuzatadi, shuning uchun fonda behuda ish bajarilmaydi.
@Composable
fun TastePickerRoute(viewModel: TastePickerViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TastePickerScreen(
        state = state,
        onGenreClick = viewModel::onGenreClick,
        onLikeClick = viewModel::onArtistLikeToggled,
        // Navigatsiya keyingi laboratoriyada qo'shiladi; hozircha faqat log.
        onContinueClick = { Log.d("TastePicker", "Continue clicked, liked=${state.likedIds}") },
        onSkipClick = { Log.d("TastePicker", "Later clicked") },
    )
}
