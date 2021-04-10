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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class MockForecastRepository : LocalForecastRepository, NetworkForecastDataSource {

    private val _forecast: MutableStateFlow<Forecast?> = MutableStateFlow(null)
    private val _location: MutableStateFlow<Location> =
        MutableStateFlow(MockDataGenerator.getRandomLocation())

    override fun getForecast(): Flow<Result<Forecast?>> = _forecast.map { Result.Success(it) }

    override fun getCurrentLocation(): Flow<Location?> = _location

    override suspend fun saveCurrentLocation(location: Location) {
        _location.value = location
    }

    override suspend fun saveForecast(forecast: Forecast) {
        _forecast.value = forecast
    }

    override suspend fun clearOldData(olderTime: Long) {}

    override suspend fun getForecast(latitude: Double, longitude: Double): Result<Forecast> {
        val forecast = MockDataGenerator.createForecast(_location.value)
        return Result.Success(forecast)
    }
}
