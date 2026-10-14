package uz.ttpu.weatherpatterns.mvp

import kotlinx.coroutines.Job
import uz.ttpu.weatherpatterns.data.model.Weather

/**
 * Contract defining the interface between View and Presenter in MVP pattern.
 */
interface WeatherContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun showWeather(weather: Weather)
        fun showError(message: String)
        fun updateCityQuery(query: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun onCityQueryChanged(newQuery: String)
        fun onFetchWeatherClicked(): Job
        fun onDestroy()
    }
}
