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

import com.example.androiddevchallenge.data.LocalForecastRepository
import com.example.androiddevchallenge.data.NetworkForecastDataSource
import com.example.androiddevchallenge.data.Result
import com.example.androiddevchallenge.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RefreshDataUseCase @Inject constructor(
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
    private val localForecastRepository: LocalForecastRepository,
    private val networkForecastDataSource: NetworkForecastDataSource
) : SuspendUseCase<Unit, Unit>(ioDispatcher) {
    override suspend fun execute(parameters: Unit) {
        val location = localForecastRepository.getCurrentLocation().first()
        location?.let { currentLocation ->
            // Get updated forecast from the network
            val result = networkForecastDataSource.getForecast(
                latitude = currentLocation.latitude,
                longitude = currentLocation.longitude
            )
            if (result is Result.Success) {
                val forecast = result.data
                // Save forecast in the device
                localForecastRepository.saveForecast(forecast)
            }
        }
    }
}
