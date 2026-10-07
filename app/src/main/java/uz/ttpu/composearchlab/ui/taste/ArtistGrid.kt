package uz.ttpu.composearchlab.ui.taste

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.ttpu.composearchlab.ui.theme.ComposeArchLabTheme

@Composable
fun ArtistGrid(
    artists: List<Artist>,
    likedIds: Set<Int>,
    onLikeClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
    ) {
        // key = id: element qo'shilganda/o'chirilganda Compose kartochkalarni to'g'ri moslashtiradi.
        items(artists, key = { it.id }) { artist ->
            ArtistCard(
                artist = artist,
                liked = artist.id in likedIds,
                onLikeClick = { onLikeClick(artist.id) },
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 640)
@Composable
private fun ArtistGridPreview() {
    ComposeArchLabTheme {
        ArtistGrid(
            artists = seedArtists,
            likedIds = setOf(1, 5),
            onLikeClick = {},
        )
    }
}
