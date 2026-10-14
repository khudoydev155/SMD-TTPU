package uz.ttpu.weatherpatterns.mvvm

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
 * ViewModel component for MVVM pattern.
 * Manages presentation state using StateFlow and survives configuration changes.
 */
class WeatherViewModel(
    private val repository: WeatherRepository,
    private val customScope: CoroutineScope? = null
) : ViewModel() {

    private val activeScope: CoroutineScope
        get() = customScope ?: viewModelScope

    private val _cityNameQuery = MutableStateFlow("")
    val cityNameQuery: StateFlow<String> = _cityNameQuery.asStateFlow()

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    // TODO 1: Process city query change and reset error state if needed
    fun onCityQueryChanged(newQuery: String) {
        _cityNameQuery.value = newQuery
        if (_uiState.value is WeatherUiState.Error) {
            _uiState.value = WeatherUiState.Idle
        }
    }

    // TODO 2: Execute weather search asynchronously using ViewModel scope and emit StateFlow updates
    fun fetchWeather(): Job {
        val query = _cityNameQuery.value
        if (query.isBlank()) {
            _uiState.value = WeatherUiState.Error("City name cannot be empty")
            return Job().apply { complete() }
        }

        _uiState.value = WeatherUiState.Loading

        return activeScope.launch {
            val result = repository.getWeather(query)
            result.fold(
                onSuccess = { weather ->
                    _uiState.value = WeatherUiState.Success(weather)
                },
                onFailure = { error ->
                    _uiState.value = WeatherUiState.Error(
                        error.localizedMessage ?: "Failed to fetch weather"
                    )
                }
            )
        }
    }
}
