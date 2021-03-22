package com.example.androiddevchallenge.data

import com.example.androiddevchallenge.model.Forecast
import com.example.androiddevchallenge.model.Location
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

interface LocalForecastRepository {
    fun getForecast(): Flow<Result<Forecast?>>
    fun getCurrentLocation(): Flow<Location?>
    suspend fun saveCurrentLocation(location: Location)
}

@OptIn(ExperimentalCoroutinesApi::class)
class LocalForecastRepositoryImplementation(
    private val dataStoreManager: DataStoreManager,
    private val appDatabase: AppDatabase
) : LocalForecastRepository {

    override fun getForecast(): Flow<Result<Forecast?>> {
        var location: Location? = null
        return dataStoreManager.currentLocation
            .flatMapLatest { currentLocation ->
                location = currentLocation
                if (currentLocation != null) {
                    appDatabase.hourForecastDAO()
                        .getHourlyForecastFrom(currentLocation.latitude, currentLocation.longitude)
                } else {
                    flow { Result.Success(null) }
                }
            }.map { hourlyDBList ->
                val currentLocation = location ?: return@map Result.Success(null)
                Result.Success(Forecast(
                    location = currentLocation,
                    hourly = hourlyDBList.map { it.toHourForecast() }
                ))
            }
    }

    override fun getCurrentLocation() = dataStoreManager.currentLocation

    override suspend fun saveCurrentLocation(location: Location) {
        dataStoreManager.saveCurrentLocation(location)
    }
}
