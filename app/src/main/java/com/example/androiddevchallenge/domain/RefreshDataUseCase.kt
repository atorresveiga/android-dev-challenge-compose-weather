package com.example.androiddevchallenge.domain

import com.example.androiddevchallenge.data.LocalForecastRepository
import com.example.androiddevchallenge.data.NetworkForecastDataSource
import com.example.androiddevchallenge.data.Result
import com.example.androiddevchallenge.di.IoDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first

class RefreshDataUseCase @Inject constructor(
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
    private val localForecastRepository: LocalForecastRepository,
    private val networkForecastDataSource: NetworkForecastDataSource
) : SuspendUseCase<Unit, Unit>(ioDispatcher) {
    override suspend fun execute(parameters: Unit) {
        val location = localForecastRepository.getCurrentLocation().first()
        location?.let { currentLocation ->
            // Get updated forecast from the network
            val result = networkForecastDataSource.getForecast(
                latitude = currentLocation.latitude,
                longitude = currentLocation.longitude
            )
            if (result is Result.Success) {
                val forecast = result.data
                // Save forecast in the device
                localForecastRepository.saveForecast(forecast)
            }
        }
    }
}