package com.example.androiddevchallenge.fake

import com.example.androiddevchallenge.data.LocalForecastRepository
import com.example.androiddevchallenge.data.Result
import com.example.androiddevchallenge.data.TestUtil
import com.example.androiddevchallenge.model.Forecast
import com.example.androiddevchallenge.model.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLocalForecastRepository : LocalForecastRepository {

    var fail: Exception? = null
    var currentLocation: Location? = null

    override fun getForecast(): Flow<Result<Forecast?>> = flow {
        fail?.let {
            emit(Result.Error(it))
            return@flow
        }
        currentLocation?.let {
            val forecast = Forecast(
                location = it,
                hourly = TestUtil.createHourlyForecast(1616407200)
            )
            emit(Result.Success(forecast))
        } ?: emit(Result.Success(null))
    }

    override fun getCurrentLocation(): Flow<Location?> = flow {
        emit(currentLocation)
    }

    override suspend fun saveCurrentLocation(location: Location) {
        currentLocation = location
    }

    override suspend fun saveForecast(forecast: Forecast) {}

    override suspend fun clearOldData(olderTime: Long) {}
}