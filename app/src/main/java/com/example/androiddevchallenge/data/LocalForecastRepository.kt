/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge.data

import com.example.androiddevchallenge.model.Forecast
import com.example.androiddevchallenge.model.Location
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

interface LocalForecastRepository {
    fun getForecast(): Flow<Result<Forecast?>>
    fun getCurrentLocation(): Flow<Location?>
    suspend fun saveCurrentLocation(location: Location)
    suspend fun saveForecast(forecast: Forecast)
    suspend fun clearOldData(olderTime: Long)
}

@OptIn(ExperimentalCoroutinesApi::class)
class LocalForecastRepositoryDefault(
    private val dataStoreManager: DataStoreManager,
    appDatabase: AppDatabase
) : LocalForecastRepository {

    private val hourForecastDAO = appDatabase.hourForecastDAO()

    override fun getForecast(): Flow<Result<Forecast?>> {
        var location: Location? = null
        return dataStoreManager.currentLocation
            .flatMapLatest { currentLocation ->
                location = currentLocation
                if (currentLocation != null) {
                    hourForecastDAO.getHourlyForecastFrom(
                        currentLocation.latitude,
                        currentLocation.longitude
                    )
                } else {
                    flow { Result.Success(null) }
                }
            }.map { hourlyDBList ->
                val currentLocation = location ?: return@map Result.Success(null)
                Result.Success(
                    Forecast(
                        location = currentLocation,
                        hourly = hourlyDBList.map { it.toHourForecast() }
                    )
                )
            }
    }

    override fun getCurrentLocation() = dataStoreManager.currentLocation

    override suspend fun saveCurrentLocation(location: Location) =
        dataStoreManager.saveCurrentLocation(location)

    override suspend fun saveForecast(forecast: Forecast) {
        dataStoreManager.saveCurrentLocation(forecast.location)
        hourForecastDAO.saveForecast(
            forecast.hourly.map {
                it.toHourForecastEntity(
                    latitude = forecast.location.latitude,
                    longitude = forecast.location.longitude
                )
            }
        )
    }

    override suspend fun clearOldData(olderTime: Long) = hourForecastDAO.clearOlderThan(olderTime)
}
