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

import com.example.androiddevchallenge.MET_NO
import com.example.androiddevchallenge.model.Forecast
import com.example.androiddevchallenge.model.Location
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class NetworkForecastRepository @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val openWeatherDataSource: OpenWeatherDataSource,
    private val metNoDataSource: MetNoDataSource
) : NetworkForecastDataSource {
    override suspend fun getForecast(location: Location): Forecast {
        val source = when (dataStoreManager.settings.first().dataSource) {
            MET_NO -> metNoDataSource
            else -> openWeatherDataSource
        }
        // Get updated forecast from the network
        return source.getForecast(location)
    }
}
