package uz.ttpu.weatherpatterns.mvp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import uz.ttpu.weatherpatterns.data.model.Weather
import uz.ttpu.weatherpatterns.di.WeatherModule
import uz.ttpu.weatherpatterns.ui.WeatherDisplay
import uz.ttpu.weatherpatterns.ui.theme.WeatherPatternsTheme

/**
 * Activity hosting the MVP pattern UI implementation.
 */
class MvpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherPatternsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MvpScreen(onBackClicked = { finish() })
                }
            }
        }
    }
}

/**
 * Composable screen for MVP pattern, implementing Passive View contract.
 */
@Composable
fun MvpScreen(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var cityNameQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var weatherState by remember { mutableStateOf<Weather?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val viewContract = remember {
        object : WeatherContract.View {
            override fun showLoading() {
                isLoading = true
                errorMessage = null
            }

            override fun hideLoading() {
                isLoading = false
            }

            override fun showWeather(weather: Weather) {
                weatherState = weather
                errorMessage = null
            }

            override fun showError(message: String) {
                errorMessage = message
                weatherState = null
            }

            override fun updateCityQuery(query: String) {
                cityNameQuery = query
                errorMessage = null
            }
        }
    }

    val presenter = remember {
        WeatherPresenter(WeatherModule.weatherRepository).apply {
            attachView(viewContract)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            presenter.onDestroy()
        }
    }

    WeatherDisplay(
        patternTitle = "MVP Pattern",
        cityNameQuery = cityNameQuery,
        onCityNameChanged = { query -> presenter.onCityQueryChanged(query) },
        onSearchClicked = { presenter.onFetchWeatherClicked() },
        isLoading = isLoading,
        weather = weatherState,
        errorMessage = errorMessage,
        onBackClicked = onBackClicked,
        modifier = modifier
    )
}
