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

import com.example.androiddevchallenge.data.SearchLocationDataSource
import com.example.androiddevchallenge.di.DefaultDispatcher
import com.example.androiddevchallenge.model.Location
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FindLocationTimezone @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val searchLocationDataSource: SearchLocationDataSource
) {
    suspend fun execute(location: Location): Location {
        return withContext(defaultDispatcher) {
            val timezoneId = searchLocationDataSource.timezone(
                latitude = location.latitude,
                longitude = location.longitude
            )
            location.copy(timezoneId = timezoneId)
        }
    }
}
