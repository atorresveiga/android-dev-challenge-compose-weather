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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

interface LocalForecastRepository {
    fun getForecast(startTime: Long): Flow<Forecast?>
    fun getCurrentLocation(): Flow<Location?>
    suspend fun saveCurrentLocation(location: Location)
    suspend fun saveForecast(forecast: Forecast)
    suspend fun clearOldData(olderTime: Long)
    fun getLastSelectedLocations(): Flow<List<Location>>
}

@OptIn(ExperimentalCoroutinesApi::class)
class LocalForecastRepositoryDefault(
    private val dataStoreManager: DataStoreManager,
    appDatabase: AppDatabase
) : LocalForecastRepository {

    private val forecastDAO = appDatabase.forecastDAO()

    @OptIn(ExperimentalTime::class)
    override fun getForecast(startTime: Long): Flow<Forecast?> {

        return dataStoreManager.currentLocation
            .combine(
                dataStoreManager.settings.map { it.dataSource }
                    .distinctUntilChanged()
            ) { location, dataSource -> Pair(location, dataSource) }
            .flatMapLatest { pair ->
                val currentLocation =
                    pair.first ?: return@flatMapLatest flow { emit(null) }

                val dataSource = pair.second

                val timeZone = TimeZone.of(currentLocation.timezoneId)
                val instant = Instant.fromEpochSeconds(startTime)
                val date = instant.toLocalDateTime(timeZone = timeZone)

                val startTimeWithoutMinutes = instant.minus(Duration.minutes((date.minute + 1)))
                val startTimeWithoutHours = startTimeWithoutMinutes.minus(Duration.hours(date.hour))

                forecastDAO.getHourlyForecastFrom(
                    latitude = currentLocation.latitude,
                    longitude = currentLocation.longitude,
                    startTime = startTimeWithoutMinutes.epochSeconds,
                    dataSource = dataSource.ordinal
                )
                    .combine(
                        forecastDAO.getDailyForecastFrom(
                            latitude = currentLocation.latitude,
                            longitude = currentLocation.longitude,
                            startTime = startTimeWithoutHours.epochSeconds,
                            dataSource = dataSource.ordinal
                        )
                    ) { hourly, daily ->

                        Forecast(
                            location = currentLocation,
                            hourly = hourly.map { it.toHourForecast() },
                            daily = daily.map { it.toDayForecast() },
                        )
                    }
            }
    }

    override fun getCurrentLocation() = dataStoreManager.currentLocation

    override suspend fun saveCurrentLocation(location: Location) {
        forecastDAO.saveLocation(location.toLocationEntity())
        dataStoreManager.setCurrentLocation(location)
    }

    override suspend fun saveForecast(forecast: Forecast) {
        val dataSource = dataStoreManager.settings.first().dataSource
        forecastDAO.saveHourlyForecast(
            forecast.hourly.map {
                it.toHourForecastEntity(
                    latitude = forecast.location.latitude,
                    longitude = forecast.location.longitude,
                    dataSource = dataSource.ordinal
                )
            }
        )
        forecastDAO.saveDailyForecast(
            forecast.daily.map {
                it.toDayForecastEntity(
                    latitude = forecast.location.latitude,
                    longitude = forecast.location.longitude,
                    dataSource = dataSource.ordinal
                )
            }
        )
        saveCurrentLocation(forecast.location)
    }

    override suspend fun clearOldData(olderTime: Long) = forecastDAO.clearOlderThan(olderTime)
    override fun getLastSelectedLocations(): Flow<List<Location>> =
        forecastDAO.getLocations().map { list -> list.map { entity -> entity.toLocation() } }
}
