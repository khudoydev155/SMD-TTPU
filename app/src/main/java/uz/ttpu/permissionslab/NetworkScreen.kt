package uz.ttpu.permissionslab

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
 * 2-topshiriq: Normal ruxsatnomalar (INTERNET va ACCESS_NETWORK_STATE)
 *
 * KOMMENTARIY (Nega runtime dialog kerak emas):
 * Normal ruxsatnomalar (INTERNET, ACCESS_NETWORK_STATE) foydalanuvchi maxfiyligi yoki shaxsiy
 * ma'lumotlariga bevosita xavf tug'dirmagani uchun Android OS ularni ilova o'rnatilayotgan vaqtda
 * (install-time) avtomatik taqdim etadi. Shuning uchun kodda runtime dialog (checkSelfPermission) yozish shart emas.
 */

@Composable
fun Task2NetworkScreen() {
    val context = LocalContext.current
    var isConnected by remember { mutableStateOf(checkNetworkConnectivity(context)) }

    DisposableEffect(context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isConnected = true
            }

            override fun onLost(network: Network) {
                isConnected = checkNetworkConnectivity(context)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, networkCallback)

        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
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
            text = "2-topshiriq: Tarmoq Holati (Normal Permissions)",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isConnected) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
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
                    text = if (isConnected) "Online" else "Offline",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isConnected) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isConnected) "Qurilma internetga ulangan" else "Internet aloqasi yo'q",
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = "Izoh: INTERNET va ACCESS_NETWORK_STATE normal ruxsatnomalar bo'lib, " +
                        "ilova o'rnatilishi bilanoq OS tomonidan avtomatik beriladi. Shuning uchun runtime dialog talab qilinmaydi.",
                fontSize = 13.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

private fun checkNetworkConnectivity(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
