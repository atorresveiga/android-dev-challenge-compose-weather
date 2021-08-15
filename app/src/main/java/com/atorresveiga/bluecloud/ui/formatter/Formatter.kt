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
package com.atorresveiga.bluecloud.ui.formatter

class Formatter(
    hourSystem: Int,
    temperatureSystem: Int,
    windSpeedSystem: Int
) {
    val temperature: TemperatureFormatter
    val date: DateFormatter
    val wind: WindFormatter
    val precipitation: PrecipitationFormatter
    val weather: WeatherFormatter
    val uvi: UVFormatter = UVFormatter
    val moonPhase: MoonPhaseFormatter = MoonPhaseFormatter
    val humidity: HumidityFormatter = HumidityFormatter

    init {
        val scaleFormatter = ScaleFormatter()
        temperature = when (TemperatureSystem.values()[temperatureSystem]) {
            TemperatureSystem.Fahrenheit -> FahrenheitTemperatureFormatter()
            else -> CelsiusTemperatureFormatter()
        }
        val hour = when (HourSystem.values()[hourSystem]) {
            HourSystem.TwentyFour -> TwentyFourHourSystemFormatter()
            else -> TwelveHourSystemFormatter()
        }
        val windMeasurement = when (WindMeasurementSystem.values()[windSpeedSystem]) {
            WindMeasurementSystem.Kilometers -> KilometersWindMeasurement()
            WindMeasurementSystem.Miles -> MilesWindMeasurement()
            else -> MetersWindMeasurement()
        }
        wind = WindFormatter(windMeasurement)
        date = DateFormatter(hour)
        weather = WeatherFormatter(scaleFormatter)
        precipitation = PrecipitationFormatter(weather)
    }
}

fun String.getLocationShortValue(): String {
    val sections = this.split(",")
    return sections.first()
}
