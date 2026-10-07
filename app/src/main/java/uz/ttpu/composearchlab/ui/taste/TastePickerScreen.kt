package uz.ttpu.composearchlab.ui.taste

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.ttpu.composearchlab.ui.theme.ComposeArchLabTheme

// Holatsiz ekran: holatni oladi, hodisalarni lambda sifatida yuqoriga uzatadi.
@Composable
fun TastePickerScreen(
    state: TastePickerState,
    onGenreClick: (String) -> Unit,
    onLikeClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            GenreChips(
                genres = state.genres,
                selectedGenre = state.selectedGenre,
                onGenreClick = onGenreClick,
                modifier = Modifier.padding(vertical = 8.dp),
            )
            ArtistGrid(
                artists = state.visibleArtists,
                likedIds = state.likedIds,
                onLikeClick = onLikeClick,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TastePickerScreenPreview() {
    ComposeArchLabTheme {
        TastePickerScreen(
            state = TastePickerState(artists = seedArtists, likedIds = setOf(1, 2)),
            onGenreClick = {},
            onLikeClick = {},
        )
    }
}
