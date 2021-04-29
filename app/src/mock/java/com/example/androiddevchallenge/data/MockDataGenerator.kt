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
import com.example.androiddevchallenge.model.Forecast
import com.example.androiddevchallenge.model.HourForecast
import com.example.androiddevchallenge.model.Location
import com.example.androiddevchallenge.model.MoonPhase
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.min
import kotlin.random.Random

object MockDataGenerator {

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

    fun createForecast(
        location: Location = getRandomLocation(),
        days: Int = 7,
        hours: Int = 48,
        startEpoch: Long = Clock.System.now().epochSeconds,
    ): Forecast {

        val daily = createDailyForecast(startEpoch, days)
        val timeZone = TimeZone.currentSystemDefault()
        val localDateTime = Instant.fromEpochSeconds(startEpoch).toLocalDateTime(timeZone)
        val hoursToMidnight = min(24 - localDateTime.hour, hours)

        val hourly =
            createHourlyForecast(
                startEpoch,
                daily.first().weatherId,
                hoursToMidnight
            ).toMutableList()

        var dayIndex = 1
        var epoch = startEpoch + (3600 * hoursToMidnight)
        var pendingHours = hours - hoursToMidnight

        while (pendingHours - 24 > 0) {
            hourly.addAll(
                createHourlyForecast(
                    epoch,
                    daily[dayIndex].weatherId,
                    24
                )
            )
            pendingHours -= 24
            epoch += (3600 * 24)
            dayIndex += 1
        }

        if (pendingHours > 0) {
            hourly.addAll(
                createHourlyForecast(
                    epoch,
                    daily[dayIndex].weatherId,
                    pendingHours
                )
            )
        }

        return Forecast(location, hourly, daily, startEpoch)
    }

    fun createDailyForecast(startEpoch: Long, days: Int = 7): List<DayForecast> {
        val result = mutableListOf<DayForecast>()
        var datetime = startEpoch

        val date = Instant.fromEpochSeconds(startEpoch)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val sunrise: Int
        val sunset: Int
        if (date.hour > 12) {
            sunrise = -8
            sunset = 5
        } else {
            sunrise = -5
            sunset = 8
        }

        for (day in 1..days) {

            var weatherId = Random.nextInt(5, 7)
            val rain = when (weatherId) {
                in 2..5, 7 -> Random.nextInt(0, 1000) / 100f
                else -> 0f
            }
            val snow = if (weatherId in 6..7) Random.nextInt(0, 1000) / 100f else 0f

            weatherId = when (weatherId) {
                1 -> {
                    val pos = Random.nextInt(-1, 3)
                    if (pos >= 0) 1 * 1000 + pos * 100 + weatherId else weatherId
                }
                in 2..7 -> {
                    val pos = Random.nextInt(-1, 4)
                    weatherId = if (pos >= 0) 2 * 1000 + pos * 100 + weatherId else weatherId
                    weatherId + Random.nextInt(0, 3) * 10000
                }
                else -> weatherId
            }

            val minTemperature = if (weatherId in 4..7) -20f else 22f

            val dayForecast = DayForecast(
                datetime = datetime,
                pressure = Random.nextInt(1000, 3000),
                humidity = Random.nextInt(40),
                uvi = Random.nextInt(12).toFloat(),
                weatherId = weatherId,
                rain = rain,
                snow = snow,
                minTemperature = minTemperature,
                maxTemperature = minTemperature + Random.nextInt(2, 4),
                sunrise = datetime + 3600 * sunrise,
                sunset = datetime + 3600 * sunset,
                moonPhase = MoonPhase.fromPhase(getMoonPhase(date.date))
            )

            datetime += 86400
            result.add(dayForecast)
        }
        return result
    }

    fun createHourlyForecast(
        startEpoch: Long,
        weatherId: Int,
        hours: Int
    ): List<HourForecast> {
        val result = mutableListOf<HourForecast>()
        var datetime = startEpoch
        for (hour in 1..hours) {

            val temperature = if (weatherId in 4..7) {
                Random.nextInt(-20, -10)
            } else {
                Random.nextInt(20, 24)
            }

            val hourWeatherId = when (weatherId) {
                1 -> {
                    val pos = Random.nextInt(-1, 3)
                    if (pos >= 0) 1 * 1000 + pos * 100 + weatherId else weatherId
                }
                in 2..7 -> {
                    val pos = Random.nextInt(-1, 4)
                    val temp = if (pos >= 0) 2 * 1000 + pos * 100 + weatherId else weatherId
                    temp + Random.nextInt(0, 3) * 10000
                }
                else -> weatherId
            }

            val rain: Float
            val snow: Float
            val pop: Float

            when (weatherId) {
                in 2..5 -> {
                    rain = Random.nextInt(0, 1000) / 100f
                    snow = 0f
                    pop = 1f
                }
                6 -> {
                    rain = 0f
                    snow = Random.nextInt(0, 1000) / 100f
                    pop = 1f
                }
                7 -> {
                    rain = Random.nextInt(0, 1000) / 100f
                    snow = Random.nextInt(0, 1000) / 100f
                    pop = 1f
                }
                else -> {
                    rain = Random.nextInt(0, 20) / 100f
                    snow = 0f
                    pop = Random.nextInt(0, 40) / 100f
                }
            }

            val hourForecast = HourForecast(
                datetime = datetime,
                temperature = temperature.toFloat(),
                feelsLike = temperature.toFloat(),
                pressure = Random.nextInt(1000, 3000),
                humidity = Random.nextInt(40),
                uvi = Random.nextInt(12).toFloat(),
                clouds = Random.nextInt(100),
                visibility = Random.nextLong(1000000),
                windSpeed = Random.nextInt(10).toFloat(),
                windDegrees = Random.nextInt(360).toFloat(),
                weatherId = hourWeatherId,
                pop = pop,
                rain = rain,
                snow = snow
            )
            datetime += 3600
            result.add(hourForecast)
        }
        return result
    }
}
