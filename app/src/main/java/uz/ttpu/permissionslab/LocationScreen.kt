package uz.ttpu.permissionslab

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

/**
 * 5-topshiriq. Bir martalik joylashuv (One-time location)
 *
 * Joylashuv ruxsatlarini RequestMultiplePermissions orqali so'rash va Map<String, Boolean> natijani o'qish.
 * Foydalanuvchi "Only this time" ni tanlagan bo'lishi mumkin. Ilova fonga o'tib qaytganda ruxsat
 * bekor qilingan bo'lishi mumkinligi sababli status har ON_RESUME da qayta tekshiriladi.
 */
@Composable
fun Task5LocationScreen() {
    val context = LocalContext.current

    var hasFineLocation by remember { mutableStateOf(checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) }
    var hasCoarseLocation by remember { mutableStateOf(checkPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)) }

    fun updateStatus() {
        hasFineLocation = checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        hasCoarseLocation = checkPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    // Har safar ekran qaytganda (ON_RESUME) joylashuv ruxsatini qayta tekshiramiz
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        updateStatus()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasFineLocation = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        hasCoarseLocation = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
    }

    val locationStatusText = when {
        hasFineLocation -> "Aniq joylashuv (Fine Location)"
        hasCoarseLocation -> "Taxminiy joylashuv (Coarse Location)"
        else -> "Ruxsat berilmagan (No Location Permission)"
    }

    val statusColor = when {
        hasFineLocation || hasCoarseLocation -> Color(0xFF2E7D32)
        else -> Color(0xFFC62828)
    }

    val cardBgColor = when {
        hasFineLocation || hasCoarseLocation -> Color(0xFFE8F5E9)
        else -> Color(0xFFFFEBEE)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "5-topshiriq: Joylashuv va Bir Martalik Ruxsat",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = cardBgColor),
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
                    text = "Joriy Joylashuv Holati:",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = locationStatusText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        launcher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                ) {
                    Text("Joylashuv Ruxsatini So'rash")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = "Bir martalik ruxsatnoma kuzatuv natijasi:\n" +
                        "Foydalanuvchi dialogda \"Only this time\" (Faqat bu safar) ni tanlasa, ruxsat faqat joriy seans uchun beriladi. " +
                        "Ilova background'ga o'tkazilib, biroz vaqtdan keyin qaytilsa, OS ruxsatni bekor qiladi. " +
                        "LifecycleEventEffect(ON_RESUME) har safar ekranga qaytilganda ruxsatni tekshirib, UI'ni to'g'ri yangilaydi.",
                fontSize = 13.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

private fun checkPermission(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}
