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
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * 6-topshiriq. Bonus: produceState
 * Maqsad: Suspend manbani Compose State'ga aylantirish.
 *
 * KOMMENTARIY VA TAHLIL:
 * 1. produceState NIMA QILADI?
 *    Suspend funksiya yoki tashqi asinxron oqimni Compose State<T> ga aylantiradi.
 *    U Composition'ga kirganda producer coroutine'ini boshlaydi va kalit (key1) o'zgarganda
 *    avvalgisini bekor qilib yangisini ishga tushiradi.
 *
 * 2. NEGA KICHIK HARF BILAN BOSHLANADI?
 *    Compose API yo'riqnomasiga ko'ra: agar Composable funksiya qiymat (State<T>) qaytarsa,
 *    u kichik harf (loadNetworkImage) bilan boshlanishi kerak; agar UI chizib Unit qaytarsa,
 *    Bosh harf (ImageCard) bilan boshlanadi.
 *
 * 3. STATE CONFLATION:
 *    Agar producer yangi qiymat o'rnatganda u avvalgisi bilan bir xil (equals == true) bo'lsa,
 *    Compose ortiqcha recomposition chaqirmaydi (conflation xususiyati).
 */

private const val TAG = "Task6ProduceState"

sealed interface ImageLoadResult {
    data object Loading : ImageLoadResult
    data class Success(val url: String, val message: String) : ImageLoadResult
    data class Error(val message: String) : ImageLoadResult
}

/**
 * Soxta repozitoriy: 500 ms kechikish bilan .png bo'lsa Success, aks holda Error qaytaradi.
 */
object FakeImageRepository {
    suspend fun load(url: String): ImageLoadResult {
        delay(500L) // 500 ms delay
        return if (url.endsWith(".png", ignoreCase = true)) {
            ImageLoadResult.Success(url, "Rasm muvaffaqiyatli yuklandi!")
        } else {
            ImageLoadResult.Error("Xatolik: Faqat .png formatdagi fayllar qabul qilinadi!")
        }
    }
}

/**
 * produceState yordamida suspend funksiyani Compose State ga aylantiruvchi Composable.
 */
@Composable
fun loadNetworkImage(url: String): State<ImageLoadResult> {
    return produceState<ImageLoadResult>(initialValue = ImageLoadResult.Loading, key1 = url) {
        Log.d(TAG, "Producer ishga tushdi: URL = $url")
        value = ImageLoadResult.Loading
        try {
            value = FakeImageRepository.load(url)
            Log.d(TAG, "Producer yangi qiymat chiqardi: $value")
        } finally {
            Log.d(TAG, "Producer finally bloki: bekor qilindi yoki yakunlandi (URL = $url)")
        }
    }
}

@Composable
fun Task6ProduceStateScreen() {
    var selectedUrl by remember { mutableStateOf("https://example.com/avatar.png") }
    var isComponentVisible by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "6-topshiriq (Bonus): produceState",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Suspend funksiyalarni Compose State'ga aylantirish. Kalit o'zgarganda producer qayta ishlaydi, composable chiqqanda bekor qilinadi.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Composable'ni yoqish / o'chirish (finally blokini kuzatish uchun)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Rasm komponentini ko'rsatish:")
                Switch(
                    checked = isComponentVisible,
                    onCheckedChange = { isComponentVisible = it }
                )
            }
        }

        // URL tanlash tugmalari
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Test URL manzilini tanlang:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { selectedUrl = "https://example.com/cat.png" },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("1. To'g'ri URL (.png) -> Success")
                }

                Button(
                    onClick = { selectedUrl = "https://example.com/document.pdf" },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("2. Noto'g'ri URL (.pdf) -> Error")
                }

                OutlinedButton(
                    onClick = {
                        val same = selectedUrl
                        selectedUrl = ""
                        selectedUrl = same
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("3. URL ni qayta yuklash")
                }
            }
        }

        Text(
            text = "Joriy URL: $selectedUrl",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )

        // Rasm yuklash natijasi
        if (isComponentVisible) {
            NetworkImageViewer(url = selectedUrl)
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = "Komponent Composition'dan chiqdi. Logcat'da finally xabari yozildi!",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
fun NetworkImageViewer(url: String) {
    val imageState by loadNetworkImage(url)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // when orqali uchala holatni UI da ko'rsatamiz
            when (val state = imageState) {
                is ImageLoadResult.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Yuklanmoqda (500 ms)...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                is ImageLoadResult.Success -> {
                    Text(
                        text = "✓",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Muvaffaqiyatli!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = state.url,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                is ImageLoadResult.Error -> {
                    Text(
                        text = "⚠",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Xatolik yuz berdi!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
