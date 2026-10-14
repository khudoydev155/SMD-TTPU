package uz.ttpu.weatherpatterns.mvc

/**
 * View interface in MVC pattern.
 * Defines contract for rendering the Model state.
 */
interface WeatherView {
    fun render(model: WeatherModel)
}
