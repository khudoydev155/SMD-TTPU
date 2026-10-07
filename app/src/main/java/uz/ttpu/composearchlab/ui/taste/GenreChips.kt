package uz.ttpu.composearchlab.ui.taste

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.ttpu.composearchlab.ui.theme.ComposeArchLabTheme

// Chip bosilishi - "hodisa": u yuqoriga (ViewModel'ga) ketadi, yangi holat esa pastga qaytadi.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreChips(
    genres: List<String>,
    selectedGenre: String?,
    onGenreClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(genres, key = { it }) { genre ->
            FilterChip(
                selected = genre == selectedGenre,
                onClick = { onGenreClick(genre) },
                label = { Text(genre) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GenreChipsPreview() {
    ComposeArchLabTheme {
        GenreChips(
            genres = listOf("Pop", "Rock", "Jazz", "Folk"),
            selectedGenre = "Rock",
            onGenreClick = {},
        )
    }
}
