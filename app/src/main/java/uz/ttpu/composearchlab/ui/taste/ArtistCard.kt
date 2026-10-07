package uz.ttpu.composearchlab.ui.taste

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.ttpu.composearchlab.ui.theme.ComposeArchLabTheme

// Holatsiz kartochka: nimani chizishni parametr sifatida oladi, bosishni lambda orqali xabar qiladi.
@Composable
fun ArtistCard(
    artist: Artist,
    liked: Boolean,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(Color.hsv((artist.id * 47 % 360).toFloat(), 0.5f, 0.8f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = artist.name.first().uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
            )
        }
        Text(
            text = artist.name,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
        IconButton(onClick = onLikeClick) {
            Icon(
                imageVector = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (liked) "Unlike ${artist.name}" else "Like ${artist.name}",
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ArtistCardLikedPreview() {
    ComposeArchLabTheme {
        ArtistCard(artist = seedArtists[0], liked = true, onLikeClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun ArtistCardNotLikedPreview() {
    ComposeArchLabTheme {
        ArtistCard(artist = seedArtists[0], liked = false, onLikeClick = {})
    }
}
