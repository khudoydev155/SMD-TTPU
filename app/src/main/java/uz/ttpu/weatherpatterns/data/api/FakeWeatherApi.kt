package uz.ttpu.weatherpatterns.data.api

import kotlinx.coroutines.delay
import uz.ttpu.weatherpatterns.data.model.Weather

/**
 * Fake implementation of WeatherApi simulating network delay and errors.
 */
class FakeWeatherApi : WeatherApi {

    private val mockDatabase = mapOf(
        "Tashkent" to Weather("Tashkent", 28.5, "Sunny", 40, 12.0),
        "London" to Weather("London", 15.0, "Rainy", 80, 20.0),
        "New York" to Weather("New York", 22.0, "Partly Cloudy", 55, 15.0),
        "Tokyo" to Weather("Tokyo", 19.5, "Clear", 60, 10.0)
    )

    var shouldSimulateError: Boolean = false

    override suspend fun fetchWeather(cityName: String): Weather {
        delay(1000) // Simulate network latency

        if (shouldSimulateError) {
            throw IllegalStateException("Simulated network error")
        }

        val trimmedName = cityName.trim()
        if (trimmedName.isEmpty()) {
            throw IllegalArgumentException("City name cannot be empty")
        }

        return mockDatabase.entries.firstOrNull {
            it.key.equals(trimmedName, ignoreCase = true)
        }?.value ?: Weather(
            cityName = trimmedName,
            temperatureCelsius = 20.0,
            condition = "Sunny",
            humidity = 50,
            windSpeedKmH = 10.0
        )
    }
}
