package uz.ttpu.weatherpatterns.mvi

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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uz.ttpu.weatherpatterns.di.WeatherModule
import uz.ttpu.weatherpatterns.ui.WeatherDisplay
import uz.ttpu.weatherpatterns.ui.theme.WeatherPatternsTheme

/**
 * Activity hosting the MVI pattern implementation.
 */
class MviActivity : ComponentActivity() {

    private val viewModel: WeatherMviViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WeatherMviViewModel(WeatherModule.weatherRepository) as T
            }
        }
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
                    MviScreen(
                        viewModel = viewModel,
                        onBackClicked = { finish() }
                    )
                }
            }
        }
    }
}

/**
 * Composable screen for MVI pattern.
 * Sends WeatherIntents to ViewModel and renders WeatherState.
 */
@Composable
fun MviScreen(
    viewModel: WeatherMviViewModel,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    WeatherDisplay(
        patternTitle = "MVI Pattern (Model-View-Intent)",
        cityNameQuery = state.cityNameQuery,
        onCityNameChanged = { newQuery ->
            viewModel.processIntent(WeatherIntent.CityQueryChanged(newQuery))
        },
        onSearchClicked = {
            viewModel.processIntent(WeatherIntent.FetchWeatherClicked)
        },
        isLoading = state.isLoading,
        weather = state.weather,
        errorMessage = state.errorMessage,
        onBackClicked = onBackClicked,
        modifier = modifier
    )
}
