package uz.ttpu.weatherpatterns.data.api

import uz.ttpu.weatherpatterns.data.model.Weather

/**
 * Interface defining API calls for fetching weather data.
 */
interface WeatherApi {
    suspend fun fetchWeather(cityName: String): Weather
}
