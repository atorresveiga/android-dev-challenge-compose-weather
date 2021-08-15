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
package com.atorresveiga.bluecloud.ui.forecast.daily

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.model.DayForecast
import com.atorresveiga.bluecloud.ui.common.day
import com.atorresveiga.bluecloud.ui.common.settings
import com.atorresveiga.bluecloud.ui.forecast.Cloud
import com.atorresveiga.bluecloud.ui.forecast.LocalSettings
import com.atorresveiga.bluecloud.ui.forecast.StormCloudWithLightning
import com.atorresveiga.bluecloud.ui.forecast.Sun
import com.atorresveiga.bluecloud.ui.theme.cloudColor
import com.atorresveiga.bluecloud.ui.theme.lightningColor
import com.atorresveiga.bluecloud.ui.theme.sunColor

/**
 * DayWeatherIcon day's icon based on weather's conditions
 * @param day current day forecast
 * @param modifier Modifier
 */
@Composable
fun DayWeatherIcon(day: DayForecast, modifier: Modifier = Modifier) {
    val hasThunders = LocalSettings.current.dataFormatter.weather.hasThunders(day.weatherId)
    val isPrecipitation =
        LocalSettings.current.dataFormatter.precipitation.isPrecipitation(day.weatherId)
    val isCloudy = day.clouds > 45

    if (hasThunders || isPrecipitation || isCloudy) {
        TwoCloudsIcon(
            color = if (isPrecipitation || hasThunders) Color.LightGray else MaterialTheme.colors.cloudColor,
            hasLightning = hasThunders,
            modifier = modifier
        )
    } else {
        SunIcon(modifier = modifier, hasClouds = day.clouds > 20)
    }
}

/**
 * TwoCloudsIcon Two clouds icon (for cloudy days and days with precipitation)
 * @param color cloud's color
 * @param hasLightning if the front cloud should display a lightning (for weather id's with thunders)
 * @param modifier Modifier
 */
@Composable
internal fun TwoCloudsIcon(color: Color, hasLightning: Boolean, modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = modifier
    ) {

        val width = maxWidth * .8f
        val height = width * 1.06f
        val mainCloudModifier =
            Modifier
                .size(width = width, height = height)
                .offset(x = 0.dp, maxHeight * .2f)

        if (hasLightning) {
            StormCloudWithLightning(
                color = color,
                drawLightning = hasLightning,
                lightningColor = MaterialTheme.colors.lightningColor,
                lightningAlpha = 1f,
                modifier = mainCloudModifier
            )
        } else {
            StormCloudWithLightning(
                color = color,
                modifier = mainCloudModifier
            )
        }

        StormCloudWithLightning(
            color = color.copy(alpha = .4f),
            modifier = Modifier
                .size(width = width, height = height)
                .offset(x = maxWidth * .2f, maxHeight * .1f)
        )
    }
}

/**
 * SunIcon Sun icon (for fair days and days with few clouds)
 * @param modifier Modifier
 * @param hasClouds when to show icon's cloud (for days with few clouds)
 */
@Composable
internal fun SunIcon(modifier: Modifier = Modifier, hasClouds: Boolean = false) {
    BoxWithConstraints(
        modifier = modifier
    ) {
        Sun(
            color = MaterialTheme.colors.sunColor,
            modifier = Modifier.fillMaxSize(),
            animate = false
        )
        val width = maxWidth * .6f
        val height = width * .66f
        if (hasClouds) {
            Cloud(
                color = MaterialTheme.colors.cloudColor,
                modifier = Modifier
                    .size(width = width, height = height)
                    .offset(x = maxWidth * .35f, y = maxHeight * .35f)
            )
        }
    }
}

@Preview(widthDp = 50, heightDp = 50)
@Composable
fun FairDayPreview() {
    val day = day.copy(weatherId = 0)
    CompositionLocalProvider(LocalSettings provides settings) {
        DayWeatherIcon(day = day)
    }
}

@Preview(widthDp = 50, heightDp = 50)
@Composable
fun FewCloudsDayPreview() {
    val day = day.copy(weatherId = 0, clouds = 30f)
    CompositionLocalProvider(LocalSettings provides settings) {
        DayWeatherIcon(day = day)
    }
}

@Preview(widthDp = 50, heightDp = 50)
@Composable
fun CloudyDayPreview() {
    val day = day.copy(weatherId = 0, clouds = 80f)
    CompositionLocalProvider(LocalSettings provides settings) {
        DayWeatherIcon(day = day)
    }
}

@Preview(widthDp = 50, heightDp = 50)
@Composable
fun RainyDayPreview() {
    val day = day.copy(weatherId = 3)
    CompositionLocalProvider(LocalSettings provides settings) {
        DayWeatherIcon(day = day)
    }
}

@Preview(widthDp = 50, heightDp = 50)
@Composable
fun StormDayPreview() {
    val day = day.copy(weatherId = 20005)
    CompositionLocalProvider(LocalSettings provides settings) {
        DayWeatherIcon(day = day)
    }
}
