package uz.ttpu.sideeffectslab

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * 2-topshiriq. Snackbar tugmasi
 * Maqsad: rememberCoroutineScope bilan callback ichidan coroutine ishga tushirish.
 *
 * KOMMENTARIY VA SAVOLLAR JAVOBI:
 * 1. NEGA LaunchedEffect BU YERDA ISHLAMAYDI?
 *    LaunchedEffect - bu @Composable funksiya. Compose qoidasiga ko'ra @Composable funksiyalarni
 *    faqat boshqa @Composable doirasida chaqirish mumkin. Button(onClick = { ... }) esa oddiy
 *    Kotlin callback lambdasi bo'lib, Composable konteksti emas. Shu sababli onClick ichiga
 *    LaunchedEffect yozilsa, kompilyator xatolik beradi ("@Composable invocations can only happen
 *    from the context of a @Composable function").
 *
 * 2. rememberCoroutineScope NIMANI QAYTARADI VA UMRI QANDAY?
 *    U joriy Composition nuqtasiga bog'langan CoroutineScope qaytaradi.
 *    Ushbu composable ekrandan chiqib ketganda (tark etganda), scope avtomatik ravishda bekor qilinadi (cancel).
 *    Shu bois fonda keraksiz coroutine'lar qolib ketmaydi va ilova qulamaydi (leak yoki crash bo'lmaydi).
 *
 * 3. IKKINCHI MARTA BOSISH KUZATUVI:
 *    Snackbar ko'rinib turgan paytda tugma ikkinchi marta bosilsa, SnackbarHostState yangi xabarni
 *    ko'rsatish uchun avvalgi ko'rinib turgan xabarni bekor qiladi (dismiss) va darhol yangisini chiqaradi.
 */

private const val TAG = "Task2Snackbar"

@Composable
fun Task2SnackbarScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var clickCount by remember { mutableIntStateOf(0) }
    var lastActionLog by remember { mutableStateOf("Hali xabar chiqarilmadi") }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "2-topshiriq: Snackbar va rememberCoroutineScope",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Foydalanuvchi tugmani bosganda suspend funksiya (showSnackbar) ni ishga tushirish uchun rememberCoroutineScope ishlatiladi.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Statistika:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(text = "Tugma bosilish soni: $clickCount")
                    Text(text = "Oxirgi hodisa: $lastActionLog")
                }
            }

            // Tugma: scope.launch yordamida snackbar chaqiradi
            Button(
                onClick = {
                    clickCount++
                    val currentMessage = "Snackbar #$clickCount ko'rsatildi!"
                    lastActionLog = currentMessage
                    Log.d(TAG, "Tugma bosildi (#$clickCount), scope.launch boshlandi")

                    // CALLBACK ICHIDAN COROUTINE ISHGA TUSHIRISH:
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = currentMessage,
                            actionLabel = "OK",
                            duration = SnackbarDuration.Short
                        )
                        Log.d(TAG, "Snackbar natijasi: $result (#$clickCount)")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🔔 Snackbar ko'rsatish (scope.launch)")
            }

            // Amaliy kuzatuv va qoidalar
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Amaliy kuzatuvlar va tahlil:",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )

                    Text(
                        text = "1. Boshqa ekranga o'tib qaytish: Snackbar chiqib turganda yuqoridagi boshqa tabga o'tib qayting. Ilova qulamaydi, chunki rememberCoroutineScope ekran yopilishi bilan bekor qilinadi.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "2. Ketma-ket ikki marta bosish: Snackbar yo'qolmasdan yana tugmani bosing. Avvalgi xabar darhol bekor bo'lib, o'rniga eng yangi xabar chiqadi.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "3. Qoida: Composable paydo bo'lganda boshlanadigan ish uchun -> LaunchedEffect; Foydalanuvchi harakati (click) uchun -> rememberCoroutineScope.",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
