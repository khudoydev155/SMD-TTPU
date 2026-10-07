package uz.ttpu.composearchlab.ui.taste

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.ttpu.composearchlab.ui.theme.ComposeArchLabTheme

// Pastki panel likedCount va canContinue'ning sof funksiyasi.
@Composable
fun TastePickerBottomBar(
    likedCount: Int,
    required: Int,
    canContinue: Boolean,
    onContinueClick: () -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { (likedCount.toFloat() / required).coerceIn(0f, 1f) },
                    modifier = Modifier.size(48.dp),
                )
                Text(
                    text = "$likedCount",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Text(
                text = "Pick $required artists you like",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onSkipClick) {
                Text("Later")
            }
            Button(onClick = onContinueClick, enabled = canContinue) {
                Text("Continue")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomBarZeroLikesPreview() {
    ComposeArchLabTheme {
        TastePickerBottomBar(0, REQUIRED_LIKES, false, onContinueClick = {}, onSkipClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomBarTwoLikesPreview() {
    ComposeArchLabTheme {
        TastePickerBottomBar(2, REQUIRED_LIKES, false, onContinueClick = {}, onSkipClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomBarThreeLikesPreview() {
    ComposeArchLabTheme {
        TastePickerBottomBar(3, REQUIRED_LIKES, true, onContinueClick = {}, onSkipClick = {})
    }
}
