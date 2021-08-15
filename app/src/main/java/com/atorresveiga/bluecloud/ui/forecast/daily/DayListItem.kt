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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.model.DayForecast
import com.atorresveiga.bluecloud.ui.common.ScreenSize
import com.atorresveiga.bluecloud.ui.common.day
import com.atorresveiga.bluecloud.ui.common.getScreenSize
import com.atorresveiga.bluecloud.ui.common.location1
import com.atorresveiga.bluecloud.ui.common.location2
import com.atorresveiga.bluecloud.ui.common.settings
import com.atorresveiga.bluecloud.ui.forecast.LocalSettings
import com.atorresveiga.bluecloud.ui.forecast.MaxMinTemperature
import com.atorresveiga.bluecloud.ui.forecast.Precipitation
import com.atorresveiga.bluecloud.ui.forecast.WeatherOffset
import com.atorresveiga.bluecloud.ui.forecast.generateRandomWeatherOffsets
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme
import com.atorresveiga.bluecloud.ui.theme.overlay
import java.util.Locale

/**
 * DayListItem daily view of the selected forecast
 * @param day day to to display
 * @param timezoneId forecast location's timezoneId
 * @param modifier Modifier
 * @param precipitation list of precipitation's weather offset to draw in a day with precipitations
 * @param sceneHeight height of the scene to calculate (rain drop / snowflake) size
 * @param screenSize user device's [ScreenSize]
 */
@Composable
fun DayListItem(
    day: DayForecast,
    timezoneId: String,
    modifier: Modifier = Modifier,
    precipitation: List<WeatherOffset> = generateRandomWeatherOffsets(20),
    sceneHeight: Float = -1f,
    screenSize: ScreenSize = getScreenSize()
) {
    if (screenSize == ScreenSize.Large) {
        LargeDayListItem(
            day = day,
            timezoneId = timezoneId,
            precipitation = precipitation,
            sceneHeight = sceneHeight,
            modifier = modifier
        )
    } else {
        SmallDayListItem(
            day = day,
            timezoneId = timezoneId,
            precipitation = precipitation,
            sceneHeight = sceneHeight,
            modifier = modifier
        )
    }
}

@Composable
fun LargeDayListItem(
    day: DayForecast,
    timezoneId: String,
    precipitation: List<WeatherOffset>,
    sceneHeight: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {

        if (LocalSettings.current.dataFormatter.precipitation.isPrecipitation(day.weatherId)) {
            val alpha =
                LocalSettings.current.dataFormatter.precipitation.getIntensity(day.weatherId)
                    .coerceAtMost(.7f)
            Precipitation(
                weatherId = day.weatherId,
                windDegrees = 0f,
                windSpeed = 0f,
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
                    text = LocalSettings.current.dataFormatter.date.getReadableDate(
                        datetime = day.datetime,
                        timezoneId = timezoneId
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
                    text = LocalSettings.current.dataFormatter.weather.getWeatherFullText(
                        day.weatherId
                    )
                        .replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.getDefault()
                            ) else it.toString()
                        }
                )
                DayWeatherIcon(
                    day = day,
                    modifier = Modifier.size(200.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MaxMinTemperature(
                    min = day.minTemperature,
                    max = day.maxTemperature,
                    modifier = modifier.padding(top = 8.dp)
                )
            }
        }
        Divider(thickness = 1.dp)
    }
}

@Composable
fun SmallDayListItem(
    day: DayForecast,
    timezoneId: String,
    precipitation: List<WeatherOffset>,
    sceneHeight: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {

        if (LocalSettings.current.dataFormatter.precipitation.isPrecipitation(day.weatherId)) {
            val alpha =
                LocalSettings.current.dataFormatter.precipitation.getIntensity(day.weatherId)
                    .coerceAtMost(.7f)
            Precipitation(
                weatherId = day.weatherId,
                windDegrees = 0f,
                windSpeed = 0f,
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
                    modifier = Modifier
                        .padding(top = 8.dp, start = 16.dp)
                        .fillMaxWidth(),
                    text = LocalSettings.current.dataFormatter.weather.getWeatherFullText(
                        day.weatherId
                    )
                        .replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.getDefault()
                            ) else it.toString()
                        }
                )
                DayWeatherIcon(
                    day = day,
                    modifier = Modifier.size(200.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = LocalSettings.current.dataFormatter.date.getReadableDate(
                        datetime = day.datetime,
                        timezoneId = timezoneId
                    ),
                    style = MaterialTheme.typography.h6
                )
                MaxMinTemperature(
                    min = day.minTemperature,
                    max = day.maxTemperature,
                    modifier = modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.h6
                )
            }
        }
        Divider(thickness = 1.dp)
    }
}

@Preview(widthDp = 360, heightDp = 220)
@Composable
fun DayListItemPreview() {
    BlueCloudTheme {
        Surface {
            CompositionLocalProvider(LocalSettings provides settings) {
                DayListItem(
                    day = day,
                    timezoneId = location1.timezoneId
                )
            }
        }
    }
}

@Preview(widthDp = 600, heightDp = 220)
@Composable
fun DayListItemPreviewLarge() {
    BlueCloudTheme {
        Surface {
            CompositionLocalProvider(LocalSettings provides settings) {
                DayListItem(
                    day = day,
                    timezoneId = location2.timezoneId,
                    screenSize = ScreenSize.Large
                )
            }
        }
    }
}
