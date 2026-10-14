package uz.ttpu.weatherpatterns.mvvm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import uz.ttpu.weatherpatterns.di.WeatherModule
import uz.ttpu.weatherpatterns.ui.WeatherDisplay
import uz.ttpu.weatherpatterns.ui.theme.WeatherPatternsTheme

/**
 * Activity hosting the MVVM pattern implementation.
 */
class MvvmActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModels {
        WeatherModule.provideWeatherViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherPatternsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MvvmScreen(
                        viewModel = viewModel,
                        onBackClicked = { finish() }
                    )
                }
            }
        }
    }
}

/**
 * Composable screen for MVVM pattern, observing StateFlow from WeatherViewModel.
 */
@Composable
fun MvvmScreen(
    viewModel: WeatherViewModel,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cityNameQuery by viewModel.cityNameQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val isLoading = uiState is WeatherUiState.Loading
    val weather = (uiState as? WeatherUiState.Success)?.weather
    val errorMessage = (uiState as? WeatherUiState.Error)?.message

    // Using toStatusText() helper extension method
    val statusText = uiState.toStatusText()

    WeatherDisplay(
        patternTitle = "MVVM Pattern ($statusText)",
        cityNameQuery = cityNameQuery,
        onCityNameChanged = { query -> viewModel.onCityQueryChanged(query) },
        onSearchClicked = { viewModel.fetchWeather() },
        isLoading = isLoading,
        weather = weather,
        errorMessage = errorMessage,
        onBackClicked = onBackClicked,
        modifier = modifier
    )
}
