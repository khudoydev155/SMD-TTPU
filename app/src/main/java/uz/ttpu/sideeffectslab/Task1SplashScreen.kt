package uz.ttpu.sideeffectslab

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * 1-topshiriq. Splash ekrani
 * Maqsad: LaunchedEffect va rememberUpdatedState bilan eskirmagan callback'ni chaqirish.
 *
 * KOMMENTARIY VA TAHLIL:
 * 1. Variant A (Xato/Captured): LaunchedEffect(true) ichida onTimeout to'g'ridan-to'g'ri chaqirilgan.
 *    LaunchedEffect faqat bir marta boshlanadi va dastlabki 'version = 0' bo'lgan lambdani closure ichiga
 *    ushlab (capture qilib) oladi. 2 soniyadan so'ng version 4 bo'lsa ham, u eski v0 ni chaqiradi!
 *
 * 2. Variant B (To'g'ri/rememberUpdatedState): rememberUpdatedState(onTimeout) ishlatilgan.
 *    Ota composable har recomposition bo'lganda state qiymati yangilanadi, lekin LaunchedEffect(true)
 *    qayta ishga tushmaydi. 2 soniya o'tgach, currentOnTimeout eng so'nggi v4 lambdani xavfsiz chaqiradi.
 *
 * 3. NEGA LaunchedEffect(onTimeout) NOTO'G'RI?
 *    Agar LaunchedEffect(onTimeout) deb yozilsa, har 500 ms da yangi lambda kelganda kalit o'zgardi deb
 *    hisoblanadi va mavjud 2000 ms lik kutish (delay) bekor qilinib, qaytadan boshlanadi. Natijada timeout
 *    hech qachon tugamaydi va onTimeout umuman chaqirilmaydi!
 */

private const val TAG = "Task1Splash"

@Composable
fun Task1SplashScreen() {
    var isRunning by remember { mutableStateOf(false) }
    var version by remember { mutableIntStateOf(0) }
    var badResult by remember { mutableStateOf("Kutilmoqda...") }
    var goodResult by remember { mutableStateOf("Kutilmoqda...") }

    // Versiya hisoblagichi: har 500 ms da oshadi va yangi lambda yaratiladi
    LaunchedEffect(isRunning) {
        if (isRunning) {
            version = 0
            badResult = "Kutilmoqda (2 sek)..."
            goodResult = "Kutilmoqda (2 sek)..."
            Log.d(TAG, "Test boshlandi: version = 0")

            while (version < 8) {
                delay(500L)
                version++
                Log.d(TAG, "Version oshdi: v$version")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "1-topshiriq: Splash ekrani (rememberUpdatedState)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Ota composable har 500 ms da version hisoblagichini oshirib yangi lambda beradi. Splash esa 2 soniya kutib callback chaqiradi.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Boshqaruv tugmalari
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { isRunning = true },
                enabled = !isRunning
            ) {
                Text("▶ Testni boshlash")
            }

            OutlinedButton(
                onClick = {
                    isRunning = false
                    version = 0
                    badResult = "Kutilmoqda..."
                    goodResult = "Kutilmoqda..."
                }
            ) {
                Text("🔄 Tozalash")
            }
        }

        // Joriy versiya holati
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Joriy Ota Versiya (har 500 ms): v$version",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Har bir versiya yangi lambda nusxasi: onTimeout = { log(\"v\$version\") }",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (isRunning) {
            // Variant A va Variant B ekranda ishga tushadi
            LandingScreenBad(
                onTimeout = {
                    val msg = "v$version"
                    badResult = "$msg (Eski/Captured!)"
                    Log.d(TAG, "Variant A (Bad) chaqirdi: $msg")
                }
            )

            LandingScreenGood(
                onTimeout = {
                    val msg = "v$version"
                    goodResult = "$msg (Eng so'nggi!)"
                    Log.d(TAG, "Variant B (Good) chaqirdi: $msg")
                }
            )
        }

        // Natijalar ko'rinishi
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Variant A: Captured",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "To'g'ridan-to'g'ri lambda:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = badResult,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Variant B: UpdatedState",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "rememberUpdatedState:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = goodResult,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }

        // Tushuntirish kartasi
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Xulosa va Tekshirish Natijasi:",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "• Variant A (Captured): 2 soniya o'tsa ham dastlabki closure'da qolib ketgan 'v0' ni chaqirdi.\n" +
                            "• Variant B (rememberUpdatedState): 2 soniyadan so'ng o'sha paytdagi eng yangi 'v4' ni xavfsiz chaqirdi.\n" +
                            "• LaunchedEffect(onTimeout) xatosi: Agar onTimeout kalit qilinsa, har 500 ms da delay qayta boshlanib, 2 soniya hech qachon to'lmaydi.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * 1-variant: Lambdani to'g'ridan-to'g'ri ushlaydigan (Captured - Xato amaliyot)
 */
@Composable
fun LandingScreenBad(onTimeout: () -> Unit) {
    LaunchedEffect(true) {
        delay(2000L) // 2 soniya kutadi
        onTimeout()  // Boshlang'ich paytda ushlab olingan eski lambdani chaqiradi
    }
}

/**
 * 2-variant: rememberUpdatedState ishlatadigan (Tavsiya etilgan to'g'ri amaliyot)
 */
@Composable
fun LandingScreenGood(onTimeout: () -> Unit) {
    // currentOnTimeout har recomposition'da eng yangi lambdaga yangilanadi,
    // lekin LaunchedEffect qayta ishga tushmaydi!
    val currentOnTimeout by rememberUpdatedState(onTimeout)

    LaunchedEffect(true) {
        delay(2000L) // 2 soniya kutadi
        currentOnTimeout() // Eng so'nggi yangi lambdani chaqiradi
    }
}
