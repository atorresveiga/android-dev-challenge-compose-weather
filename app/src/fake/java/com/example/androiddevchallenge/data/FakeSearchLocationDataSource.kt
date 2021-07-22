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

import com.example.androiddevchallenge.model.Location
import kotlinx.coroutines.delay
import kotlinx.datetime.TimeZone
import java.util.Locale
import javax.inject.Inject

class FakeSearchLocationDataSource @Inject constructor() : SearchLocationDataSource {
    override suspend fun searchLocation(query: String): List<Location> {
        delay(1000)
        return RandomFakeData.locations.filter {
            it.name.lowercase(Locale.getDefault())
                .contains(query.lowercase(Locale.getDefault()).trim())
        }
    }

    override suspend fun findNearby(latitude: Double, longitude: Double): Location {
        return RandomFakeData.locations.first()
            .copy(timezoneId = timezone(latitude = latitude, longitude = longitude))
    }

    override suspend fun timezone(latitude: Double, longitude: Double) =
        TimeZone.currentSystemDefault().id
}
