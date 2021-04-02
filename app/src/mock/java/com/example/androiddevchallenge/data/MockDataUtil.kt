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

import com.example.androiddevchallenge.model.DayForecast
import com.example.androiddevchallenge.model.HourForecast
import com.example.androiddevchallenge.model.Location
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

object MockDataUtil {

    fun getRandomLocation(): Location {
        val locations = arrayOf(
            Location(
                timezone = "Americas/Argentina/Buenos_Aires",
                latitude = -34.5477769,
                longitude = -58.4515826,
            ),
            Location(
                timezone = "America/Chicago",
                latitude = -22.955536,
                longitude = -43.1847027
            ),
            Location(
                timezone = "Americas/Cuba/La_Habana",
                latitude = 23.1206009,
                longitude = -82.4065344
            )
        )
        return locations[Random.nextInt(0, 2)]
    }

    fun createDailyForecast(startEpoch: Long, days: Int = 7): List<DayForecast> {
        val result = mutableListOf<DayForecast>()
        var datetime = startEpoch
        val weather = listOf("broken clouds", "scattered clouds", "clear sky", "light rain")
        for (hour in 0..days) {

            val date = Instant.fromEpochSeconds(startEpoch)
                .toLocalDateTime(TimeZone.currentSystemDefault())

            val (sunrise, sunset) = if (date.hour > 12) {
                arrayOf(-8, 5)
            } else {
                arrayOf(-5, 8)
            }

            val dayForecast = DayForecast(
                datetime = datetime,
                pressure = Random.nextInt(1000, 3000),
                humidity = Random.nextInt(40),
                uvi = Random.nextInt(30).toFloat(),
                weather = weather[Random.nextInt(0, 3)],
                rain = Random.nextInt(0, 1000) / 100f,
                snow = Random.nextInt(0, 1000) / 100f,
                minTemperature = Random.nextInt(-10, 20).toFloat(),
                maxTemperature = Random.nextInt(20, 40).toFloat(),
                sunrise = datetime + 3600 * sunrise,
                sunset = datetime + 3600 * sunset,
            )

            datetime += 86400
            result.add(dayForecast)
        }
        return result
    }

    fun createHourlyForecast(startEpoch: Long, hours: Int = 48): List<HourForecast> {
        val result = mutableListOf<HourForecast>()
        var datetime = startEpoch
        val weather = listOf("broken clouds", "scattered clouds", "clear sky", "light rain")
        for (hour in 0..hours) {
            val temperature = Random.nextInt(10, 38).toFloat()
            val hourForecast = HourForecast(
                datetime = datetime,
                temperature = temperature,
                feelsLike = temperature,
                pressure = Random.nextInt(1000, 3000),
                humidity = Random.nextInt(40),
                uvi = Random.nextInt(30).toFloat(),
                clouds = Random.nextInt(40),
                visibility = Random.nextLong(1000000),
                windSpeed = Random.nextInt(10).toFloat(),
                windDegrees = Random.nextInt(360).toFloat(),
                weather = weather[Random.nextInt(0, 3)],
                pop = Random.nextInt(0, 100) / 100f,
                rain = Random.nextInt(0, 1000) / 100f,
                snow = Random.nextInt(0, 1000) / 100f
            )
            datetime += 3600
            result.add(hourForecast)
        }
        return result
    }
}
