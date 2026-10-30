package uz.ttpu.sideeffectslab

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * 5-topshiriq. Scroll: derivedStateOf va snapshotFlow
 * Maqsad: Recomposition'ni kamaytirish va Compose State'dan Flow olish.
 *
 * KOMMENTARIY VA TAHLIL:
 * 1. TO'G'RIDAN-TO'G'RI O'QISH:
 *    listState.firstVisibleItemIndex har bir element aylantirilganda (0, 1, 2, 3...) o'zgaradi.
 *    To'g'ridan-to'g'ri o'qilganda composable har bir yangi indeksda ortiqcha qayta chiziladi (recompose).
 *
 * 2. derivedStateOf ning AFZALLIGI:
 *    derivedStateOf hisob-kitob natijasini (bu yerda Boolean: firstVisibleItemIndex > 0) keshlaydi.
 *    Natija faqat false -> true yoki true -> false ga o'zgargandagina recomposition bo'ladi.
 *    Natijada ro'yxatni 1 dan 50 gacha aylantirsak ham recomposition soni 1-2 martadan oshmaydi!
 *
 * 3. snapshotFlow PIPELINE:
 *    snapshotFlow { listState.firstVisibleItemIndex }
 *        .map { index -> index > 0 }
 *        .distinctUntilChanged()
 *        .filter { it == true }
 *    Bu zanjir chegaradan (0-indeksdan) pastga o'tganda faqat bitta analytics hodisasi yuboradi,
 *    har bir element almashganda emas!
 */

private const val TAG = "Task5Scroll"

@Composable
fun Task5ScrollScreen() {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var analyticsEventCount by remember { mutableIntStateOf(0) }

    // snapshotFlow orqali ro'yxat chegaradan o'tganda analitika hodisasi yuboriladi
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { index -> index > 0 }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                analyticsEventCount++
                Log.d(TAG, "Analytics hodisasi yuborildi: Chegara (indeks 0) kesib o'tildi! Jami hodisalar: $analyticsEventCount")
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Yuqori ma'lumot va tahlil paneli
        Surface(
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "5-topshiriq: derivedStateOf va snapshotFlow",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Joriy 1-ko'rinuvchi indeks: ${listState.firstVisibleItemIndex} | Analytics hodisalari: $analyticsEventCount ta",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Ikkala variantni yonma-yon solishtirish
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        DirectReadScrollButton(
                            listState = listState,
                            onScrollToTop = {
                                scope.launch { listState.animateScrollToItem(0) }
                            }
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        DerivedStateScrollButton(
                            listState = listState,
                            onScrollToTop = {
                                scope.launch { listState.animateScrollToItem(0) }
                            }
                        )
                    }
                }
            }
        }

        // 100 ta elementdan iborat LazyColumn
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(100) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (index == 0) MaterialTheme.colorScheme.tertiaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (index == 0) "Element #0 (Boshlang'ich chegara)"
                            else "Element #$index",
                            fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal
                        )
                        if (index == 0) {
                            SuggestionChip(
                                onClick = {},
                                label = { Text("Chegara") }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 1-variant: Indeksni to'g'ridan-to'g'ri o'quvchi tugma (Ko'p recomposition)
 */
@Composable
fun DirectReadScrollButton(
    listState: LazyListState,
    onScrollToTop: () -> Unit
) {
    var recomposeCount by remember { mutableIntStateOf(0) }

    SideEffect {
        recomposeCount++
        Log.d(TAG, "DirectRead recomposition soni: $recomposeCount (indeks=${listState.firstVisibleItemIndex})")
    }

    // Har safar firstVisibleItemIndex o'zgarganda qayta hisoblanadi va recompose bo'ladi
    val showButton = listState.firstVisibleItemIndex > 0

    if (showButton) {
        Button(
            onClick = onScrollToTop,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("▲ Direct: $recomposeCount", fontSize = 12.sp)
        }
    } else {
        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Direct: 0 da yashirin",
                modifier = Modifier.padding(8.dp),
                fontSize = 11.sp
            )
        }
    }
}

/**
 * 2-variant: derivedStateOf yordamida tejamkor o'quvchi tugma (Minimal recomposition)
 */
@Composable
fun DerivedStateScrollButton(
    listState: LazyListState,
    onScrollToTop: () -> Unit
) {
    var recomposeCount by remember { mutableIntStateOf(0) }

    SideEffect {
        recomposeCount++
        Log.d(TAG, "derivedStateOf recomposition soni: $recomposeCount")
    }

    // derivedStateOf faqat firstVisibleItemIndex > 0 natijasi (true/false) o'zgargandagina recompose chaqiradi
    val showButton by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    if (showButton) {
        Button(
            onClick = onScrollToTop,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("▲ derived: $recomposeCount", fontSize = 12.sp)
        }
    } else {
        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "derived: 0 da yashirin",
                modifier = Modifier.padding(8.dp),
                fontSize = 11.sp
            )
        }
    }
}
