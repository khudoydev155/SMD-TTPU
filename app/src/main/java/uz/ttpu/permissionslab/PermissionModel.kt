package uz.ttpu.permissionslab

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 1-topshiriq. Kotlin modeli: rad etish holatlari ("Denied twice" modeli)
 *
 * Denied twice modelini ifodalovchi klass.
 * - granted: ruxsat berilganligi
 * - rationale: tushuntirish berish kerakligi (faqat 1 marta rad etilganda true)
 * - dialogShows: muloqot oynasi (dialog) ko'rsatilish-ko'rsatilmasligi
 */
class PermissionModel {
    var granted: Boolean = false
        private set
    var rationale: Boolean = false
        private set
    var dialogShows: Boolean = true
        private set

    var deniedCount: Int = 0
        private set

    fun request(userAllows: Boolean): String {
        if (granted) {
            dialogShows = false
            rationale = false
            return "Ruxsat allaqachon berilgan."
        }

        if (deniedCount >= 2) {
            dialogShows = false
            rationale = false
            return "dialog yo'q, darhol rad"
        }

        dialogShows = true

        return if (userAllows) {
            granted = true
            rationale = false
            deniedCount = 0
            "Foydalanuvchi ruxsat berdi (granted = true)."
        } else {
            granted = false
            deniedCount++
            if (deniedCount == 1) {
                rationale = true
                "1-marta rad etildi (rationale = true)."
            } else {
                rationale = false
                "2-marta rad etildi (Don't ask again)."
            }
        }
    }

    fun reset() {
        granted = false
        rationale = false
        dialogShows = true
        deniedCount = 0
    }
}

fun main() {
    val model = PermissionModel()
    println("=== 1-topshiriq: Denied Twice Model Test ===")

    println("\n1-so'rov (rad):")
    println(model.request(userAllows = false))
    println("granted=${model.granted}, rationale=${model.rationale}, dialogShows=${model.dialogShows}")

    println("\n2-so'rov (rad):")
    println(model.request(userAllows = false))
    println("granted=${model.granted}, rationale=${model.rationale}, dialogShows=${model.dialogShows}")

    println("\n3-so'rov (so'rov):")
    println(model.request(userAllows = false))
    println("granted=${model.granted}, rationale=${model.rationale}, dialogShows=${model.dialogShows}")

    println("\n4-so'rov (ruxsat):")
    println(model.request(userAllows = true))
    println("granted=${model.granted}, rationale=${model.rationale}, dialogShows=${model.dialogShows}")
}

@Composable
fun Task1ModelScreen() {
    val model = remember { PermissionModel() }
    var logList by remember { mutableStateOf(listOf<String>()) }
    var currentGranted by remember { mutableStateOf(model.granted) }
    var currentRationale by remember { mutableStateOf(model.rationale) }
    var currentDialogShows by remember { mutableStateOf(model.dialogShows) }
    var deniedCount by remember { mutableStateOf(model.deniedCount) }

    fun updateState() {
        currentGranted = model.granted
        currentRationale = model.rationale
        currentDialogShows = model.dialogShows
        deniedCount = model.deniedCount
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "1-topshiriq: PermissionModel Simulyatsiyasi",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "granted: $currentGranted", fontWeight = FontWeight.SemiBold)
                Text(text = "rationale: $currentRationale", fontWeight = FontWeight.SemiBold)
                Text(text = "dialogShows: $currentDialogShows", fontWeight = FontWeight.SemiBold)
                Text(text = "Rad etishlar soni: $deniedCount", fontWeight = FontWeight.SemiBold)
            }
        }

        Text(text = "Amallarni bajaring:", fontWeight = FontWeight.Medium)

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    val res = model.request(userAllows = false)
                    logList = logList + "Rad etildi -> $res | granted=${model.granted}, rationale=${model.rationale}, dialogShows=${model.dialogShows}"
                    updateState()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Rad etish")
            }

            Button(
                onClick = {
                    val res = model.request(userAllows = true)
                    logList = logList + "Ruxsat berildi -> $res | granted=${model.granted}, rationale=${model.rationale}, dialogShows=${model.dialogShows}"
                    updateState()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Ruxsat berish")
            }
        }

        OutlinedButton(
            onClick = {
                model.reset()
                logList = emptyList()
                updateState()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Modelni Qayta Tiklash (Reset)")
        }

        Divider()

        Text(text = "Natijalar tarixi (Log):", fontWeight = FontWeight.Bold)

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            logList.forEachIndexed { index, log ->
                Text(
                    text = "${index + 1}. $log",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}
