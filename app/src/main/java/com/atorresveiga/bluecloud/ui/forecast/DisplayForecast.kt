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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.atorresveiga.bluecloud.R
import com.atorresveiga.bluecloud.model.Forecast
import com.atorresveiga.bluecloud.ui.common.BlueCloudTitle
import com.atorresveiga.bluecloud.ui.common.Information
import com.atorresveiga.bluecloud.ui.common.forecast
import com.atorresveiga.bluecloud.ui.common.settings
import com.atorresveiga.bluecloud.ui.forecast.daily.DailyForecastScreen
import com.atorresveiga.bluecloud.ui.forecast.hourly.HourlyForecastScreen
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

/**
 * DisplayForecast current location forecast wrapped in SwipeRefresh composable
 * @param swipeRefreshState state object that can be hoisted to control and observe changes for [SwipeRefresh]
 * @param forecast forecast to display
 * @param forecastView how we want to display the forecast Hourly or Daily [ForecastView]
 * @param modifier Modifier
 * @param onSelectLocation command to select a new location
 * @param onUpdateSettings command to change app settings
 * @param onForecastViewChange the callback that is triggered when the forecast display view change.
 * An updated [ForecastView] comes as a parameter of the callback.
 * @param onRefresh command to refresh forecast data from network
 * @param onUpdateForecast get up to date forecast data
 */
@Composable
fun DisplayForecast(
    swipeRefreshState: SwipeRefreshState,
    forecast: Forecast?,
    forecastView: ForecastView,
    modifier: Modifier = Modifier,
    onSelectLocation: () -> Unit = {},
    onUpdateSettings: () -> Unit = {},
    onForecastViewChange: (view: ForecastView) -> Unit = {},
    onRefresh: () -> Unit = {},
    onUpdateForecast: () -> Unit = {},
) {
    val refreshTriggerDistance = 80.dp
    val alpha =
        swipeRefreshState.indicatorOffset / with(LocalDensity.current) { refreshTriggerDistance.toPx() }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { onRefresh() },
        modifier = modifier.fillMaxSize(),
        refreshTriggerDistance = refreshTriggerDistance,
        indicator = { s, trigger ->
            SwipeRefreshIndicator(
                state = s,
                refreshTriggerDistance = trigger,
                refreshingOffset = 32.dp
            )
        }
    ) {
        if (forecast != null) {
            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = object : DefaultLifecycleObserver {
                    override fun onResume(owner: LifecycleOwner) {
                        onUpdateForecast()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
            }

            when (forecastView) {
                ForecastView.DailyView -> {
                    DailyForecastScreen(
                        forecast = forecast,
                        onSelectLocation = onSelectLocation,
                        modifier = Modifier.alpha(1f - alpha),
                        onUpdateSettings = onUpdateSettings,
                        onForecastViewChange = onForecastViewChange
                    )
                }
                else -> {
                    HourlyForecastScreen(
                        forecast = forecast,
                        onSelectLocation = onSelectLocation,
                        modifier = Modifier.alpha(1f - alpha),
                        onUpdateSettings = onUpdateSettings,
                        onForecastViewChange = onForecastViewChange
                    )
                }
            }
        }
        Information(
            modifier = Modifier
                .alpha(if (swipeRefreshState.isRefreshing) 1f else alpha)
        ) {
            BlueCloudTitle(
                modifier = Modifier
                    .padding(top = 16.dp),
                text = stringResource(id = R.string.loading_forecast),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showSystemUi = true, device = Devices.PIXEL_4)
@Composable
fun DisplayForecastPreview() {
    BlueCloudTheme {
        Surface {
            CompositionLocalProvider(LocalSettings provides settings) {
                DisplayForecast(
                    swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false),
                    forecast = forecast,
                    forecastView = ForecastView.HourlyView
                )
            }
        }
    }
}

@Preview(showSystemUi = true, device = Devices.PIXEL_4)
@Composable
fun DisplayForecastLoadingPreview() {
    BlueCloudTheme {
        Surface {
            CompositionLocalProvider(LocalSettings provides settings) {
                DisplayForecast(
                    swipeRefreshState = rememberSwipeRefreshState(isRefreshing = true),
                    forecast = null,
                    forecastView = ForecastView.HourlyView
                )
            }
        }
    }
}
