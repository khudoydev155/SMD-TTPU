package uz.ttpu.sideeffectslab

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/**
 * 3-topshiriq. Lifecycle logger
 * Maqsad: DisposableEffect bilan listener qo'shish va olib tashlash.
 *
 * KOMMENTARIY VA TAHLIL:
 * 1. NEGA TOZALASH (cleanup) KERAK?
 *    Agar observer lifecycle'ga qo'shilsa va composable tark etilganda olib tashlanmasa,
 *    lifecycle obyekti composable nusxasini xotirada ushlab qoladi. Bu xotira oqishiga (memory leak)
 *    va keraksiz hodisalarni doimiy ishlab turishiga sabab bo'ladi.
 *
 * 2. onDispose QOIDASI:
 *    DisposableEffect bloki har doim oxirgi qatorida onDispose { ... } chaqiruvi bilan tugashi SHART.
 *    Agar onDispose yozilmasa, Kotlin kompilyatori kompilyatsiya xatosi beradi.
 *
 * 3. TEKSHIRISH:
 *    Switch yoqilganda addObserver, o'chirilganda esa onDispose ichidagi removeObserver ishlaydi.
 *    Switch 5 marta yoqib-o'chirilganda, add va remove soni aynan teng bo'ladi (5 va 5).
 */

private const val TAG = "Task3Lifecycle"

@Composable
fun Task3LifecycleLoggerScreen() {
    var isObserverActive by remember { mutableStateOf(false) }
    var addCount by remember { mutableIntStateOf(0) }
    var removeCount by remember { mutableIntStateOf(0) }
    val logs = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "3-topshiriq: Lifecycle logger (DisposableEffect)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "DisposableEffect tashqi listener'larni qo'shish va Composable tark etilganda ularni onDispose bilan xavfsiz tozalash uchun xizmat qiladi.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Switch boshqaruvi
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Observer Composable",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isObserverActive) "Aktiv (Composition'da)" else "O'chiq (Dispose bo'lgan)",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Switch(
                    checked = isObserverActive,
                    onCheckedChange = { isObserverActive = it }
                )
            }
        }

        // Hisoblagichlar paneli
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("addObserver soni:", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = "$addCount ta",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("removeObserver soni:", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = "$removeCount ta",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Observer faol bo'lsa ekranga kiritiladi
        if (isObserverActive) {
            LifecycleObserverSubComponent(
                onAdd = {
                    addCount++
                    val msg = "[$addCount] addObserver chaqirildi"
                    logs.add(0, msg)
                    Log.d(TAG, msg)
                },
                onRemove = {
                    removeCount++
                    val msg = "[$removeCount] removeObserver (onDispose) chaqirildi"
                    logs.add(0, msg)
                    Log.d(TAG, msg)
                },
                onEvent = { event ->
                    val msg = "Lifecycle hodisasi: $event"
                    logs.add(0, msg)
                    Log.d(TAG, msg)
                }
            )
        }

        // Loglar ko'rinishi
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hodisalar jurnali (Logcat)",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    TextButton(onClick = { logs.clear() }) {
                        Text("Tozalash")
                    }
                }

                if (logs.isEmpty()) {
                    Text(
                        text = "Switch'ni yoqib-o'chirib ko'ring...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    logs.take(6).forEach { logLine ->
                        Text(
                            text = logLine,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * DisposableEffect ishlatuvchi alohida Composable.
 * Composition'ga kirganda observer qo'shadi, chiqib ketganda onDispose ishlaydi.
 */
@Composable
fun LifecycleObserverSubComponent(
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onEvent: (Lifecycle.Event) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        // 1. Observer yaratish va qo'shish
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START || event == Lifecycle.Event.ON_STOP || event == Lifecycle.Event.ON_RESUME) {
                onEvent(event)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onAdd()

        // 2. Tozalash (cleanup) - oxirgi qatorda bo'lishi SHART!
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            onRemove()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inversePrimary)
    ) {
        Text(
            text = "Observer hozir faol! Lifecycle hodisalari kuzatilmoqda.",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
