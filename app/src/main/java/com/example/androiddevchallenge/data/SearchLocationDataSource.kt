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
import javax.inject.Inject

interface SearchLocationDataSource {
    suspend fun searchLocation(query: String): List<Location>
    suspend fun findNearby(latitude: Double, longitude: Double): Location
    suspend fun timezone(latitude: Double, longitude: Double): String
}

class GeoNamesDataSource @Inject constructor(private val api: GeoNamesAPI) :
    SearchLocationDataSource {

    override suspend fun searchLocation(query: String): List<Location> {
        // Completing location timezoneId will increase the number of request to the api.
        // For now timezoneId is left empty and completed once the user selects the location he is
        // searching for
        return api.search(query = query).result.map { it.toLocation() }
    }

    override suspend fun findNearby(latitude: Double, longitude: Double): Location {
        val timeZoneId = timezone(latitude = latitude, longitude = longitude)
        val geoLocation = api.findNearBy(latitude = latitude, longitude = longitude).result.first()
        return geoLocation.toLocation(timeZoneId)
    }

    override suspend fun timezone(latitude: Double, longitude: Double): String {
        return api.timezone(
            latitude = latitude,
            longitude = longitude
        ).timezoneId
    }
}

fun GeoNamesLocation.toLocation(timeZoneId: String = ""): Location {
    return Location(
        latitude = latitude,
        longitude = longitude,
        name = if (state.isNotEmpty()) "$name, $state, $country" else "$name, $country",
        timezoneId = timeZoneId
    )
}
