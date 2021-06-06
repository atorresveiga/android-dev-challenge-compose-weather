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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.North
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.South
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.ui.BlueCloudDestinations
import com.example.androiddevchallenge.ui.Information
import com.example.androiddevchallenge.ui.LocalDataFormatter
import com.example.androiddevchallenge.ui.Result
import com.example.androiddevchallenge.ui.location.BlueCloudButton
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
    val (displayDailyForecast, onDisplayForecastChange) = remember { mutableStateOf(false) }

    when (state) {
        is DisplayForecast -> {
            val isRefreshing = state.forecast is Result.Loading
            val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
            val refreshTriggerDistance = 80.dp
            val alpha =
                swipeRefreshState.indicatorOffset / with(LocalDensity.current) { refreshTriggerDistance.toPx() }

            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = { viewModel.onRefreshData() },
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
                if (state.forecast is Result.Success) {

                    val lifecycleOwner = LocalLifecycleOwner.current
                    DisposableEffect(lifecycleOwner) {
                        val observer = object : DefaultLifecycleObserver {
                            override fun onResume(owner: LifecycleOwner) {
                                viewModel.displayCurrentForecast()
                            }
                        }
                        lifecycleOwner.lifecycle.addObserver(observer)
                        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                    }

                    if (displayDailyForecast) {
                        DailyForecastScreen(
                            forecast = state.forecast.data,
                            onSelectLocation = onSelectLocation,
                            modifier = Modifier.alpha(1f - alpha),
                            isDailyForecastSelected = displayDailyForecast,
                            onDisplayForecastChange = onDisplayForecastChange
                        )
                    } else {
                        HourlyForecastScreen(
                            forecast = state.forecast.data,
                            onSelectLocation = onSelectLocation,
                            modifier = Modifier.alpha(1f - alpha),
                            isDailyForecastSelected = displayDailyForecast,
                            onDisplayForecastChange = onDisplayForecastChange
                        )
                    }
                }

                Information(
                    modifier = Modifier
                        .alpha(if (swipeRefreshState.isRefreshing) 1f else alpha)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(top = 16.dp),
                        text = stringResource(id = R.string.loading_forecast),
                        style = MaterialTheme.typography.h4,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        else -> {
            Information {
                if (state == NoLocationFound) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = 600.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(top = 16.dp),
                            text = stringResource(id = R.string.without_location),
                            style = MaterialTheme.typography.h4,
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
                } else {
                    Text(
                        modifier = Modifier
                            .padding(top = 16.dp),
                        text = stringResource(id = R.string.initializing),
                        style = MaterialTheme.typography.h4,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun MaxMinTemperature(min: Float, max: Float, modifier: Modifier = Modifier) {
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
            text = LocalDataFormatter.current.temperature.getValue(min),
            style = MaterialTheme.typography.h5
        )
        Icon(
            imageVector = Icons.Rounded.North,
            contentDescription = null
        )
        Text(
            text = LocalDataFormatter.current.temperature.getValue(max),
            style = MaterialTheme.typography.h5
        )
    }
}

@Composable
fun SelectLocation(
    currentLocationName: String,
    modifier: Modifier = Modifier,
    onSelectLocation: () -> Unit = {}
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
            text = LocalDataFormatter.current.location.getShortValue(currentLocationName),
            style = MaterialTheme.typography.h5
        )
    }
}

@Composable
fun DisplayDailyHourlyForecast(
    isDailySelected: Boolean,
    modifier: Modifier = Modifier,
    onDisplayForecastChange: (displayDailyForecast: Boolean) -> Unit = { }
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.daily),
            style = MaterialTheme.typography.h5,
            modifier = Modifier
                .clickable { onDisplayForecastChange(true) }
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .alpha(if (isDailySelected) 1f else .5f)
        )
        Text(
            text = "/",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.alpha(.5f)
        )
        Text(
            text = stringResource(R.string.hourly),
            style = MaterialTheme.typography.h5,
            modifier = Modifier
                .clickable { onDisplayForecastChange(false) }
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .alpha(if (isDailySelected) .5f else 1f)
        )
    }
}
