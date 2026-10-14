package uz.ttpu.weatherpatterns.mvc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.ttpu.weatherpatterns.data.repository.WeatherRepository

/**
 * Controller component for MVC pattern.
 * Receives user input from View, updates Model state, and triggers View rendering.
 */
class WeatherController(
    private val repository: WeatherRepository,
    private var view: WeatherView? = null
) {
    private val controllerScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var model = WeatherModel()

    fun attachView(view: WeatherView) {
        this.view = view
        view.render(model)
    }

    fun detachView() {
        this.view = null
    }

    // TODO 1: Process user input for city query change and update View
    fun onCityQueryChanged(newQuery: String) {
        model = model.copy(cityNameQuery = newQuery, errorMessage = null)
        view?.render(model)
    }

    // TODO 2: Execute asynchronous weather fetch, update Model, and invoke view.render()
    fun onFetchWeatherClicked() {
        if (model.cityNameQuery.isBlank()) {
            model = model.copy(errorMessage = "City name cannot be empty")
            view?.render(model)
            return
        }

        model = model.copy(isLoading = true, errorMessage = null, weather = null)
        view?.render(model)

        controllerScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.getWeather(model.cityNameQuery)
            }
            result.fold(
                onSuccess = { weather ->
                    model = model.copy(
                        isLoading = false,
                        weather = weather,
                        errorMessage = null
                    )
                    view?.render(model)
                },
                onFailure = { error ->
                    model = model.copy(
                        isLoading = false,
                        weather = null,
                        errorMessage = error.localizedMessage ?: "Failed to fetch weather"
                    )
                    view?.render(model)
                }
            )
        }
    }

    // TODO 3: Clean up resources and cancel active coroutine jobs when Controller is destroyed
    fun onDestroy() {
        detachView()
        controllerScope.cancel()
    }
}
