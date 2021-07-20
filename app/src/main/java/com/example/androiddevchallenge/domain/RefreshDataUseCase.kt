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
package com.example.androiddevchallenge.domain

import com.example.androiddevchallenge.data.DataStoreManager
import com.example.androiddevchallenge.data.LocalForecastRepository
import com.example.androiddevchallenge.data.NetworkForecastDataSource
import com.example.androiddevchallenge.di.IoDispatcher
import com.example.androiddevchallenge.di.MetNo
import com.example.androiddevchallenge.di.OpenWeather
import com.example.androiddevchallenge.ui.ForecastDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RefreshDataUseCase @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val localForecastRepository: LocalForecastRepository,
    private val dataStoreManager: DataStoreManager,
    @OpenWeather private val openWeatherDataSource: NetworkForecastDataSource,
    @MetNo private val metNoDataSource: NetworkForecastDataSource
) {
    suspend fun execute() {
        withContext(ioDispatcher) {

            localForecastRepository.getCurrentLocation().first()?.let { location ->
                // Get dataSource selected in settings
                val dataSource = dataStoreManager.settings.first().dataSource
                val source = when (dataSource) {
                    ForecastDataSource.OpenWeather -> openWeatherDataSource
                    ForecastDataSource.MetNo -> metNoDataSource
                }
                // Get updated forecast from the network
                source.getForecast(location).also { forecast ->
                    localForecastRepository.saveForecast(forecast, dataSource)
                }
            }
        }
    }
}
