package uz.ttpu.weatherpatterns.data.model

/**
 * Data class representing weather information for a specific city.
 */
data class Weather(
    val cityName: String,
    val temperatureCelsius: Double,
    val condition: String,
    val humidity: Int,
    val windSpeedKmH: Double
)
