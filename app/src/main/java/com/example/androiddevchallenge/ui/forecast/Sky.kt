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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.androiddevchallenge.model.DayForecast
import com.example.androiddevchallenge.model.HourForecast
import com.example.androiddevchallenge.ui.theme.overlay

@Composable
fun Sky(
    currentDayForecast: DayForecast,
    currentHourForecast: HourForecast,
    direction: Direction,
    timezoneId: String,
    isSouthernHemisphere: Boolean,
    modifier: Modifier = Modifier
) {
    val dateFormatter = LocalSettings.current.dataFormatter.date
    val currentHour = dateFormatter.getHour(currentHourForecast.datetime, timezoneId)
    val sunriseHour = dateFormatter.getHour(currentDayForecast.sunrise, timezoneId)
    val sunsetHour = dateFormatter.getHour(currentDayForecast.sunset, timezoneId)
    val skyState = when (currentHour) {
        sunriseHour -> SkyState.Sunrise
        sunsetHour -> SkyState.Sunset
        in sunriseHour..sunsetHour -> SkyState.Day
        else -> SkyState.Night
    }
    val totalClouds = LocalSettings.current.clouds
    val clouds by remember { mutableStateOf(generateRandomWeatherOffsets(totalClouds)) }
    val backgroundClouds = clouds.filter { it.z <= 2 }
    val foregroundClouds = clouds.filter { it.z > 2 }

    Box(modifier = modifier.fillMaxSize()) {
        SkyBackground(state = skyState)
        Clouds(
            cloudiness = currentHourForecast.clouds,
            weatherId = currentHourForecast.weatherId,
            direction = direction,
            clouds = backgroundClouds,
            withStormClouds = false
        )
        DayNight(
            currentHour = currentHour,
            sunriseHour = sunriseHour,
            sunsetHour = sunsetHour,
            moonPhaseId = currentDayForecast.moonPhase,
            isSouthernHemisphere = isSouthernHemisphere,
            skyState = skyState
        )
        Clouds(
            cloudiness = currentHourForecast.clouds,
            weatherId = currentHourForecast.weatherId,
            direction = direction,
            clouds = foregroundClouds
        )

        SkyOverlay(weatherId = currentHourForecast.weatherId)

        if (LocalSettings.current.dataFormatter.precipitation.isPrecipitation(currentHourForecast.weatherId)) {
            val hourlyPrecipitation = LocalSettings.current.hourlyPrecipitation
            val precipitation by remember {
                mutableStateOf(
                    generateRandomWeatherOffsets(
                        hourlyPrecipitation
                    )
                )
            }
            Precipitation(
                weatherId = currentHourForecast.weatherId,
                windDegrees = currentHourForecast.windDegrees,
                windSpeed = currentHourForecast.windSpeed,
                precipitation = precipitation,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun SkyBackground(state: SkyState) {
    val background by animateColorAsState(
        targetValue = when (state) {
            SkyState.Day -> MaterialTheme.colors.primary
            SkyState.Night -> MaterialTheme.colors.secondary
            else -> MaterialTheme.colors.secondaryVariant
        }
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = background)
    )
}

@Composable
fun SkyOverlay(weatherId: Int) {
    val precipitation = LocalSettings.current.dataFormatter.precipitation
    val isPrecipitation = precipitation.isPrecipitation(weatherId)

    val alpha by animateFloatAsState(
        targetValue = when {
            isPrecipitation && precipitation.getIntensity(weatherId) >= .5f -> .8f
            else -> .5f
        }
    )
    val brush = Brush.verticalGradient(
        .0f to Color.Black,
        .3f to MaterialTheme.colors.overlay,
        .9f to Color.Black,
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    Box(
        modifier = Modifier
            .alpha(alpha)
            .background(brush = brush)
            .fillMaxSize()
    )
}

enum class SkyState { Day, Night, Sunrise, Sunset }
