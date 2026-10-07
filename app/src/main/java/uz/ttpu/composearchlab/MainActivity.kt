package uz.ttpu.composearchlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.ttpu.composearchlab.ui.signin.SignInRoute
import uz.ttpu.composearchlab.ui.theme.ComposeArchLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeArchLabTheme {
                SignInRoute()
            }
        }
    }
}

// Holatli (stateful) composable: holatga o'zi egalik qiladi.
@Composable
fun NameScreen(modifier: Modifier = Modifier) {
    // rememberSaveable holatni Bundle'ga yozadi, shuning uchun Activity aylantirishda qayta yaratilsa ham matn tiklanadi; remember esa faqat kompozitsiya xotirasida turadi va Activity bilan birga yo'qoladi.
    var name by rememberSaveable { mutableStateOf("") }

    Column(modifier.padding(24.dp)) {
        // Holat pastga (name), hodisa yuqoriga ({ name = it }) oqadi.
        NameField(name = name, onNameChange = { name = it })
        Text("Hello, $name!")
    }
}

// Holatsiz (stateless) composable: faqat parametrni ko'rsatadi, o'zgarishni lambda orqali xabar qiladi.
@Composable
fun NameField(
    name: String,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Name") },
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun NameFieldPreview() {
    ComposeArchLabTheme {
        NameField(name = "Amin", onNameChange = {})
    }
}
