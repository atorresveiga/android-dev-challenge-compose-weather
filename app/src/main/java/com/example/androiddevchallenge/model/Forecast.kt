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
package com.example.androiddevchallenge.model

/**
 * A data class that holds the forecast data.
 * @param location location of the current forecast (timezone,lat lon..ect)
 * @param hourly forecast for the next 48 hours
 * @param daily forecast for the next 7 days
 */
data class Forecast(
    val location: Location,
    val hourly: List<HourForecast>,
    val daily: List<DayForecast>
)

/**
 * A data class that holds a location's forecast data.
 * @param timezone timezone name
 * @param latitude location's latitude
 * @param longitude location's longitude
 * @param lastUpdated when was created this forecast
 */
data class Location(
    val timezone: String,
    val latitude: Double,
    val longitude: Double,
    val lastUpdated: Long
)

/**
 * A data class that holds the forecast for this specific Hour.
 * @param datetime time of the forecasted data, unix, UTC
 * @param temperature temperature metric: Celsius
 * @param feelsLike this accounts for the human perception of weather metric: Celsius
 * @param pressure atmospheric pressure on the sea level, hPa
 * @param humidity, %
 * @param uvi UV index
 * @param clouds cloudiness %
 * @param visibility average visibility, metres
 * @param windSpeed Wind speed. Units metre/sec
 * @param windDegrees Wind direction, degrees (meteorological)
 * @param weather group of weather parameters (Rain, Snow, Extreme etc.)
 * @param pop probability of precipitation
 * @param rain rain volume for last hour
 * @param snow snow volume for last hour
 */
data class HourForecast(
    val datetime: Long,
    val temperature: Float,
    val feelsLike: Float,
    val pressure: Int,
    val humidity: Int,
    val uvi: Float,
    val clouds: Int,
    val visibility: Long,
    val windSpeed: Float,
    val windDegrees: Float,
    val weather: String,
    val pop: Float,
    val rain: Float,
    val snow: Float
)

/**
 * A data class that holds the forecast for this specific Hour.
 * @param datetime time of the forecasted data, unix, UTC
 * @param pressure atmospheric pressure on the sea level, hPa
 * @param humidity, %
 * @param uvi UV index
 * @param sunrise Sunrise time, Unix, UTC
 * @param sunset Sunset time, Unix, UTC
 * @param minTemperature Min daily temperature
 * @param maxTemperature Max daily temperature
 * @param rain rain volume
 * @param snow snow volume
 */
data class DayForecast(
    val datetime: Long,
    val pressure: Int,
    val humidity: Int,
    val uvi: Float,
    val sunrise: Long,
    val sunset: Long,
    val minTemperature: Float,
    val maxTemperature: Float,
    val rain: Float = 0f,
    val snow: Float = 0f
)
