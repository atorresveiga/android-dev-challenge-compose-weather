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
package com.atorresveiga.bluecloud.domain

import com.atorresveiga.bluecloud.data.LocalForecastRepository
import com.atorresveiga.bluecloud.data.NetworkForecastDataSource
import com.atorresveiga.bluecloud.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RefreshDataUseCase @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val localForecastRepository: LocalForecastRepository,
    private val networkForecastDataSource: NetworkForecastDataSource
) {
    suspend fun execute() {
        withContext(ioDispatcher) {
            localForecastRepository.getCurrentLocation().first()?.let { location ->
                // Get updated forecast from the network
                networkForecastDataSource.getForecast(location).also { forecast ->
                    localForecastRepository.saveForecast(forecast)
                }
            }
        }
    }
}
