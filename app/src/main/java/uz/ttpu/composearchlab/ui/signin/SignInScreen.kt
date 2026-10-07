package uz.ttpu.composearchlab.ui.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import uz.ttpu.composearchlab.ui.theme.ComposeArchLabTheme

// Route: ViewModel'ni oladi va holatsiz SignInScreen'ga ulaydi.
@Composable
fun SignInRoute(viewModel: SignInViewModel = viewModel()) {
    val uiState = viewModel.uiState.value
    val snackbarHostState = remember { SnackbarHostState() }

    // IZOH (8-vazifa): aylantirishdan keyin AYNAN O'SHA Snackbar yana paydo bo'ladi. Sabab: ViewModel
    // konfiguratsiya o'zgarishidan omon qoladi va Error holatini saqlab turadi, yangidan yaratilgan
    // kompozitsiya esa LaunchedEffect'ni (uiState Error bo'lgani uchun) yana ishga tushiradi.
    // Snackbar bir martalik effekt, holat esa doimiy - ular o'rtasidagi nomuvofiqlik xatoga olib keladi.
    // Tuzatish 9-vazifada: "Snackbar ko'rsatildi" hodisasi bilan Error holatini iste'mol qilamiz.
    LaunchedEffect(uiState) {
        if (uiState is SignInUiState.Error) {
            snackbarHostState.showSnackbar(uiState.message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        SignInScreen(
            uiState = uiState,
            onSignIn = viewModel::onSignIn,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

// Holatsiz ekran: uiState'ning sof funksiyasi. Yozilgan matn (email, parol) - UI elementining
// holati, shuning uchun u shu yerda rememberSaveable bilan saqlanadi (ekran aylansa ham qoladi).
// Holat `when` TASHQARISIDA turadi, shu sabab InProgress -> Error o'tishida matn yo'qolmaydi.
@Composable
fun SignInScreen(
    uiState: SignInUiState,
    onSignIn: (email: String, password: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    when (uiState) {
        // SignedOut va Error faqat formani ko'rsatadi (Error uchun Snackbar 8-vazifada qo'shiladi).
        SignInUiState.SignedOut,
        is SignInUiState.Error -> SignInForm(
            email = email,
            onEmailChange = { email = it },
            password = password,
            onPasswordChange = { password = it },
            isLoading = false,
            onSignInClick = { onSignIn(email, password) },
            modifier = modifier,
        )

        // InProgress: forma (tugma o'chirilgan) va CircularProgressIndicator.
        SignInUiState.InProgress -> SignInForm(
            email = email,
            onEmailChange = { email = it },
            password = password,
            onPasswordChange = { password = it },
            isLoading = true,
            onSignInClick = { onSignIn(email, password) },
            modifier = modifier,
        )

        // SignedIn faqat salomlashuvni ko'rsatadi.
        is SignInUiState.SignedIn -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Welcome, ${uiState.email}",
                style = MaterialTheme.typography.headlineSmall,
            )
        }
    }
}

@Composable
private fun SignInForm(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
        )
        Button(
            onClick = onSignInClick,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Sign in")
        }
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

// To'rtta holat uchun to'rtta preview - emulyator ham, server ham kerak emas.
@Preview(showBackground = true)
@Composable
private fun SignInSignedOutPreview() {
    ComposeArchLabTheme {
        SignInScreen(uiState = SignInUiState.SignedOut, onSignIn = { _, _ -> })
    }
}

@Preview(showBackground = true)
@Composable
private fun SignInInProgressPreview() {
    ComposeArchLabTheme {
        SignInScreen(uiState = SignInUiState.InProgress, onSignIn = { _, _ -> })
    }
}

@Preview(showBackground = true)
@Composable
private fun SignInErrorPreview() {
    ComposeArchLabTheme {
        SignInScreen(
            uiState = SignInUiState.Error("Wrong email or password"),
            onSignIn = { _, _ -> },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SignInSignedInPreview() {
    ComposeArchLabTheme {
        SignInScreen(
            uiState = SignInUiState.SignedIn("amin@example.com"),
            onSignIn = { _, _ -> },
        )
    }
}
