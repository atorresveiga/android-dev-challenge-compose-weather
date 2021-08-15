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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.atorresveiga.bluecloud.model.Forecast
import com.atorresveiga.bluecloud.ui.common.ScreenSize
import com.atorresveiga.bluecloud.ui.common.forecast
import com.atorresveiga.bluecloud.ui.common.getScreenSize
import com.atorresveiga.bluecloud.ui.common.settings
import com.atorresveiga.bluecloud.ui.forecast.ForecastView
import com.atorresveiga.bluecloud.ui.forecast.LocalSettings
import com.atorresveiga.bluecloud.ui.forecast.generateRandomWeatherOffsets
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme
import com.google.accompanist.insets.navigationBarsPadding

/**
 * DailyForecastScreen daily view of the selected forecast
 * @param forecast forecast to to display
 * @param modifier Modifier
 * @param onSelectLocation command to select a new location
 * @param onUpdateSettings command to change app settings
 * @param onForecastViewChange the callback that is triggered when the forecast display view change.
 * An updated [ForecastView] comes as a parameter of the callback
 * @param screenSize user device's [ScreenSize]
 */
@Composable
fun DailyForecastScreen(
    forecast: Forecast,
    modifier: Modifier = Modifier,
    onSelectLocation: () -> Unit = {},
    onUpdateSettings: () -> Unit = {},
    onForecastViewChange: (view: ForecastView) -> Unit = {},
    screenSize: ScreenSize = getScreenSize()
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val sceneHeight = with(LocalDensity.current) { maxHeight.toPx() }
        val dailyPrecipitation = LocalSettings.current.dailyPrecipitation
        val precipitation by remember {
            mutableStateOf(
                generateRandomWeatherOffsets(dailyPrecipitation)
            )
        }
        Column(
            modifier = modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DailyForecastTopBar(
                currentLocation = forecast.location.name,
                onSelectLocation = onSelectLocation,
                onUpdateSettings = onUpdateSettings,
                onForecastViewChange = onForecastViewChange,
                screenSize = screenSize
            )
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
                        DayListItem(
                            day = day,
                            timezoneId = forecast.location.timezoneId,
                            precipitation = precipitation,
                            sceneHeight = sceneHeight,
                            modifier = rowModifier,
                            screenSize = screenSize
                        )
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true, device = Devices.PIXEL_4)
@Composable
fun DailyForecastScreenPreview() {
    BlueCloudTheme {
        CompositionLocalProvider(LocalSettings provides settings) {
            Surface {
                DailyForecastScreen(forecast = forecast)
            }
        }
    }
}

@Preview(showSystemUi = true, device = Devices.PIXEL_C)
@Composable
fun DailyForecastScreenLargePreview() {
    BlueCloudTheme {
        Surface {
            CompositionLocalProvider(LocalSettings provides settings) {
                DailyForecastScreen(forecast = forecast, screenSize = ScreenSize.Large)
            }
        }
    }
}
