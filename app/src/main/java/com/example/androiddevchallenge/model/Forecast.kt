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

const val EMPTY_TIME = 0L
const val SECONDS_IN_AN_HOUR = 3600

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
 * @param name name
 * @param latitude location's latitude
 * @param longitude location's longitude
 * @param timezoneId location's timezoneId (according to olson).
 * @param lastUpdated last time we update this location
 */
data class Location(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneId: String,
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
 * @param pop probability of precipitation
 * @param rain rain volume for last hour
 * @param snow snow volume for last hour
 * @param weatherId is an encoded value of [shower/thunder][scale id][scale position][2 digits weather position],
 * for example the value 32105 represents:
 * [first digit] 3 the weather is of type showers and has thunders, first digit is a binary check where
 * first position is whether is of type showers and the second is if the weather have thunders for example
 * 05 is just drizzle
 * 10005 is drizzle showers
 * 20005 is drizzle and thunder
 * 30005 is drizzle showers and thunder
 * [second digit] 2 the scale id 1 (clouds scale), 2 (precipitations scale), 0 weather without scale
 * [third digit] 1 the scale is light (position 1 in array precipitations scale)
 * [four and five digit] 05 drizzle position 5 in string array weather
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
    val weatherId: Int,
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
 * @param weatherId is an encoded value of [shower/thunder][scale id][scale position][2 digits weather position],
 * for example the value 32105 represents:
 * [first digit] 3 the weather is of type showers and has thunders, first digit is a binary check where
 * first position is whether is of type showers and the second is if the weather have thunders for example
 * 05 is just drizzle
 * 10005 is drizzle showers
 * 20005 is drizzle and thunder
 * 30005 is drizzle showers and thunder
 * [second digit] 2 the scale id 1 (clouds scale), 2 (precipitations scale), 0 weather without scale
 * [third digit] 1 the scale is light (position 1 in array precipitations scale)
 * [four and five digit] 05 drizzle position 5 in string array weather
 * @param moonPhase encoded moonPhaseId (position in moon phase enum)
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
    val snow: Float = 0f,
    val weatherId: Int,
    val moonPhase: Int
)
