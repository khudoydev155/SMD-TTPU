package uz.ttpu.weatherpatterns.mvi

import uz.ttpu.weatherpatterns.data.model.Weather

/**
 * Single immutable state object representing the full UI state in MVI pattern.
 */
data class WeatherState(
    val cityNameQuery: String = "",
    val isLoading: Boolean = false,
    val weather: Weather? = null,
    val errorMessage: String? = null
)
