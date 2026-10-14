package uz.ttpu.weatherpatterns.data.repository

import kotlinx.coroutines.CancellationException
import uz.ttpu.weatherpatterns.data.api.WeatherApi
import uz.ttpu.weatherpatterns.data.model.Weather

/**
 * Repository interface for fetching weather data wrapped in a Result type.
 */
interface WeatherRepository {
    suspend fun getWeather(cityName: String): Result<Weather>
}

/**
 * Implementation of WeatherRepository.
 * Handles validation, safety, and proper Coroutine CancellationException handling.
 */
class WeatherRepositoryImpl(
    private val api: WeatherApi
) : WeatherRepository {

    override suspend fun getWeather(cityName: String): Result<Weather> {
        // TODO 1: Validate input city name
        if (cityName.isBlank()) {
            return Result.failure(IllegalArgumentException("City name cannot be empty"))
        }

        // TODO 2: Execute API call safely
        return try {
            val weather = api.fetchWeather(cityName)
            // TODO 3: Return success result
            Result.success(weather)
        } catch (e: CancellationException) {
            // Re-throw CancellationException to maintain coroutine structured concurrency (do NOT swallow)
            throw e
        } catch (e: Exception) {
            // Return failure result for standard errors
            Result.failure(e)
        }
    }
}
