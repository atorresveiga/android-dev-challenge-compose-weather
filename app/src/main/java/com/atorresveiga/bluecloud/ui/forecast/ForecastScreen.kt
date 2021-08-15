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
package com.atorresveiga.bluecloud.ui.forecast

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.atorresveiga.bluecloud.R
import com.atorresveiga.bluecloud.data.Settings
import com.atorresveiga.bluecloud.ui.BlueCloudDestinations
import com.atorresveiga.bluecloud.ui.common.BlueCloudTitle
import com.atorresveiga.bluecloud.ui.common.BooleanProvider
import com.atorresveiga.bluecloud.ui.common.Information
import com.atorresveiga.bluecloud.ui.common.NoLocationFound
import com.atorresveiga.bluecloud.ui.common.forecast
import com.atorresveiga.bluecloud.ui.common.settings
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

/**
 * ForecastScreen screen to display selected location's forecast
 * @param viewModel forecast screen's view model
 * @param navController [NavController] to manages app navigation
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ForecastScreen(
    viewModel: ForecastViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val onSelectLocation = { navController.navigate(BlueCloudDestinations.LocationRoute) }
    val onUpdateSettings = { navController.navigate(BlueCloudDestinations.SettingsRoute) }

    ForecastScreen(
        uiState = uiState,
        onSelectLocation = onSelectLocation,
        onUpdateSettings = onUpdateSettings,
        onUpdateForecast = viewModel::displayCurrentForecast,
        onRefreshForecast = viewModel::onRefreshData
    )
}

val LocalSettings = compositionLocalOf<Settings> { error("No settings found!") }

@Composable
fun ForecastScreen(
    uiState: ForecastViewState,
    onSelectLocation: () -> Unit,
    onUpdateSettings: () -> Unit,
    onUpdateForecast: () -> Unit,
    onRefreshForecast: (forced: Boolean) -> Unit,
) {
    when (uiState) {
        is DisplayForecastState -> {
            val (forecastView, onForecastViewChange) = rememberSaveable {
                mutableStateOf(
                    ForecastView.values()[uiState.settings.defaultDisplayView]
                )
            }
            CompositionLocalProvider(LocalSettings provides uiState.settings) {
                DisplayForecast(
                    swipeRefreshState = rememberSwipeRefreshState(isRefreshing = uiState.isLoading),
                    forecast = uiState.forecast,
                    onSelectLocation = onSelectLocation,
                    onUpdateForecast = onUpdateForecast,
                    onRefresh = { onRefreshForecast(false) },
                    onUpdateSettings = onUpdateSettings,
                    forecastView = forecastView,
                    onForecastViewChange = onForecastViewChange
                )
            }
        }
        is NoLocationFoundState -> {
            NoLocationFound(onSelectLocation = onSelectLocation)
        }
        is CheckCurrentLocationState -> {
            Information {
                BlueCloudTitle(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    text = stringResource(id = R.string.initializing),
                    textAlign = TextAlign.Center
                )
            }
        }
        is LoadingForecastErrorState -> {
            LoadingForecastError(retry = { onRefreshForecast(true) })
        }
    }
}

@Preview(showSystemUi = true, device = Devices.PIXEL_4)
@Preview(showSystemUi = true, device = Devices.PIXEL_C)
@Composable
fun ForecastScreenPreview(@PreviewParameter(BooleanProvider::class) isDarkTheme: Boolean) {
    BlueCloudTheme(darkTheme = isDarkTheme) {
        Surface {
            ForecastScreen(
                uiState = DisplayForecastState(
                    isLoading = false,
                    forecast = forecast,
                    settings = settings
                ),
                onSelectLocation = {},
                onUpdateSettings = {},
                onUpdateForecast = {},
                onRefreshForecast = {},
            )
        }
    }
}
