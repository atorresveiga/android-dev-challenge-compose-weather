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

import com.example.androiddevchallenge.CELSIUS
import com.example.androiddevchallenge.HOURLY
import com.example.androiddevchallenge.METERS
import com.example.androiddevchallenge.OPEN_WEATHER
import com.example.androiddevchallenge.TWELVE
import com.example.androiddevchallenge.ui.DataFormatter

data class Settings(
    val clouds: Int = 30,
    val stormClouds: Int = 5,
    val hourlyPrecipitation: Int = 150,
    val dailyPrecipitation: Int = 85,
    val hourSystem: Int = TWELVE,
    val temperatureSystem: Int = CELSIUS,
    val windSpeedSystem: Int = METERS,
    val defaultDisplayView: Int = HOURLY,
    val dataSource: Int = OPEN_WEATHER
) {
    val dataFormatter = DataFormatter(
        hourSystem = hourSystem,
        temperatureSystem = temperatureSystem,
        windSpeedSystem = windSpeedSystem
    )
}