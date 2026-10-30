package uz.ttpu.sideeffectslab

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Oddiy tashqi Analytics obyekti (Compose boshqarmaydigan kod)
 */
object Analytics {
    var userType: String = "Guest"
}

/**
 * 4-topshiriq. SideEffect va analytics
 * Maqsad: Compose state'ni tashqi obyektga xavfsiz uzatish.
 *
 * KOMMENTARIY VA TAHLIL:
 * 1. SideEffect QACHON ISHLAYDI?
 *    SideEffect har bir muvaffaqiyatli recomposition yakunlanib, o'zgarishlar ekranga chizilgach (commit bo'lgach)
 *    ishga tushadi. U hech qanday kalit qabul qilmaydi.
 *
 * 2. HOLAT O'ZGARMASA HAM ISHLASH SABABI:
 *    SideEffect ga kalit berilmagani sababli, composable har qanday sabab bilan (masalan, mutlaqo boshqa
 *    unrelatedCounter o'zgarganda) recompose bo'lsa, recomposition commit bo'lgani uchun SideEffect yana ishlaydi.
 *
 * 3. MA'RUZADAGI "ATTEMPT" MODELI:
 *    Compose recomposition jarayonini to'xtatishi, bekor qilishi (discard) yoki optimizatsiya tufayli tashlab
 *    yuborishi mumkin. To'g'ridan-to'g'ri composable tanasida yozilgan kod bekor qilingan urinishda ham ishlab
 *    ketishi mumkin; SideEffect esa FAQAT kompozitsiya muvaffaqiyatli commit bo'lgandagina ishlaydi.
 */

private const val TAG = "Task4SideEffect"

@Composable
fun Task4SideEffectScreen() {
    var userType by remember { mutableStateOf("Guest") }
    var unrelatedCounter by remember { mutableIntStateOf(0) }
    var sideEffectExecutionCount by remember { mutableIntStateOf(0) }

    // SideEffect orqali Compose state'ni tashqi Analytics obyektiga uzatamiz
    SideEffect {
        Analytics.userType = userType
        sideEffectExecutionCount++
        Log.d(TAG, "SideEffect ishladi! Jami soni: $sideEffectExecutionCount, Analytics.userType = ${Analytics.userType}")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "4-topshiriq: SideEffect va Tashqi Analytics",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Compose State qiymatini Compose boshqarmaydigan tashqi obyektlarga (masalan, Analytics yoki Logger) xavfsiz uzatish uchun SideEffect ishlatiladi.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Tashqi Analytics holati
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Tashqi Analytics obyekti holati:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Analytics.userType: ${Analytics.userType}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "SideEffect ishlagan soni: $sideEffectExecutionCount marta",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Boshqaruv tugmalari
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Eksperiment tugmalari:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                // 1-tugma: userType ni o'zgartiradi
                Button(
                    onClick = {
                        userType = when (userType) {
                            "Guest" -> "Standard User"
                            "Standard User" -> "Premium VIP"
                            else -> "Guest"
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("userType ni o'zgartirish: \"$userType\"")
                }

                // 2-tugma: userType ni o'zgartirmaydi, lekin recomposition chaqiradi
                OutlinedButton(
                    onClick = {
                        unrelatedCounter++
                        Log.d(TAG, "UnrelatedCounter oshirildi: $unrelatedCounter (userType o'zgarmadi)")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Boshqa state'ni yangilash (Hisoblagich: $unrelatedCounter)")
                }
            }
        }

        // Tushuntirish
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Kuzatuv va Tushuntirish:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "• \"Boshqa state'ni yangilash\" tugmasini bosganda userType o'zgarmasa ham, composable recompose bo'lgani sababli SideEffect yana ishlaydi.\n\n" +
                            "• Sababi: SideEffect hech qanday kalit qabul qilmaydi, u har bir muvaffaqiyatli chizishdan keyin chaqiriladi.\n\n" +
                            "• Attempt modeli: Agar composable chizilish jarayonida bekor qilinsa (discard), SideEffect ishlamaydi. Faqat muvaffaqiyatli commit bo'lgandagina tashqi obyektga yangi state uzatiladi.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
