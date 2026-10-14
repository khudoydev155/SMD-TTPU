package uz.ttpu.weatherpatterns.mvc

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
import uz.ttpu.weatherpatterns.di.WeatherModule
import uz.ttpu.weatherpatterns.ui.WeatherDisplay
import uz.ttpu.weatherpatterns.ui.theme.WeatherPatternsTheme

/**
 * Activity hosting the MVC pattern UI implementation.
 */
class MvcActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherPatternsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MvcScreen(onBackClicked = { finish() })
                }
            }
        }
    }
}

/**
 * Composable screen for MVC pattern, binding View state to Controller.
 */
@Composable
fun MvcScreen(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var modelState by remember { mutableStateOf(WeatherModel()) }

    val view = remember {
        object : WeatherView {
            // In MVC, the View receives the full Model state from the Controller and updates its visual representation.
            override fun render(model: WeatherModel) {
                modelState = model
            }
        }
    }

    val controller = remember {
        WeatherController(WeatherModule.weatherRepository).apply {
            attachView(view)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            controller.onDestroy()
        }
    }

    WeatherDisplay(
        patternTitle = "MVC Pattern",
        cityNameQuery = modelState.cityNameQuery,
        onCityNameChanged = { newQuery -> controller.onCityQueryChanged(newQuery) },
        onSearchClicked = { controller.onFetchWeatherClicked() },
        isLoading = modelState.isLoading,
        weather = modelState.weather,
        errorMessage = modelState.errorMessage,
        onBackClicked = onBackClicked,
        modifier = modifier
    )
}
