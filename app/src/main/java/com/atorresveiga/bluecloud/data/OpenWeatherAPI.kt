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
package com.atorresveiga.bluecloud.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherAPI {
    @GET("onecall")
    suspend fun oneCall(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("exclude") exclude: String = "minutely,current,alerts",
        @Query("units") units: String = "metric"
    ): OpenWeatherForecast
}

@Serializable
data class OpenWeatherForecast(
    @SerialName("lat")
    val latitude: Double,
    @SerialName("lon")
    val longitude: Double,
    val timezone: String,
    @SerialName("timezone_offset")
    val offset: Long,
    val hourly: List<OpenWeatherHourForecast>,
    val daily: List<OpenWeatherDayForecast>
)

@Serializable
data class OpenWeatherDayForecast(
    @SerialName("dt")
    val datetime: Long,
    val pressure: Float,
    val humidity: Float,
    @SerialName("dew_point")
    val dewPoint: Float,
    val uvi: Float,
    val clouds: Float,
    @SerialName("wind_speed")
    val windSpeed: Float,
    @SerialName("wind_deg")
    val windDegrees: Float,
    val weather: List<Weather>,
    val pop: Float,

    val sunrise: Long,
    val sunset: Long,
    @SerialName("temp")
    val temperature: DayTemperature,
    @SerialName("feels_like")
    val feelsLike: FeelsLikeTemperature,
    val rain: Float = 0f,
    val snow: Float = 0f,
    @SerialName("moon_phase")
    val moonPhase: Double
)

@Serializable
data class OpenWeatherHourForecast(
    @SerialName("dt")
    val datetime: Long,
    val pressure: Float,
    val humidity: Float,
    @SerialName("dew_point")
    val dewPoint: Float,
    val uvi: Float,
    val clouds: Float,
    @SerialName("wind_speed")
    val windSpeed: Float,
    @SerialName("wind_deg")
    val windDegrees: Float,
    @SerialName("wind_gust")
    val windGust: Float,
    val weather: List<Weather>,
    val visibility: Long,
    val pop: Float,

    @SerialName("temp")
    val temperature: Float,
    @SerialName("feels_like")
    val feelsLike: Float,
    val rain: Volume = Volume(0f),
    val snow: Volume = Volume(0f)
)

@Serializable
data class Weather(
    val id: Int,
    val main: String,
    val description: String
)

@Serializable
data class DayTemperature(
    val min: Float,
    val max: Float,
    val day: Float,
    val night: Float,
    @SerialName("eve")
    val evening: Float,
    @SerialName("morn")
    val morning: Float
)

@Serializable
data class FeelsLikeTemperature(
    val day: Float,
    val night: Float,
    @SerialName("eve")
    val evening: Float,
    @SerialName("morn")
    val morning: Float
)

@Serializable
data class Volume(
    @SerialName("1h")
    val lastHour: Float
)
