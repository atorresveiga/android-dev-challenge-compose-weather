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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.ui.common.ScreenSize
import com.atorresveiga.bluecloud.ui.common.getScreenSize
import com.atorresveiga.bluecloud.ui.forecast.ForecastView
import com.atorresveiga.bluecloud.ui.forecast.SelectForecastView
import com.atorresveiga.bluecloud.ui.location.SelectLocationButton
import com.atorresveiga.bluecloud.ui.settings.UpdateSettingsButton
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme
import com.google.accompanist.insets.statusBarsPadding

/**
 * DailyForecastTopBar top bar of [DailyForecastScreen]
 * @param currentLocation forecast's location
 * @param onSelectLocation command to select a new location
 * @param onUpdateSettings command to change app settings
 * @param onForecastViewChange the callback that is triggered when the forecast display view change.
 * An updated [ForecastView] comes as a parameter of the callback
 * @param modifier Modifier
 * @param screenSize user device's [ScreenSize]
 */
@Composable
fun DailyForecastTopBar(
    currentLocation: String,
    modifier: Modifier = Modifier,
    onSelectLocation: () -> Unit = {},
    onUpdateSettings: () -> Unit = {},
    onForecastViewChange: (view: ForecastView) -> Unit = {},
    screenSize: ScreenSize = getScreenSize()
) {
    if (screenSize == ScreenSize.Large) {
        LargeDailyForecastTopBar(
            currentLocation = currentLocation,
            onSelectLocation = onSelectLocation,
            onUpdateSettings = onUpdateSettings,
            onForecastViewChange = onForecastViewChange,
            modifier = modifier
        )
    } else {
        SmallDailyForecastTopBar(
            currentLocation = currentLocation,
            onSelectLocation = onSelectLocation,
            onUpdateSettings = onUpdateSettings,
            onForecastViewChange = onForecastViewChange,
            modifier = modifier
        )
    }
}

@Composable
fun SmallDailyForecastTopBar(
    currentLocation: String,
    onSelectLocation: () -> Unit,
    onUpdateSettings: () -> Unit,
    onForecastViewChange: (view: ForecastView) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        TopAppBar(
            Modifier
                .statusBarsPadding()
                .fillMaxWidth()
        ) {
            Box(modifier = modifier.fillMaxWidth()) {
                SelectForecastView(
                    forecastView = ForecastView.DailyView,
                    onForecastViewChange = onForecastViewChange,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .align(Alignment.CenterStart)
                )
                UpdateSettingsButton(
                    onUpdateSettings = onUpdateSettings,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(48.dp)
                        .align(Alignment.CenterEnd)
                )
            }
        }
        TopAppBar(
            backgroundColor = MaterialTheme.colors.background,
            elevation = 2.dp
        ) {
            Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                SelectLocationButton(
                    currentLocationName = currentLocation,
                    onSelectLocation = onSelectLocation,
                    style = MaterialTheme.typography.h6
                )
            }
        }
    }
}

@Composable
fun LargeDailyForecastTopBar(
    currentLocation: String,
    onSelectLocation: () -> Unit,
    onUpdateSettings: () -> Unit,
    onForecastViewChange: (view: ForecastView) -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxWidth()
    ) {
        Box(
            modifier = modifier.fillMaxWidth()
        ) {
            SelectForecastView(
                forecastView = ForecastView.DailyView,
                onForecastViewChange = onForecastViewChange,
                modifier = Modifier.padding(start = 12.dp)
            )
            SelectLocationButton(
                currentLocationName = currentLocation,
                onSelectLocation = onSelectLocation,
                modifier = Modifier.align(Alignment.Center)
            )
            UpdateSettingsButton(
                onUpdateSettings = onUpdateSettings,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(48.dp)
                    .align(Alignment.CenterEnd)
            )
        }
    }
}

@Preview(widthDp = 360, heightDp = 115)
@Composable
fun DailyForecastTopBar() {
    BlueCloudTheme {
        DailyForecastTopBar(currentLocation = "Location")
    }
}

@Preview(widthDp = 600, heightDp = 50)
@Composable
fun DailyForecastTopBarLarge() {
    BlueCloudTheme {
        DailyForecastTopBar(currentLocation = "Location", screenSize = ScreenSize.Large)
    }
}
