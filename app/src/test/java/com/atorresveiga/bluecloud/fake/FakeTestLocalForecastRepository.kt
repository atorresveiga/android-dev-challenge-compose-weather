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
package com.atorresveiga.bluecloud.fake

import com.atorresveiga.bluecloud.data.LocalForecastRepository
import com.atorresveiga.bluecloud.model.Forecast
import com.atorresveiga.bluecloud.model.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTestLocalForecastRepository : LocalForecastRepository {

    private var currentLocation: Location? = null

    override fun getForecast(startTime: Long): Flow<Forecast?> {
        TODO("Not yet implemented")
    }

    override fun getCurrentLocation(): Flow<Location?> = flow { emit(currentLocation) }

    override suspend fun saveCurrentLocation(location: Location) {
        currentLocation = location
    }

    override suspend fun saveForecast(forecast: Forecast) {
        TODO("Not yet implemented")
    }

    override suspend fun clearOldData(olderTime: Long) {
        TODO("Not yet implemented")
    }

    override fun getLastSelectedLocations(): Flow<List<Location>> {
        TODO("Not yet implemented")
    }
}
