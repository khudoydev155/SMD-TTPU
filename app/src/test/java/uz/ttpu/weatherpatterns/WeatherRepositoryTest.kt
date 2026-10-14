package uz.ttpu.weatherpatterns

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uz.ttpu.weatherpatterns.data.api.FakeWeatherApi
import uz.ttpu.weatherpatterns.data.api.WeatherApi
import uz.ttpu.weatherpatterns.data.model.Weather
import uz.ttpu.weatherpatterns.data.repository.WeatherRepository
import uz.ttpu.weatherpatterns.data.repository.WeatherRepositoryImpl

/**
 * Unit tests for WeatherRepository using JUnit4 and runBlocking.
 */
class WeatherRepositoryTest {

    private lateinit var fakeApi: FakeWeatherApi
    private lateinit var repository: WeatherRepository

    @Before
    fun setUp() {
        fakeApi = FakeWeatherApi()
        repository = WeatherRepositoryImpl(fakeApi)
    }

    @Test
    fun getWeather_withValidCity_returnsSuccessResult() = runBlocking {
        val result = repository.getWeather("Tashkent")
        assertTrue(result.isSuccess)
        val weather = result.getOrNull()
        assertEquals("Tashkent", weather?.cityName)
        assertEquals(28.5, weather?.temperatureCelsius ?: 0.0, 0.01)
    }

    @Test
    fun getWeather_withEmptyCity_returnsFailureResult() = runBlocking {
        val result = repository.getWeather("   ")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun getWeather_whenApiThrowsError_returnsFailureResult() = runBlocking {
        fakeApi.shouldSimulateError = true
        val result = repository.getWeather("Tashkent")
        assertTrue(result.isFailure)
        assertEquals("Simulated network error", result.exceptionOrNull()?.message)
    }

    @Test(expected = CancellationException::class)
    fun getWeather_whenCancelled_rethrowsCancellationException(): Unit = runBlocking {
        val cancellingApi = object : WeatherApi {
            override suspend fun fetchWeather(cityName: String): Weather {
                throw CancellationException("Coroutine cancelled")
            }
        }
        val customRepo = WeatherRepositoryImpl(cancellingApi)
        customRepo.getWeather("Tashkent")
    }
}
