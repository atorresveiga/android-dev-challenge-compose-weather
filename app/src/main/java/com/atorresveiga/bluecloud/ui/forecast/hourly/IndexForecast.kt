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
package com.atorresveiga.bluecloud.ui.forecast.hourly

import com.atorresveiga.bluecloud.model.DayForecast
import com.atorresveiga.bluecloud.model.Forecast
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * IndexForecast util class to create an index between an hour forecast and the day forecast this hour belongs
 * @param forecast forecast to process
 */
class IndexForecast(forecast: Forecast) {
    val location = forecast.location
    private val daily = forecast.daily
    val hourly = forecast.hourly

    private val dayIndex: HashMap<Long, Int> = hashMapOf()

    init {
        daily.forEachIndexed { index, dayForecast ->
            val timezone = TimeZone.of(location.timezoneId)
            val date = Instant.fromEpochSeconds(dayForecast.datetime).toLocalDateTime(timezone).date
            for (hour in hourly) {
                val hourDate =
                    Instant.fromEpochSeconds(hour.datetime).toLocalDateTime(timezone).date
                if (date == hourDate) {
                    dayIndex[hour.datetime] = index
                }
            }
        }
    }

    fun getDayForecast(hourDatetime: Long): DayForecast =
        dayIndex[hourDatetime]?.let { daily[it] }
            ?: throw IllegalArgumentException("Hour not belongs to current forecast")
}
