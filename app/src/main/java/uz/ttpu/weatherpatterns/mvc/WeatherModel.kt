package uz.ttpu.weatherpatterns.mvc

import uz.ttpu.weatherpatterns.data.model.Weather

/**
 * Model component for MVC pattern.
 * Holds the weather application state.
 */
data class WeatherModel(
    val cityNameQuery: String = "",
    val isLoading: Boolean = false,
    val weather: Weather? = null,
    val errorMessage: String? = null
)
