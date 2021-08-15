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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.atorresveiga.bluecloud.model.DayForecast
import com.atorresveiga.bluecloud.model.HourForecast
import com.atorresveiga.bluecloud.ui.common.day
import com.atorresveiga.bluecloud.ui.common.hour1
import com.atorresveiga.bluecloud.ui.common.location1
import com.atorresveiga.bluecloud.ui.common.settings
import com.atorresveiga.bluecloud.ui.forecast.LocalSettings
import com.atorresveiga.bluecloud.ui.forecast.Precipitation
import com.atorresveiga.bluecloud.ui.forecast.generateRandomWeatherOffsets
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme
import com.atorresveiga.bluecloud.ui.theme.overlay

enum class Sky { Day, Sunrise, Sunset, Night }

/**
 * Sky representation based on the forecast and the day light
 * @param currentDayForecast current day's forecast
 * @param currentHourForecast hour to represent
 * @param direction user's navigation direction (to animate the clouds)
 * @param timezoneId current location timezone id
 * @param isSouthernHemisphere if the current location is in the south hemisphere (some moon phases
 * are shown differently in each hemisphere)
 * @param modifier Modifier
 */
@Composable
fun Sky(
    currentDayForecast: DayForecast,
    currentHourForecast: HourForecast,
    direction: NavigationDirection,
    timezoneId: String,
    isSouthernHemisphere: Boolean,
    modifier: Modifier = Modifier
) {
    val dateFormatter = LocalSettings.current.dataFormatter.date
    val currentHour = dateFormatter.getHour(currentHourForecast.datetime, timezoneId)
    val sunriseHour = dateFormatter.getHour(currentDayForecast.sunrise, timezoneId)
    val sunsetHour = dateFormatter.getHour(currentDayForecast.sunset, timezoneId)
    val sky = when (currentHour) {
        sunriseHour -> Sky.Sunrise
        sunsetHour -> Sky.Sunset
        in sunriseHour..sunsetHour -> Sky.Day
        else -> Sky.Night
    }
    val totalClouds = LocalSettings.current.clouds
    val clouds by remember { mutableStateOf(generateRandomWeatherOffsets(totalClouds)) }
    val backgroundClouds = clouds.filter { it.z <= 2 }
    val foregroundClouds = clouds.filter { it.z > 2 }

    Box(modifier = modifier.fillMaxSize()) {
        SkyBackground(sky = sky)
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
            sky = sky
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

/**
 * SkyBackground background color from sky's state
 * @param sky current [Sky] state
 */
@Composable
fun SkyBackground(sky: Sky) {
    val background by animateColorAsState(
        targetValue = when (sky) {
            Sky.Day -> MaterialTheme.colors.primary
            Sky.Night -> MaterialTheme.colors.secondary
            else -> MaterialTheme.colors.secondaryVariant
        }
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = background)
    )
}

/**
 * SkyOverlay overlay from current hour's weatherId
 * @param weatherId current hour's weatherId
 */
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

@Preview(showSystemUi = true, device = Devices.PIXEL_4)
@Composable
fun SkyPreview() {
    BlueCloudTheme {
        Surface {
            CompositionLocalProvider(LocalSettings provides settings) {
                Sky(
                    currentDayForecast = day,
                    currentHourForecast = hour1.copy(weatherId = 20005),
                    direction = NavigationDirection.Forward,
                    timezoneId = location1.timezoneId,
                    isSouthernHemisphere = false
                )
            }
        }
    }
}
