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

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.androiddevchallenge.model.DayForecast
import com.example.androiddevchallenge.model.HourForecast
import com.example.androiddevchallenge.ui.LocalDataFormatter

@Composable
fun Sky(
    currentDay: DayForecast,
    currentHour: HourForecast
) {
    val daylight = currentHour.datetime in currentDay.sunrise..currentDay.sunset
    val transition = updateTransition(targetState = daylight, label = "sky transition")
    val background by transition.animateColor(label = "daylight") {
        if (it) {
            MaterialTheme.colors.primary
        } else {
            MaterialTheme.colors.secondary
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
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
