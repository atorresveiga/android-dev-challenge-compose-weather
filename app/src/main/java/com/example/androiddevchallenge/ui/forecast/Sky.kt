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
package com.example.androiddevchallenge.ui.forecast

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.androiddevchallenge.model.DayForecast
import com.example.androiddevchallenge.model.HourForecast
import com.example.androiddevchallenge.ui.LocalDataFormatter
import com.example.androiddevchallenge.ui.theme.overlay

@Composable
fun Sky(
    currentDay: DayForecast,
    currentHour: HourForecast,
    direction: Direction
) {
    Box(modifier = Modifier.fillMaxSize()) {
        DayNight(
            datetime = currentHour.datetime,
            sunrise = currentDay.sunrise,
            sunset = currentDay.sunset
        )
        Clouds(
            cloudiness = currentHour.clouds,
            weatherId = currentHour.weatherId,
            direction = direction
        )
        SkyOverlay(weatherId = currentHour.weatherId)
        if (LocalDataFormatter.current.precipitation.isPrecipitation(currentHour.weatherId)) {
            Precipitation(
                weatherId = currentHour.weatherId,
                windDegrees = currentHour.windDegrees,
                windSpeed = currentHour.windSpeed,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun SkyOverlay(weatherId: Int) {
    val precipitation = LocalDataFormatter.current.precipitation
    val isPrecipitation = precipitation.isPrecipitation(weatherId)

    val alpha by animateFloatAsState(
        targetValue = when {
            isPrecipitation && precipitation.getIntensity(weatherId) >= .5f -> .8f
            else -> .5f
        }
    )

    Box(
        modifier = Modifier
            .alpha(alpha)
            .background(
                brush = Brush
                    .verticalGradient(
                        .0f to Color.Black,
                        .3f to MaterialTheme.colors.overlay,
                        .9f to Color.Black,
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
            )
            .fillMaxSize()
    )
}
