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
import com.atorresveiga.bluecloud.data.SearchLocationDataSource
import com.atorresveiga.bluecloud.data.UserLocationDataSource
import com.atorresveiga.bluecloud.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Find user's location save it in local datastore.
 */
class FindUserLocationUseCase @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val userLocationDataSource: UserLocationDataSource,
    private val searchLocationDataSource: SearchLocationDataSource,
    private val localForecastRepository: LocalForecastRepository
) {
    suspend fun execute() {
        withContext(defaultDispatcher) {
            val userLocation = userLocationDataSource.getLocation()
                ?: throw LocationNotFoundException("User location is null")

            val location = searchLocationDataSource.findNearby(
                latitude = userLocation.first,
                longitude = userLocation.second
            )

            localForecastRepository.saveCurrentLocation(location)
        }
    }
}

class LocationNotFoundException(message: String) : RuntimeException(message)
