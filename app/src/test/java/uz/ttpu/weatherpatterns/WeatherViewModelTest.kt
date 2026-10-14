package uz.ttpu.weatherpatterns

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uz.ttpu.weatherpatterns.data.api.FakeWeatherApi
import uz.ttpu.weatherpatterns.data.repository.WeatherRepositoryImpl
import uz.ttpu.weatherpatterns.mvvm.WeatherUiState
import uz.ttpu.weatherpatterns.mvvm.WeatherViewModel

/**
 * Unit tests for WeatherViewModel in MVVM pattern.
 */
class WeatherViewModelTest {

    private lateinit var fakeApi: FakeWeatherApi
    private lateinit var repository: WeatherRepositoryImpl
    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setUp() {
        fakeApi = FakeWeatherApi()
        repository = WeatherRepositoryImpl(fakeApi)
        viewModel = WeatherViewModel(
            repository = repository,
            customScope = CoroutineScope(Dispatchers.Unconfined)
        )
    }

    @Test
    fun fetchWeather_success_updatesUiStateToSuccess() = runBlocking {
        viewModel.onCityQueryChanged("Tashkent")
        viewModel.fetchWeather().join()

        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Success)
        val successState = state as WeatherUiState.Success
        assertEquals("Tashkent", successState.weather.cityName)
    }

    @Test
    fun fetchWeather_error_updatesUiStateToError() = runBlocking {
        fakeApi.shouldSimulateError = true
        viewModel.onCityQueryChanged("Tashkent")
        viewModel.fetchWeather().join()

        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Error)
        val errorState = state as WeatherUiState.Error
        assertEquals("Simulated network error", errorState.message)
    }

    @Test
    fun fetchWeather_blankCity_updatesUiStateToError() = runBlocking {
        viewModel.onCityQueryChanged("   ")
        viewModel.fetchWeather().join()

        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Error)
        val errorState = state as WeatherUiState.Error
        assertEquals("City name cannot be empty", errorState.message)
    }
}
