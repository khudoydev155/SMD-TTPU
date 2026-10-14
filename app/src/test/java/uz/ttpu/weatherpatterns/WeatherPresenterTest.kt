package uz.ttpu.weatherpatterns

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import uz.ttpu.weatherpatterns.data.api.FakeWeatherApi
import uz.ttpu.weatherpatterns.data.model.Weather
import uz.ttpu.weatherpatterns.data.repository.WeatherRepositoryImpl
import uz.ttpu.weatherpatterns.mvp.WeatherContract
import uz.ttpu.weatherpatterns.mvp.WeatherPresenter

/**
 * Unit tests for WeatherPresenter implementing MVP logic verification.
 */
class WeatherPresenterTest {

    private lateinit var fakeApi: FakeWeatherApi
    private lateinit var repository: WeatherRepositoryImpl
    private lateinit var presenter: WeatherPresenter
    private lateinit var fakeView: FakeWeatherView

    class FakeWeatherView : WeatherContract.View {
        var isLoadingDisplayed = false
        var lastDisplayedWeather: Weather? = null
        var lastErrorMessage: String? = null
        var queryText: String = ""

        override fun showLoading() {
            isLoadingDisplayed = true
        }

        override fun hideLoading() {
            isLoadingDisplayed = false
        }

        override fun showWeather(weather: Weather) {
            lastDisplayedWeather = weather
            lastErrorMessage = null
        }

        override fun showError(message: String) {
            lastErrorMessage = message
            lastDisplayedWeather = null
        }

        override fun updateCityQuery(query: String) {
            queryText = query
        }
    }

    @Before
    fun setUp() {
        fakeApi = FakeWeatherApi()
        repository = WeatherRepositoryImpl(fakeApi)
        presenter = WeatherPresenter(
            repository = repository,
            mainDispatcher = Dispatchers.Unconfined,
            ioDispatcher = Dispatchers.Unconfined
        )
        fakeView = FakeWeatherView()
        presenter.attachView(fakeView)
    }

    @After
    fun tearDown() {
        presenter.onDestroy()
    }

    // TODO 1: Implement test for successful weather fetch calling showWeather on View
    @Test
    fun fetchWeather_success_callsShowWeatherOnView() = runBlocking {
        presenter.onCityQueryChanged("Tashkent")
        presenter.onFetchWeatherClicked().join()

        assertFalse(fakeView.isLoadingDisplayed)
        assertNotNull(fakeView.lastDisplayedWeather)
        assertEquals("Tashkent", fakeView.lastDisplayedWeather?.cityName)
        assertNull(fakeView.lastErrorMessage)
    }

    // TODO 2: Implement test for error scenarios calling showError on View
    @Test
    fun fetchWeather_error_callsShowErrorOnView() = runBlocking {
        fakeApi.shouldSimulateError = true
        presenter.onCityQueryChanged("Tashkent")
        presenter.onFetchWeatherClicked().join()

        assertFalse(fakeView.isLoadingDisplayed)
        assertNull(fakeView.lastDisplayedWeather)
        assertEquals("Simulated network error", fakeView.lastErrorMessage)
    }

    @Test
    fun fetchWeather_blankCity_callsShowErrorOnView() = runBlocking {
        presenter.onCityQueryChanged("   ")
        presenter.onFetchWeatherClicked().join()

        assertEquals("City name cannot be empty", fakeView.lastErrorMessage)
    }
}
