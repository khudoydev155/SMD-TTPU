package uz.ttpu.permissionslab

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

/**
 * 6-topshiriq (Bonus). Bildirishnoma ruxsatnomasi (POST_NOTIFICATIONS)
 *
 * Android 13 (API 33 - TIRAMISU) va undan yuqorida Bildirishnomalar uchun runtime permission talab etiladi.
 * API 33 dan past versiyalarda so'rov o'tkazib yuboriladi va ruxsat o'rnatish vaqtida berilgan hisoblanadi.
 */
@Composable
fun Task6NotificationScreen() {
    val context = LocalContext.current
    val channelId = "permissions_lab_channel"

    var isNotificationGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else {
                true // API 33 dan pastda avtomatik berilgan
            }
        )
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isNotificationGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isNotificationGranted = granted
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "6-topshiriq: Bildirishnoma Ruxsatnomasi (Bonus)",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isNotificationGranted) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isNotificationGranted) "Bildirishnoma Ruxsati Mavjud" else "Ruxsat Yo'q",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isNotificationGranted) Color(0xFF2E7D32) else Color(0xFFC62828)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isNotificationGranted) {
                    Button(
                        onClick = { launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }
                    ) {
                        Text("POST_NOTIFICATIONS Ruxsatini So'rash")
                    }
                } else {
                    Button(
                        onClick = {
                            sendTestNotification(context, channelId)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("Test Bildirishnomasini Yuborish")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = "Taqqoslama (API 32 vs API 34):\n" +
                        "• API 32 (Android 12) va undan pastida POST_NOTIFICATIONS runtime ruxsatnomasi yo'q, bildirishnoma darhol yuboriladi.\n" +
                        "• API 34 (Android 14) da runtime dialog orqali foydalanuvchidan ruxsat so'raladi va ruxsat berilmaguncha bildirishnoma ko'rsatilmaydi.",
                fontSize = 13.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

private fun sendTestNotification(context: Context, channelId: String) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "PermissionsLab Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "PermissionsLab test notification channel"
        }
        notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("PermissionsLab Test")
        .setContentText("Ushbu test bildirishnomasi muvaffaqiyatli yuborildi!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(1001, notification)
}
