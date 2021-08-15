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

import com.atorresveiga.bluecloud.ui.forecast.ForecastView
import com.atorresveiga.bluecloud.ui.formatter.Formatter
import com.atorresveiga.bluecloud.ui.formatter.HourSystem
import com.atorresveiga.bluecloud.ui.formatter.TemperatureSystem
import com.atorresveiga.bluecloud.ui.formatter.WindMeasurementSystem

data class Settings(
    val clouds: Int = 30,
    val stormClouds: Int = 5,
    val hourlyPrecipitation: Int = 150,
    val dailyPrecipitation: Int = 85,
    val hourSystem: Int = HourSystem.Twelve.ordinal,
    val temperatureSystem: Int = TemperatureSystem.Celsius.ordinal,
    val windSpeedSystem: Int = WindMeasurementSystem.Meters.ordinal,
    val defaultDisplayView: Int = ForecastView.HourlyView.ordinal,
    val dataSource: Int = ForecastDataSource.OpenWeather.ordinal
) {
    val dataFormatter = Formatter(
        hourSystem = hourSystem,
        temperatureSystem = temperatureSystem,
        windSpeedSystem = windSpeedSystem
    )
}
