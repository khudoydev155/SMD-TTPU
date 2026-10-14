package uz.ttpu.weatherpatterns.mvi

/**
 * Sealed interface representing all user intents in MVI pattern.
 */
sealed interface WeatherIntent {
    data class CityQueryChanged(val newQuery: String) : WeatherIntent
    data object FetchWeatherClicked : WeatherIntent
    data object ClearError : WeatherIntent
}
