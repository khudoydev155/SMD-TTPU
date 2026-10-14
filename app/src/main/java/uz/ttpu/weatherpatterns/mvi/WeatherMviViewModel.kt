package uz.ttpu.weatherpatterns.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.ttpu.weatherpatterns.data.repository.WeatherRepository

/**
 * MVI ViewModel that processes WeatherIntents and emits immutable WeatherState.
 */
class WeatherMviViewModel(
    private val repository: WeatherRepository,
    private val customScope: CoroutineScope? = null
) : ViewModel() {

    private val activeScope: CoroutineScope
        get() = customScope ?: viewModelScope

    private val _state = MutableStateFlow(WeatherState())
    val state: StateFlow<WeatherState> = _state.asStateFlow()

    fun processIntent(intent: WeatherIntent): Job {
        return activeScope.launch {
            handleIntent(intent)?.join()
        }
    }

    private fun handleIntent(intent: WeatherIntent): Job? {
        return when (intent) {
            is WeatherIntent.CityQueryChanged -> {
                _state.value = _state.value.copy(
                    cityNameQuery = intent.newQuery,
                    errorMessage = null
                )
                null
            }
            is WeatherIntent.FetchWeatherClicked -> executeFetchWeather()
            WeatherIntent.ClearError -> {
                _state.value = _state.value.copy(errorMessage = null)
                null
            }
        }
    }

    private fun executeFetchWeather(): Job {
        val query = _state.value.cityNameQuery
        if (query.isBlank()) {
            _state.value = _state.value.copy(
                errorMessage = "City name cannot be empty",
                isLoading = false
            )
            return Job().apply { complete() }
        }

        _state.value = _state.value.copy(
            isLoading = true,
            errorMessage = null,
            weather = null
        )

        return activeScope.launch {
            val result = repository.getWeather(query)
            result.fold(
                onSuccess = { weather ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        weather = weather,
                        errorMessage = null
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        weather = null,
                        errorMessage = error.localizedMessage ?: "Failed to fetch weather"
                    )
                }
            )
        }
    }
}
