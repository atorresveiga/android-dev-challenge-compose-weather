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
package com.example.androiddevchallenge.ui

data class Settings(
    val clouds: Int = 30,
    val stormClouds: Int = 5,
    val hourlyPrecipitation: Int = 150,
    val dailyPrecipitation: Int = 85,
    val hourSystem: HourSystem = HourSystem.Twelve,
    val temperatureSystem: TemperatureSystem = TemperatureSystem.Celsius,
    val windSpeedSystem: WindSpeedSystem = WindSpeedSystem.Meters,
    val defaultDisplayView: ForecastDisplayView = ForecastDisplayView.Hourly
) {
    val dataFormatter = DataFormatter(
        hourSystem = hourSystem,
        temperatureSystem = temperatureSystem,
        windSpeedSystem = windSpeedSystem
    )
}