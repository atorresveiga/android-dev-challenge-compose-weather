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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.model.DayForecast
import com.example.androiddevchallenge.model.Forecast
import com.example.androiddevchallenge.ui.LocalDataFormatter
import com.example.androiddevchallenge.ui.theme.cloudColor
import com.example.androiddevchallenge.ui.theme.lightningColor
import com.example.androiddevchallenge.ui.theme.overlay
import com.example.androiddevchallenge.ui.theme.sunColor
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import java.util.Locale

@Composable
fun DailyForecastScreen(
    forecast: Forecast,
    modifier: Modifier = Modifier,
    onSelectLocation: () -> Unit = {},
    isDailyForecastSelected: Boolean = true,
    onDisplayForecastChange: (displayDailyForecast: Boolean) -> Unit = { }
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val sceneHeight = with(LocalDensity.current) { maxHeight.toPx() }
        val precipitation by remember { mutableStateOf(generateRandomWeatherOffsets(80)) }
        Column(
            modifier = modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(
                modifier = modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                ) {
                    DisplayDailyHourlyForecast(
                        isDailySelected = isDailyForecastSelected,
                        onDisplayForecastChange = onDisplayForecastChange
                    )
                    SelectLocation(
                        modifier = Modifier.align(Alignment.Center),
                        currentLocationName = forecast.location.name,
                        onSelectLocation = onSelectLocation
                    )
                }
            }
            LazyColumn(
                modifier = modifier.fillMaxHeight()
            ) {
                itemsIndexed(forecast.daily) { index, day ->

                    var rowModifier = Modifier
                        .clipToBounds()
                        .fillMaxWidth()

                    if (index == forecast.daily.lastIndex) {
                        rowModifier = rowModifier.navigationBarsPadding()
                    }

                    key(day.datetime) {
                        Box(
                            modifier = rowModifier,
                            contentAlignment = Alignment.TopCenter
                        ) {

                            if (LocalDataFormatter.current.precipitation.isPrecipitation(day.weatherId)) {
                                val alpha =
                                    LocalDataFormatter.current.precipitation.getIntensity(day.weatherId)
                                        .coerceAtMost(.7f)
                                Precipitation(
                                    weatherId = day.weatherId,
                                    windDegrees = day.windDegrees,
                                    windSpeed = day.windSpeed,
                                    precipitation = precipitation,
                                    sceneHeight = sceneHeight,
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(color = MaterialTheme.colors.overlay.copy(alpha = alpha))
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .widthIn(max = 600.dp)
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        modifier = Modifier.padding(top = 8.dp),
                                        text = LocalDataFormatter.current.date.getDate(
                                            datetime = day.datetime,
                                            timezoneId = forecast.location.timezoneId
                                        ),
                                        style = MaterialTheme.typography.h5
                                    )
                                }

                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        modifier = Modifier.padding(top = 8.dp),
                                        text = LocalDataFormatter.current.weather.getWeatherFullText(
                                            day.weatherId
                                        )
                                            .replaceFirstChar {
                                                if (it.isLowerCase()) it.titlecase(
                                                    Locale.getDefault()
                                                ) else it.toString()
                                            }
                                    )
                                    DayWeatherIcon(
                                        dayForecast = day,
                                        modifier = Modifier.size(200.dp)
                                    )
                                }

                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    MaxMinTemperature(
                                        min = day.minTemperature,
                                        max = day.maxTemperature
                                    )
                                }
                            }
                            Divider(thickness = 1.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayWeatherIcon(dayForecast: DayForecast, modifier: Modifier) {
    val hasThunders = LocalDataFormatter.current.weather.hasThunders(dayForecast.weatherId)
    val isPrecipitation =
        LocalDataFormatter.current.precipitation.isPrecipitation(dayForecast.weatherId)
    val isCloudy = dayForecast.clouds > 45

    if (hasThunders || isPrecipitation || isCloudy) {
        TwoCloudsIcon(
            color = if (isPrecipitation || hasThunders) Color.LightGray else MaterialTheme.colors.cloudColor,
            hasLightning = hasThunders,
            modifier = modifier
        )
    } else {
        SunIcon(modifier = modifier, showCloud = dayForecast.clouds > 20)
    }
}

@Composable
fun TwoCloudsIcon(color: Color, hasLightning: Boolean, modifier: Modifier = Modifier) {
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

@Composable
fun SunIcon(modifier: Modifier = Modifier, showCloud: Boolean = false) {
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
        if (showCloud) {
            Cloud(
                color = MaterialTheme.colors.cloudColor,
                modifier = Modifier
                    .size(width = width, height = height)
                    .offset(x = maxWidth * .35f, y = maxHeight * .35f)
            )
        }
    }
}
