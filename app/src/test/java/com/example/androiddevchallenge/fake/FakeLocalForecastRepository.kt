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
package com.example.androiddevchallenge.fake

import com.example.androiddevchallenge.data.LocalForecastRepository
import com.example.androiddevchallenge.data.MockDataUtil
import com.example.androiddevchallenge.data.Result
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
                hourly = MockDataUtil.createHourlyForecast(1616407200),
                daily = MockDataUtil.createDailyForecast(1616407200),
                lastUpdated = 1616407200
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
