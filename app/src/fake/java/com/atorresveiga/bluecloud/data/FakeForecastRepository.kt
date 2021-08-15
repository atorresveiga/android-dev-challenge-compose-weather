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
package com.atorresveiga.bluecloud.data

import com.atorresveiga.bluecloud.model.Forecast
import com.atorresveiga.bluecloud.model.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock

class FakeForecastRepository : LocalForecastRepository, NetworkForecastDataSource {

    private val mutableForecast: MutableStateFlow<Forecast?> = MutableStateFlow(null)
    private val mutableLocation: MutableStateFlow<Location?> = MutableStateFlow(null)
    private val mutableLocations: MutableStateFlow<MutableList<Location>> =
        MutableStateFlow(mutableListOf())

    override fun getForecast(startTime: Long): Flow<Forecast?> = mutableForecast

    override fun getCurrentLocation(): Flow<Location?> = mutableLocation

    override suspend fun saveCurrentLocation(location: Location) {
        val locations = mutableLocations.value
        if (locations.contains(location)) {
            locations.remove(location)
        }
        locations.add(0, location)
        mutableLocation.value = location.copy(lastUpdated = Clock.System.now().epochSeconds)
        val forecast = getForecast(location)
        saveForecast(forecast)
    }

    override suspend fun saveForecast(forecast: Forecast) {
        mutableForecast.value = forecast
    }

    override suspend fun clearOldData(olderTime: Long) {}
    override fun getLastSelectedLocations(): Flow<List<Location>> = mutableLocations

    override suspend fun getForecast(location: Location): Forecast {
        return RandomFakeData.createForecast(
            location = location,
            startEpoch = location.lastUpdated
        )
    }
}
