package uz.ttpu.weatherpatterns.mvvm

import uz.ttpu.weatherpatterns.data.model.Weather

/**
 * Sealed interface representing UI states for MVVM pattern.
 */
sealed interface WeatherUiState {
    data object Idle : WeatherUiState
    data object Loading : WeatherUiState
    data class Success(val weather: Weather) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

/**
 * Helper extension function converting WeatherUiState to a status text message.
 */
fun WeatherUiState.toStatusText(): String = when (this) {
    WeatherUiState.Idle -> "Ready to search"
    WeatherUiState.Loading -> "Fetching weather..."
    is WeatherUiState.Success -> "Loaded: ${weather.cityName}"
    is WeatherUiState.Error -> "Error: $message"
}
