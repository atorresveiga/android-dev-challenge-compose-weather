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

interface MetNoAPI {
    @GET("locationforecast/2.0/complete")
    suspend fun complete(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): MetNoForecast

    @GET("sunrise/2.0/.json")
    suspend fun sunriseMoonPhase(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("date") date: String,
        @Query("offset") offset: String,
        @Query("days") numberOfDays: Int = 15
    ): MetNoSunriseMoonPhase
}

@Serializable
data class MetNoForecast(
    val type: String,
    @SerialName("properties")
    val data: MetNoProperties
)

@Serializable
data class MetNoProperties(
    val meta: MetNoMeta,
    @SerialName("timeseries")
    val timeSeries: List<MetNoTimeSeries>
)

@Serializable
data class MetNoMeta(
    @SerialName("updated_at")
    val updated: String,
    val units: MetNoUnits
)

@Serializable
data class MetNoUnits(
    @SerialName("air_pressure_at_sea_level")
    val pressure: String = "",
    @SerialName("air_temperature")
    val temperature: String = "",
    @SerialName("air_temperature_max")
    val temperatureMax: String = "",
    @SerialName("air_temperature_min")
    val temperatureMin: String = "",
    @SerialName("cloud_area_fraction")
    val clouds: String = "",
    @SerialName("dew_point_temperature")
    val dewPoint: String = "",
    @SerialName("fog_area_fraction")
    val visibility: String = "",
    @SerialName("precipitation_amount")
    val precipitation: String = "",
    @SerialName("relative_humidity")
    val humidity: String = "",
    @SerialName("ultraviolet_index_clear_sky")
    val uv: String = "",
    @SerialName("wind_from_direction")
    val windDirection: String = "",
    @SerialName("wind_speed")
    val windSpeed: String = "",
    @SerialName("probability_of_thunder")
    val thunders: String = "",
    @SerialName("probability_of_precipitation")
    val pop: String = "",
)

@Serializable
data class MetNoTimeSeries(
    val time: String,
    val data: MetNoTimeData,
)

@Serializable
data class MetNoTimeData(
    val instant: MetNoInstant,
    @SerialName("next_1_hours")
    val nextHour: MetNoNextHour? = null,
    @SerialName("next_6_hours")
    val next6Hour: MetNoNextHour? = null,
    @SerialName("next_12_hours")
    val next12Hour: MetNoNextHour? = null,
)

@Serializable
data class MetNoInstant(
    val details: MetNoInstantDetails
)

@Serializable
data class MetNoNextHour(
    val summary: MetNoSummary,
    val details: MetNoNextHourDetails? = null
)

@Serializable
data class MetNoSummary(
    @SerialName("symbol_code")
    val code: String
)

@Serializable
data class MetNoInstantDetails(
    @SerialName("cloud_area_fraction")
    val clouds: Float = 0f,
    @SerialName("wind_from_direction")
    val windDirection: Float = 0f,
    @SerialName("air_pressure_at_sea_level")
    val pressure: Float = 0f,
    @SerialName("relative_humidity")
    val humidity: Float = 0f,
    @SerialName("fog_area_fraction")
    val fogArea: Float = 0f,
    @SerialName("air_temperature")
    val temperature: Float = 0f,
    @SerialName("dew_point_temperature")
    val dewPoint: Float = 0f,
    @SerialName("wind_speed")
    val windSpeed: Float = 0f,
    @SerialName("ultraviolet_index_clear_sky")
    val uv: Float = 0f
)

@Serializable
data class MetNoNextHourDetails(
    @SerialName("probability_of_precipitation")
    val pop: Float = 0f,
    @SerialName("probability_of_thunder")
    val thunders: Float = 0f,
    @SerialName("precipitation_amount")
    val precipitation: Float = 0f,
    @SerialName("air_temperature_max")
    val temperatureMax: Float = Float.MIN_VALUE,
    @SerialName("air_temperature_min")
    val temperatureMin: Float = Float.MAX_VALUE,
    @SerialName("ultraviolet_index_clear_sky")
    val uv: Float = 0f,
)

// /////////////////////Sunrise////////////////////////////
@Serializable
data class MetNoSunriseMoonPhase(
    val location: MetNoSunriseMoonPhaseLocation
)

@Serializable
data class MetNoSunriseMoonPhaseLocation(
    val time: List<MetNoSunriseMoonPhaseDayInfo>
)

@Serializable
data class MetNoSunriseMoonPhaseDayInfo(
    val sunset: MetNoSunriseInfo? = null,
    val sunrise: MetNoSunriseInfo? = null,
    @SerialName("moonposition")
    val moon: MetNoMoonPhase,
    val date: String
)

@Serializable
data class MetNoSunriseInfo(
    val time: String,
    @SerialName("desc")
    val description: String
)

@Serializable
data class MetNoMoonPhase(val phase: String)
