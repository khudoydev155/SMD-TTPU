package uz.ttpu.weatherpatterns.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uz.ttpu.weatherpatterns.data.api.FakeWeatherApi
import uz.ttpu.weatherpatterns.data.api.WeatherApi
import uz.ttpu.weatherpatterns.data.repository.WeatherRepository
import uz.ttpu.weatherpatterns.data.repository.WeatherRepositoryImpl
import uz.ttpu.weatherpatterns.mvvm.WeatherViewModel

/**
 * Service Locator module providing repositories and ViewModel Factories.
 */
object WeatherModule {

    val weatherApi: WeatherApi by lazy {
        FakeWeatherApi()
    }

    val weatherRepository: WeatherRepository by lazy {
        WeatherRepositoryImpl(weatherApi)
    }

    fun createRepository(): WeatherRepository {
        return WeatherRepositoryImpl(FakeWeatherApi())
    }

    fun provideWeatherViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
                    return WeatherViewModel(weatherRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
