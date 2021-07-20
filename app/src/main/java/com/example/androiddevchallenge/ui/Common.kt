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

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.androiddevchallenge.R

enum class MoonPhase {
    NewMoon, WaxingCrescent, FirstQuarter, WaxingGibbous, FullMoon, WaningGibbous, ThirdQuarter, WaningCrescent;
}

enum class PrecipitationForm { Rain, Snow, RainAndSnow }

enum class HourSystem { Twelve, TwentyFour }

@Composable
fun HourSystem.translatableString(): String {
    return when (this) {
        HourSystem.Twelve -> stringResource(R.string.twelve)
        HourSystem.TwentyFour -> stringResource(R.string.twenty_four)
    }
}

enum class ForecastDisplayView { Hourly, Daily }

@Composable
fun ForecastDisplayView.translatableString(): String {
    return when (this) {
        ForecastDisplayView.Daily -> stringResource(R.string.daily)
        ForecastDisplayView.Hourly -> stringResource(R.string.hourly)
    }
}

enum class WindSpeedSystem { Meters, Kilometers, Miles }

@Composable
fun WindSpeedSystem.translatableString(): String {
    return when (this) {
        WindSpeedSystem.Meters -> stringResource(R.string.meters)
        WindSpeedSystem.Kilometers -> stringResource(R.string.kilometers)
        WindSpeedSystem.Miles -> stringResource(R.string.miles)
    }
}

enum class TemperatureSystem { Celsius, Fahrenheit }

@Composable
fun TemperatureSystem.translatableString(): String {
    return when (this) {
        TemperatureSystem.Celsius -> stringResource(R.string.celsius)
        TemperatureSystem.Fahrenheit -> stringResource(R.string.fahrenheit)
    }
}

enum class ForecastDataSource { OpenWeather, MetNo }
@Composable
fun ForecastDataSource.translatableString(): String {
    return when (this) {
        ForecastDataSource.OpenWeather -> stringResource(R.string.open_weather)
        ForecastDataSource.MetNo -> stringResource(R.string.met_no)
    }
}
