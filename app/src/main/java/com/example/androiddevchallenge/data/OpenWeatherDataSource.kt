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

import com.example.androiddevchallenge.model.Forecast
import com.example.androiddevchallenge.model.HourForecast
import com.example.androiddevchallenge.model.Location
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject

interface NetworkForecastDataSource {
    suspend fun getForecast(latitude: Double, longitude: Double): Result<Forecast>
}

class OpenWeatherDataSource @Inject constructor(private val api: OpenWeatherAPI) :
    NetworkForecastDataSource {
    override suspend fun getForecast(latitude: Double, longitude: Double): Result<Forecast> {
        val apiLocationForecast = api.oneCall(latitude, longitude)
        return Result.Success(apiLocationForecast.transformToForecast())
    }
}

fun LocationForecast.transformToForecast(): Forecast {
    return OpenWeatherTransformation.transformToForecast(this)
}

class OpenWeatherTransformation {
    companion object {
        fun transformToForecast(locationForecast: LocationForecast): Forecast {
            val hours = mutableListOf<HourForecast>()
            val days = locationForecast.daily.associateBy { day -> day.datetime.getStringDate() }

            for (hour in locationForecast.hourly) {
                val hourDate = hour.datetime.getStringDate()
                days[hourDate]?.let { day ->
                    val sunPosition =
                        calculateSunPosition(hour.datetime, day.sunrise, day.sunset)
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
                        weather = hour.weather.joinToString { it.description },
                        pop = hour.pop,
                        sunPosition = sunPosition,
                        rain = hour.rain.lastHour,
                        snow = hour.snow.lastHour
                    )
                    hours.add(hourForecast)
                }
            }

            val location = Location(
                timezone = locationForecast.timezone,
                latitude = locationForecast.latitude,
                longitude = locationForecast.longitude,
                lastUpdated = Clock.System.now().epochSeconds
            )

            return Forecast(
                location = location,
                hourly = hours
            )
        }

        private fun calculateSunPosition(datetime: Long, sunrise: Long, sunset: Long): Int {
            val top = (sunset - sunrise) / 2
            return when {
                datetime < sunrise -> -1
                datetime > sunset -> -1
                datetime < top -> ((datetime - sunrise) * 100 / top).toInt()
                else -> ((sunset - datetime) * 100 / top).toInt()
            }
        }
    }
}

/**
 * A util function to get date string representation from a Unix datetime.
 */
fun Long.getStringDate() = Instant.fromEpochMilliseconds(this).toString().take(10)
