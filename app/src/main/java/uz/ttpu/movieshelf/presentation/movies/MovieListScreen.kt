package uz.ttpu.movieshelf.presentation.movies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.ttpu.movieshelf.domain.model.Movie
import uz.ttpu.movieshelf.ui.theme.MovieShelfTheme

// Holatsiz ekran: holatning funksiyasi, shuning uchun to'rttala vaziyatni ilovani ishga tushirmasdan
// preview'da ko'rish mumkin.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    state: MovieListUiState,
    onFavoriteClick: (Int) -> Unit,
    onRefresh: () -> Unit,
    onFavoritesOnlyChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        MovieListUiState.Loading -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }

        is MovieListUiState.Error -> Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = state.message, textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onRefresh) {
                Text("Retry")
            }
        }

        is MovieListUiState.Success -> Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilterChip(
                    selected = state.favoritesOnly,
                    onClick = { onFavoritesOnlyChange(!state.favoritesOnly) },
                    label = { Text("Favorites only") },
                )
                TextButton(onClick = onRefresh) {
                    Text("Refresh")
                }
            }
            if (state.isFromCache) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                ) {
                    Text(
                        text = "Offline \u2014 showing saved data",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            if (state.movies.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No favorite movies yet")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.movies, key = { it.id }) { movie ->
                        MovieItem(
                            movie = movie,
                            onFavoriteClick = { onFavoriteClick(movie.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MovieItem(
    movie: Movie,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "${movie.year} \u00b7 Rating ${movie.rating}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = if (movie.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (movie.isFavorite) {
                    "Remove ${movie.title} from favorites"
                } else {
                    "Add ${movie.title} to favorites"
                },
            )
        }
    }
}

private val previewMovies = listOf(
    Movie(6, "Echoes of Khiva", 2022, 9.0, isFavorite = true),
    Movie(3, "Registan Rhapsody", 2018, 8.4),
    Movie(1, "The Silk Road Express", 2019, 8.4),
)

@Preview(showBackground = true)
@Composable
private fun MovieListLoadingPreview() {
    MovieShelfTheme {
        MovieListScreen(MovieListUiState.Loading, onFavoriteClick = {}, onRefresh = {}, onFavoritesOnlyChange = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun MovieListSuccessPreview() {
    MovieShelfTheme {
        MovieListScreen(
            MovieListUiState.Success(previewMovies, isFromCache = false),
            onFavoriteClick = {},
            onRefresh = {},
            onFavoritesOnlyChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MovieListCachedPreview() {
    MovieShelfTheme {
        MovieListScreen(
            MovieListUiState.Success(previewMovies, isFromCache = true),
            onFavoriteClick = {},
            onRefresh = {},
            onFavoritesOnlyChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MovieListErrorPreview() {
    MovieShelfTheme {
        MovieListScreen(
            MovieListUiState.Error("Can't load movies. Check your connection and try again."),
            onFavoriteClick = {},
            onRefresh = {},
            onFavoritesOnlyChange = {},
        )
    }
}
