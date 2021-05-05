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
import javax.inject.Inject

interface NetworkForecastDataSource {
    suspend fun getForecast(latitude: Double, longitude: Double): Forecast
}

class OpenWeatherDataSource @Inject constructor(private val api: OpenWeatherAPI) :
    NetworkForecastDataSource {
    override suspend fun getForecast(latitude: Double, longitude: Double): Forecast {
        val apiLocationForecast = api.oneCall(latitude, longitude)
        return apiLocationForecast.transformToForecast()
    }
}

fun LocationForecast.transformToForecast(): Forecast {
    return OpenWeatherTransformation.transformToForecast(this)
}

class OpenWeatherTransformation {
    companion object {
        private fun encodeWeatherId(weather: Weather): Int {
            return when (weather.id) {
                800 -> 0
                801 -> 1001
                802 -> 1101
                803 -> 1201
                804 -> 1301
                741 -> 8
                701 -> 9
                711 -> 10
                721 -> 11
                731 -> 12
                751 -> 13
                761 -> 14
                762 -> 15
                771 -> 16
                781 -> 17
                300, 310 -> 2102 // Drizzle
                301, 311 -> 2
                302, 312 -> 2202
                313, 321 -> 10002
                314 -> 12202
                230 -> 22102
                231 -> 20002
                232 -> 22202
                500 -> 2103 // Rain
                501 -> 3
                502 -> 2203
                503 -> 2303
                504 -> 2403
                520 -> 12103
                521 -> 10003
                522 -> 12203
                531 -> 12003
                200 -> 22103
                201 -> 20003
                202 -> 22203
                511 -> 4 // Freezing Rain
                611 -> 5 // Sleet
                612 -> 12105
                613 -> 10005
                600 -> 2106 // Snow
                601 -> 6
                602 -> 2206
                620 -> 12106
                621 -> 10006
                622 -> 12206
                615 -> 2107 // Rain and snow
                616 -> 7
                210 -> 2119 // Thunderstorm
                211 -> 19
                212 -> 2219
                221 -> 2019
                else -> throw IllegalArgumentException("WeatherId not found")
            }
        }

        fun transformToForecast(locationForecast: LocationForecast): Forecast {
            val hours = mutableListOf<HourForecast>()
            val days = mutableListOf<DayForecast>()
            for (hour in locationForecast.hourly) {
                val hourForecast = HourForecast(
                    datetime = hour.datetime, // local representation of datetime
                    temperature = hour.temperature,
                    feelsLike = hour.feelsLike,
                    pressure = hour.pressure,
                    humidity = hour.humidity,
                    uvi = hour.uvi,
                    clouds = hour.clouds,
                    visibility = hour.visibility,
                    windSpeed = hour.windSpeed,
                    windDegrees = hour.windDegrees,
                    weatherId = encodeWeatherId(hour.weather.first()),
                    pop = hour.pop,
                    rain = hour.rain.lastHour,
                    snow = hour.snow.lastHour
                )
                hours.add(hourForecast)
            }

            val timezone = TimeZone.currentSystemDefault()
            for (day in locationForecast.daily) {
                val date = Instant.fromEpochSeconds(day.datetime).toLocalDateTime(timezone).date
                val dayForecast = DayForecast(
                    datetime = day.datetime,
                    pressure = day.pressure,
                    humidity = day.humidity,
                    uvi = day.uvi,
                    sunrise = day.sunrise,
                    sunset = day.sunset,
                    minTemperature = day.temperature.min,
                    maxTemperature = day.temperature.max,
                    rain = day.rain,
                    snow = day.snow,
                    weatherId = encodeWeatherId(day.weather.first()),
                    moonPhase = MoonPhase.fromPhase(getMoonPhase(date))
                )
                days.add(dayForecast)
            }

            val location = Location(
                timezone = locationForecast.timezone,
                latitude = locationForecast.latitude,
                longitude = locationForecast.longitude
            )

            return Forecast(
                location = location,
                hourly = hours,
                daily = days,
                lastUpdated = Clock.System.now().epochSeconds
            )
        }
    }
}
