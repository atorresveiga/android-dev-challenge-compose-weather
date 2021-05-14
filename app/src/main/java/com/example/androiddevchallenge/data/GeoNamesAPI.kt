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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoNamesAPI {
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("isNameRequired") isNameRequired: Boolean = true,
        @Query("type") type: String = "json",
        @Query("featureClass") featureClass: String = "P",
        @Query("maxRows") max: Int = 8
    ): GeoNamesSearchResponse
}

@Serializable
data class GeoNamesSearchResponse(
    @SerialName("totalResultsCount")
    val total: Int,
    @SerialName("geonames")
    val result: List<GeoNamesLocation>
)

@Serializable
data class GeoNamesLocation(
    @SerialName("lat")
    val latitude: Double,
    @SerialName("lng")
    val longitude: Double,
    val name: String,
    @SerialName("adminName1")
    val state: String,
    @SerialName("countryName")
    val country: String
)
