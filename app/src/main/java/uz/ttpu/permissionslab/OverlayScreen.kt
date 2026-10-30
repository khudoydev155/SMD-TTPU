package uz.ttpu.permissionslab

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

/**
 * 4-topshiriq. Special ruxsatnoma: overlay (SYSTEM_ALERT_WINDOW)
 *
 * Special ruxsatnomalar oddiy runtime dialog orqali so'ralmaydi, balki Sozlamalar
 * (Settings) ekraniga yo'naltirish orqali olinadi. Sozlamalardan ilovaga qaytganda
 * LifecycleEventEffect(Lifecycle.Event.ON_RESUME) yordamida ruxsat holati yangilanadi.
 */
@Composable
fun Task4OverlayScreen() {
    val context = LocalContext.current
    var canDrawOverlays by remember {
        mutableStateOf(Settings.canDrawOverlays(context))
    }

    // Sozlamalar ekranidan qaytganda ruxsat holatini ON_RESUME da yangilaymiz
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        canDrawOverlays = Settings.canDrawOverlays(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "4-topshiriq: Special Permission (Overlay)",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (canDrawOverlays) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
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
                    text = if (canDrawOverlays) "Overlay Ruxsati Berilgan" else "Overlay Ruxsati Yo'q",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (canDrawOverlays) Color(0xFF2E7D32) else Color(0xFFC62828)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Boshqa ilovalar ustidan chiqarish:",
                        fontSize = 15.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )

                    Switch(
                        checked = canDrawOverlays,
                        onCheckedChange = {
                            openOverlaySettings(context)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = "Tushuntirish: Switch bosilganda Settings.ACTION_MANAGE_OVERLAY_PERMISSION " +
                        "orqali Sozlamalar ochiladi. Sozlamalardan qaytganimizda LifecycleEventEffect(ON_RESUME) " +
                        "Settings.canDrawOverlays() ni qayta tekshiradi va Switch holatini haqiqiy ruxsatga moslaydi.",
                fontSize = 13.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

private fun openOverlaySettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:${context.packageName}")
    )
    context.startActivity(intent)
}
