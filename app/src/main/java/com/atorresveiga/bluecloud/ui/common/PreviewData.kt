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
package com.atorresveiga.bluecloud.ui.common

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.atorresveiga.bluecloud.data.Settings
import com.atorresveiga.bluecloud.model.DayForecast
import com.atorresveiga.bluecloud.model.Forecast
import com.atorresveiga.bluecloud.model.HourForecast
import com.atorresveiga.bluecloud.model.Location

val location1 = Location(
    name = "Buenos Aires,Argentina",
    latitude = -34.5477769,
    longitude = -58.4515826,
    timezoneId = "America/Argentina/Buenos_Aires",
    lastUpdated = 1616407200L
)

val location2 = Location(
    name = "Vancouver, Canada",
    latitude = 49.2578263,
    longitude = -123.1939437,
    timezoneId = "America/Vancouver",
    lastUpdated = 1616407200L
)

val hour1 = HourForecast(
    datetime = 1628870400L,
    temperature = 16.51f,
    feelsLike = 15.84f,
    pressure = 1027f,
    humidity = 62f,
    uvi = 10.53f,
    clouds = 75f,
    visibility = 1000,
    windSpeed = 6f,
    windDegrees = 45f,
    weatherId = 0,
    pop = 0f,
    precipitation = 0f,
)

val hour2 = HourForecast(
    datetime = 1628874000L,
    temperature = -10f,
    feelsLike = -10f,
    pressure = 1029f,
    humidity = 5f,
    uvi = 5f,
    clouds = 80f,
    visibility = 1000,
    windSpeed = 6f,
    windDegrees = 90f,
    weatherId = 3,
    pop = 1f,
    precipitation = 1.5f,
)

val day = DayForecast(
    datetime = 1628874000L,
    minTemperature = 10f,
    maxTemperature = 25f,
    pressure = 10f,
    humidity = 90f,
    uvi = 1f,
    clouds = 10f,
    windSpeed = 6f,
    windDegrees = 45f,
    weatherId = 0,
    precipitation = 0f,
    moonPhase = 1,
    sunrise = 1628851980L,
    sunset = 1628896169L
)

val forecast = Forecast(
    location = location1,
    hourly = listOf(hour1, hour2),
    daily = listOf(day)
)

val locations = listOf(location1, location2)

val settings = Settings()

class BooleanProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean>
        get() = sequenceOf(true, false)
}
