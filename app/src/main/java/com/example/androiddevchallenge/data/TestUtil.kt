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

import androidx.annotation.VisibleForTesting
import com.example.androiddevchallenge.model.HourForecast
import kotlin.random.Random

@VisibleForTesting
object TestUtil {

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
                windSpeed = Random.nextInt(30).toFloat(),
                windDegrees = Random.nextInt(360).toFloat(),
                weather = weather[Random.nextInt(0, 3)],
                sunPosition = Random.nextInt(-1, 100),
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
