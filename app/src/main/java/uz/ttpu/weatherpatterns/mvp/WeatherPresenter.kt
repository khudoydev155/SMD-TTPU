package uz.ttpu.weatherpatterns.mvp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.ttpu.weatherpatterns.data.repository.WeatherRepository

/**
 * Presenter component for MVP pattern.
 * Manages presentation logic and updates Passive View via WeatherContract.View interface.
 */
class WeatherPresenter(
    private val repository: WeatherRepository,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : WeatherContract.Presenter {

    private var view: WeatherContract.View? = null
    private var currentCityQuery: String = ""
    private var presenterScope: CoroutineScope = CoroutineScope(mainDispatcher + SupervisorJob())

    override fun attachView(view: WeatherContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun onCityQueryChanged(newQuery: String) {
        currentCityQuery = newQuery
        view?.updateCityQuery(newQuery)
    }

    override fun onFetchWeatherClicked(): Job {
        if (currentCityQuery.isBlank()) {
            view?.showError("City name cannot be empty")
            return Job().apply { complete() }
        }

        view?.showLoading()

        return presenterScope.launch {
            val result = withContext(ioDispatcher) {
                repository.getWeather(currentCityQuery)
            }
            result.fold(
                onSuccess = { weather ->
                    view?.hideLoading()
                    view?.showWeather(weather)
                },
                onFailure = { error ->
                    view?.hideLoading()
                    view?.showError(error.localizedMessage ?: "Failed to fetch weather")
                }
            )
        }
    }

    override fun onDestroy() {
        detachView()
        presenterScope.cancel()
    }
}
