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
package com.atorresveiga.bluecloud.model

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
