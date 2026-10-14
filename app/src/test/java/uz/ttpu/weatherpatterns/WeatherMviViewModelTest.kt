package uz.ttpu.weatherpatterns

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import uz.ttpu.weatherpatterns.data.api.FakeWeatherApi
import uz.ttpu.weatherpatterns.data.repository.WeatherRepositoryImpl
import uz.ttpu.weatherpatterns.mvi.WeatherIntent
import uz.ttpu.weatherpatterns.mvi.WeatherMviViewModel

/**
 * Unit tests for WeatherMviViewModel in MVI pattern.
 */
class WeatherMviViewModelTest {

    private lateinit var fakeApi: FakeWeatherApi
    private lateinit var repository: WeatherRepositoryImpl
    private lateinit var viewModel: WeatherMviViewModel

    @Before
    fun setUp() {
        fakeApi = FakeWeatherApi()
        repository = WeatherRepositoryImpl(fakeApi)
        viewModel = WeatherMviViewModel(
            repository = repository,
            customScope = CoroutineScope(Dispatchers.Unconfined)
        )
    }

    @Test
    fun processIntent_cityQueryChanged_updatesState() = runBlocking {
        viewModel.processIntent(WeatherIntent.CityQueryChanged("Tashkent")).join()

        assertEquals("Tashkent", viewModel.state.value.cityNameQuery)
    }

    @Test
    fun processIntent_fetchWeather_success_updatesStateWithWeather() = runBlocking {
        viewModel.processIntent(WeatherIntent.CityQueryChanged("Tashkent")).join()
        viewModel.processIntent(WeatherIntent.FetchWeatherClicked).join()

        val currentState = viewModel.state.value
        assertFalse(currentState.isLoading)
        assertNotNull(currentState.weather)
        assertEquals("Tashkent", currentState.weather?.cityName)
    }

    @Test
    fun processIntent_fetchWeather_error_updatesStateWithError() = runBlocking {
        fakeApi.shouldSimulateError = true
        viewModel.processIntent(WeatherIntent.CityQueryChanged("Tashkent")).join()
        viewModel.processIntent(WeatherIntent.FetchWeatherClicked).join()

        val currentState = viewModel.state.value
        assertFalse(currentState.isLoading)
        assertEquals("Simulated network error", currentState.errorMessage)
    }
}
