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

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.rounded.North
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.South
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.model.Forecast
import com.example.androiddevchallenge.ui.BlueCloudDestinations
import com.example.androiddevchallenge.ui.BlueCloudTitle
import com.example.androiddevchallenge.ui.ForecastDisplayView
import com.example.androiddevchallenge.ui.Information
import com.example.androiddevchallenge.ui.Settings
import com.example.androiddevchallenge.ui.getLocationShortValue
import com.example.androiddevchallenge.ui.location.BlueCloudButton
import com.example.androiddevchallenge.ui.translatableString
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ForecastScreen(
    viewModel: ForecastViewModel,
    navController: NavController
) {
    val state = viewModel.uiState.collectAsState().value
    val onSelectLocation = { navController.navigate(BlueCloudDestinations.LOCATION_ROUTE) }
    val updateSettings = { navController.navigate(BlueCloudDestinations.SETTINGS_ROUTE) }

    when (state) {
        is DisplayForecast -> {
            val (forecastDisplay, onDisplayViewChange) = remember { mutableStateOf(state.settings.defaultDisplayView) }
            CompositionLocalProvider(LocalSettings provides state.settings) {
                DisplayForecast(
                    isRefreshing = state.isLoading,
                    forecast = state.forecast,
                    onSelectLocation = onSelectLocation,
                    updateForecast = { viewModel.displayCurrentForecast() },
                    onRefresh = { viewModel.onRefreshData() },
                    updateSettings = updateSettings,
                    forecastDisplayView = forecastDisplay,
                    onDisplayViewChange = onDisplayViewChange
                )
            }
        }
        is NoLocationFound -> {
            NoLocationFound(onSelectLocation = onSelectLocation)
        }
        is CheckCurrentLocation -> {
            Information {
                BlueCloudTitle(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    text = stringResource(id = R.string.initializing),
                    textAlign = TextAlign.Center
                )
            }
        }
        is LoadingForecastError -> {
            LoadingForecastError(retry = { viewModel.onRefreshData(force = true) })
        }
    }
}

val LocalSettings = compositionLocalOf<Settings> { error("No settings found!") }

@Composable
fun MaxMinTemperature(
    min: Float,
    max: Float,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.h5
) {
    Row(
        modifier = modifier.padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.South,
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(end = 16.dp),
            text = LocalSettings.current.dataFormatter.temperature.getValue(min),
            style = style
        )
        Icon(
            imageVector = Icons.Rounded.North,
            contentDescription = null
        )
        Text(
            text = LocalSettings.current.dataFormatter.temperature.getValue(max),
            style = style
        )
    }
}

@Composable
fun SelectLocation(
    currentLocationName: String,
    modifier: Modifier = Modifier,
    onSelectLocation: () -> Unit = {},
    style: TextStyle = MaterialTheme.typography.h5
) {
    Row(
        modifier = modifier
            .clickable { onSelectLocation() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Place,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = currentLocationName.getLocationShortValue(),
            style = style
        )
    }
}

@Composable
fun SmallSelectDailyHourlyForecast(
    forecastDisplayView: ForecastDisplayView,
    onDisplayViewChange: (view: ForecastDisplayView) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) { // Anchor view
            Text(
                text = forecastDisplayView.translatableString(),
                style = MaterialTheme.typography.h6
            ) // City name label
            Icon(
                modifier = Modifier.padding(top = 8.dp),
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ForecastDisplayView.values().forEach {
                DropdownMenuItem(
                    onClick = {
                        onDisplayViewChange(it)
                        expanded = false
                    }
                ) {
                    Text(
                        text = it.translatableString(),
                        style = MaterialTheme.typography.h6
                    )
                }
            }
        }
    }
}

@Composable
fun LargeSelectDailyHourlyForecast(
    forecastDisplayView: ForecastDisplayView,
    onDisplayViewChange: (view: ForecastDisplayView) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.hourly),
            style = MaterialTheme.typography.h5,
            modifier = Modifier
                .clickable { onDisplayViewChange(ForecastDisplayView.Hourly) }
                .padding(8.dp)
                .alpha(if (forecastDisplayView == ForecastDisplayView.Hourly) 1f else .5f)
        )
        Text(
            text = "/",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.alpha(.5f)
        )
        Text(
            text = stringResource(R.string.daily),
            style = MaterialTheme.typography.h5,
            modifier = Modifier
                .clickable { onDisplayViewChange(ForecastDisplayView.Daily) }
                .padding(8.dp)
                .alpha(if (forecastDisplayView == ForecastDisplayView.Daily) 1f else .5f)
        )
    }
}

@Composable
fun DailyHourlyForecast(
    forecastDisplayView: ForecastDisplayView,
    onDisplayViewChange: (view: ForecastDisplayView) -> Unit,
    modifier: Modifier = Modifier
) {
    if (booleanResource(id = R.bool.is_large_display)) {
        LargeSelectDailyHourlyForecast(
            forecastDisplayView = forecastDisplayView,
            onDisplayViewChange = onDisplayViewChange,
            modifier = modifier
        )
    } else {
        SmallSelectDailyHourlyForecast(
            forecastDisplayView = forecastDisplayView,
            onDisplayViewChange = onDisplayViewChange,
            modifier = modifier
        )
    }
}

@Composable
fun DisplayForecast(
    isRefreshing: Boolean,
    forecast: Forecast?,
    forecastDisplayView: ForecastDisplayView,
    onDisplayViewChange: (view: ForecastDisplayView) -> Unit,
    onRefresh: () -> Unit,
    updateForecast: () -> Unit,
    onSelectLocation: () -> Unit,
    updateSettings: () -> Unit
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    val refreshTriggerDistance = 80.dp
    val alpha =
        swipeRefreshState.indicatorOffset / with(LocalDensity.current) { refreshTriggerDistance.toPx() }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { onRefresh() },
        modifier = Modifier.fillMaxSize(),
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
                        updateForecast()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
            }

            when (forecastDisplayView) {
                ForecastDisplayView.Daily -> {
                    DailyForecastScreen(
                        forecast = forecast,
                        onSelectLocation = onSelectLocation,
                        modifier = Modifier.alpha(1f - alpha),
                        updateSettings = updateSettings,
                        forecastDisplayView = forecastDisplayView,
                        onDisplayViewChange = onDisplayViewChange
                    )
                }
                ForecastDisplayView.Hourly -> {
                    HourlyForecastScreen(
                        forecast = forecast,
                        onSelectLocation = onSelectLocation,
                        modifier = Modifier.alpha(1f - alpha),
                        updateSettings = updateSettings,
                        forecastDisplayView = forecastDisplayView,
                        onDisplayViewChange = onDisplayViewChange
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

@Composable
fun NoLocationFound(onSelectLocation: () -> Unit) {
    Information {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BlueCloudTitle(
                modifier = Modifier
                    .padding(top = 16.dp),
                text = stringResource(id = R.string.without_location),
                textAlign = TextAlign.Center
            )
            BlueCloudButton(
                onClick = { onSelectLocation() },
                modifier = Modifier
                    .padding(top = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Place,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(text = stringResource(id = R.string.select_location_action))
            }
        }
    }
}

@Composable
fun LoadingForecastError(retry: () -> Unit) {
    Information {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BlueCloudTitle(
                modifier = Modifier
                    .padding(top = 16.dp),
                text = stringResource(id = R.string.loading_forecast_error),
                textAlign = TextAlign.Center
            )
            BlueCloudButton(
                onClick = { retry() },
                modifier = Modifier
                    .padding(top = 24.dp)
            ) {
                Text(text = stringResource(id = R.string.update_forecast))
            }
        }
    }
}

@Composable
fun UpdateSettingsButton(updateSettings: () -> Unit, modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Rounded.Settings,
        contentDescription = null,
        modifier = modifier
            .clickable { updateSettings() }
            .padding(8.dp)

    )
}
