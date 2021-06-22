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

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.model.DayForecast
import com.example.androiddevchallenge.model.Forecast
import com.example.androiddevchallenge.model.HourForecast
import com.example.androiddevchallenge.ui.ForecastDisplayView
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun HourlyForecastScreen(
    forecast: Forecast,
    modifier: Modifier = Modifier,
    onSelectLocation: () -> Unit = {},
    updateSettings: () -> Unit,
    forecastDisplayView: ForecastDisplayView,
    onDisplayViewChange: (view: ForecastDisplayView) -> Unit,
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

        DailyHourlyForecast(
            forecastDisplayView = forecastDisplayView,
            onDisplayViewChange = onDisplayViewChange,
            modifier = Modifier
                .statusBarsPadding()
                .padding(
                    top = 4.dp,
                    start = dimensionResource(id = R.dimen.small_horizontal_padding)
                )
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
                .padding(
                    start = dimensionResource(id = R.dimen.horizontal_padding),
                    bottom = 16.dp
                )
        )

        WindIndicator(
            selectedHour.windDegrees,
            selectedHour.windSpeed,
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier
                .navigationBarsPadding()
                .align(Alignment.BottomEnd)
                .padding(
                    end = dimensionResource(id = R.dimen.horizontal_padding),
                    bottom = 16.dp
                )
        )

        UpdateSettingsButton(
            updateSettings = updateSettings,
            modifier = Modifier
                .statusBarsPadding()
                .padding(
                    top = 4.dp,
                    end = dimensionResource(id = R.dimen.small_horizontal_padding)
                )
                .size(48.dp)
                .align(Alignment.TopEnd)
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
            text = LocalSettings.current.dataFormatter.precipitation.getIntensityString(
                hourForecast.weatherId, hourForecast.pop
            )
        )
        Text(
            text = LocalSettings.current.dataFormatter.precipitation.getVolume(
                max(hourForecast.snow, hourForecast.rain)
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
