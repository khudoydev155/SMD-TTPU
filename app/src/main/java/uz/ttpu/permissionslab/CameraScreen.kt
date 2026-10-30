package uz.ttpu.permissionslab

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

/**
 * 3-topshiriq. Kamera ekrani: uch holat
 *
 * Runtime ruxsatnoma holatlari:
 * 1. Granted: Ruxsat berilgan
 * 2. Rationale: Bir marta rad etilgan -> Tushuntirish berish kerak
 * 3. Denied / Settings: Ikki marta rad etilgan -> Sozlamalarni ochish
 */
@Composable
fun Task3CameraScreen() {
    val context = LocalContext.current
    val activity = context as? Activity

    // minSdk < 23 bo'lsa runtime permission kerak emas
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("API 23 (Marshmallow) dan past versiyalarda Kamera ruxsati o'rnatish vaqtida avtomatik berilgan.")
        }
        return
    }

    var isGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    var showRationale by remember {
        mutableStateOf(
            activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
            } ?: false
        )
    }

    var hasBeenDeniedOnce by remember { mutableStateOf(false) }

    fun updatePermissionStatus() {
        isGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        showRationale = activity?.let {
            ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
        } ?: false
    }

    // Settings'dan qaytganda holatni yangilash
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        updatePermissionStatus()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isGranted = granted
        if (!granted) {
            hasBeenDeniedOnce = true
            showRationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
            } ?: false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "3-topshiriq: Kamera Runtime Ruxsatnomasi",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        when {
            // 1-HOLAT: Ruxsat berilgan
            isGranted -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Kamera ruxsati berilgan!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Kameradan foydalanishingiz mumkin.",
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            // 2-HOLAT: Bir marta rad etilgan (Rationale ko'rsatiladi)
            showRationale -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nega Kamera ruxsati kerak?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ilovalar rasmga olish va skanerlash funksiyalari uchun kameraga kirish talab etiladi. " +
                                    "Iltimos, davom etish uchun ruxsat bering.",
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { launcher.launch(Manifest.permission.CAMERA) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
                        ) {
                            Text("Ruxsat berish")
                        }
                    }
                }
            }

            // 3-HOLAT: Ikki marta rad etilgan yoki hali so'ralmagan
            else -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (hasBeenDeniedOnce) "Ruxsat batamom rad etilgan" else "Kamera ruxsati yo'q",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC62828)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (hasBeenDeniedOnce)
                                "Ruxsat bir necha marta rad etildi. Tizim dialogi boshqa chiqmaydi. Ruxsatni Sozlamalar orqali yoqishingiz mumkin."
                            else
                                "Kameradan foydalanish uchun ruxsat so'rash tugmasini bosing.",
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (hasBeenDeniedOnce) {
                            Button(
                                onClick = { openAppSettings(context) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                            ) {
                                Text("Sozlamalarni ochish")
                            }
                        } else {
                            Button(
                                onClick = { launcher.launch(Manifest.permission.CAMERA) }
                            ) {
                                Text("Kamera ruxsatini so'rash")
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    context.startActivity(intent)
}
