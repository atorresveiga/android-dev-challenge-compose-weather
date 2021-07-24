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

import com.atorresveiga.bluecloud.data.SearchLocationDataSource
import com.atorresveiga.bluecloud.model.Location

class SuccessTestSearchLocation : SearchLocationDataSource {

    override suspend fun searchLocation(query: String): List<Location> {
        TODO("Not yet implemented")
    }

    override suspend fun findNearby(latitude: Double, longitude: Double): Location {
        return Location(
            name = "Test Location",
            latitude = latitude,
            longitude = longitude,
            timezoneId = timezone(latitude, longitude),
            lastUpdated = 1616407200L
        )
    }

    override suspend fun timezone(latitude: Double, longitude: Double): String = "test_timezone"
}
