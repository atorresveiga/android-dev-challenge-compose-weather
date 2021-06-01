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
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.model.DayForecast
import com.example.androiddevchallenge.model.Forecast
import com.example.androiddevchallenge.model.HourForecast
import com.example.androiddevchallenge.ui.BlueCloudDestinations
import com.example.androiddevchallenge.ui.Information
import com.example.androiddevchallenge.ui.LocalDataFormatter
import com.example.androiddevchallenge.ui.Result
import com.example.androiddevchallenge.ui.location.BlueCloudButton
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.max
import kotlin.math.roundToInt

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ForecastScreen(
    viewModel: ForecastViewModel,
    navController: NavController
) {
    val state = viewModel.uiState.collectAsState().value
    val onSelectLocation = { navController.navigate(BlueCloudDestinations.LOCATION_ROUTE) }
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

                    ForecastScreen(
                        forecast = state.forecast.data,
                        onSelectLocation = onSelectLocation,
                        modifier = Modifier.alpha(1f - alpha)
                    )
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
fun ForecastScreen(
    forecast: Forecast,
    modifier: Modifier = Modifier,
    onSelectLocation: () -> Unit = {}
) {
    val indexForecast = IndexForecast(forecast)
    Box(
        modifier = modifier.fillMaxSize()
    ) {

        val (index, onIndexChange) = remember { mutableStateOf(0) }
        val (direction, onDirectionChange) = remember { mutableStateOf(Direction.FORWARD) }

        HourNavigation(indexForecast.hourly, index, onIndexChange, onDirectionChange)

        val selectedHour = indexForecast.hourly[index]
        val currentDay = indexForecast.getDayForecast(selectedHour.datetime)

        Sky(
            currentDay = currentDay,
            currentHour = selectedHour,
            direction = direction,
            timezoneId = indexForecast.location.timezoneId,
            isSouthernHemisphere = indexForecast.location.latitude < 0
        )

        WeatherInformation(
            name = indexForecast.location.name,
            weatherId = currentDay.weatherId,
            hourForecast = selectedHour,
            minTemperature = currentDay.minTemperature,
            maxTemperature = currentDay.maxTemperature,
            timezoneId = indexForecast.location.timezoneId,
            modifier = Modifier.align(Alignment.Center),
            onSelectLocation = onSelectLocation
        )

        PrecipitationInformation(
            selectedHour,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(start = 16.dp, bottom = 16.dp)
        )

        WindIndicator(
            selectedHour.windDegrees,
            selectedHour.windSpeed,
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .systemBarsPadding()
                .padding(end = 8.dp, top = 8.dp)
        )
    }
}

@Composable
fun HourNavigation(
    hourlyForecast: List<HourForecast>,
    selected: Int,
    onSelectedChange: (index: Int) -> Unit,
    onDirectionChange: (direction: Direction) -> Unit
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthPx = with(density) { screenWidth.toPx() }

    // Get the selected hour offset
    val selectedOffset = -1 * selected * screenWidthPx / (hourlyForecast.size - 1)

    // Initialize offset in the selected hour offset
    var offset by remember { mutableStateOf(selectedOffset) }

    Spacer(
        Modifier
            .fillMaxSize()
            .scrollable(
                orientation = Orientation.Horizontal,
                // Scrollable state: describes how to consume
                // scrolling delta and update offset (max offset to screenWidthPx)
                state = rememberScrollableState { delta ->
                    offset = (delta / 8 + offset).coerceIn(-1 * screenWidthPx, 0f)
                    val index =
                        (-1 * (hourlyForecast.size - 1) * offset / screenWidthPx).roundToInt()
                    onSelectedChange(index)
                    val direction = if (delta < 0) Direction.FORWARD else Direction.BACKWARD
                    onDirectionChange(direction)
                    delta / 8
                }
            )
            .verticalScroll(state = rememberScrollState())
    )
}

enum class Direction { FORWARD, BACKWARD }

@Composable
fun PrecipitationInformation(hourForecast: HourForecast, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = LocalDataFormatter.current.precipitation.getIntensityString(
                hourForecast.weatherId,
                hourForecast.pop
            )
        )
        Text(
            text = LocalDataFormatter.current.precipitation.getVolume(
                max(
                    hourForecast.snow,
                    hourForecast.rain
                )
            )
        )
    }
}

class IndexForecast(forecast: Forecast) {
    val location = forecast.location
    private val daily = forecast.daily
    val hourly = forecast.hourly

    private val dayIndex: HashMap<Long, Int> = hashMapOf()

    init {
        daily.forEachIndexed { index, dayForecast ->
            val timezone = TimeZone.of(location.timezoneId)
            val date = Instant.fromEpochSeconds(dayForecast.datetime).toLocalDateTime(timezone).date
            for (hour in hourly) {
                val hourDate =
                    Instant.fromEpochSeconds(hour.datetime).toLocalDateTime(timezone).date
                if (date == hourDate) {
                    dayIndex[hour.datetime] = index
                }
            }
        }
    }

    fun getDayForecast(hourDatetime: Long): DayForecast =
        dayIndex[hourDatetime]?.let { daily[it] }
            ?: throw IllegalArgumentException("Hour not belongs to current forecast")
}
