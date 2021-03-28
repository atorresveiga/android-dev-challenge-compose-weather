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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.model.Forecast
import com.example.androiddevchallenge.model.HourForecast
import com.example.androiddevchallenge.ui.LocalDataFormatter
import kotlin.math.roundToInt

@Composable
fun ForecastScreen(forecast: Forecast) {
    Box(modifier = Modifier.fillMaxSize()) {

        val (index, onIndexChange) = remember { mutableStateOf(0) }
        HourNavigation(forecast.hourly, index, onIndexChange)

        val selectedHour = forecast.hourly[index]

        CurrentTemperature(selectedHour, modifier = Modifier.align(Alignment.Center))
        Precipitation(
            selectedHour,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 16.dp)
        )
        WindIndicator(
            selectedHour.windDegrees,
            selectedHour.windSpeed,
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 8.dp, top = 8.dp)
        )
    }
}

@Composable
fun HourNavigation(
    hourlyForecast: List<HourForecast>,
    selected: Int,
    onSelectedChange: (index: Int) -> Unit
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
                    offset = (delta / 4 + offset).coerceIn(-1 * screenWidthPx, 0f)
                    val index =
                        (-1 * (hourlyForecast.size - 1) * offset / screenWidthPx).roundToInt()
                    onSelectedChange(index)
                    delta / 4
                }
            )
    )
}

@Composable
fun CurrentTemperature(hourForecast: HourForecast, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = LocalDataFormatter.current.date.getDateHour(datetime = hourForecast.datetime))
        Text(
            modifier = Modifier.offset(x = 16.dp),
            text = LocalDataFormatter.current.temperature.getValue(hourForecast.temperature),
            style = MaterialTheme.typography.h1
        )
        Text(
            text = stringResource(
                R.string.feels_like,
                LocalDataFormatter.current.temperature.getValue(hourForecast.feelsLike)
            )
        )
    }
}

@Composable
fun Precipitation(hourForecast: HourForecast, modifier: Modifier = Modifier) {

    Column(modifier = modifier) {
        if (hourForecast.rain < hourForecast.snow) {
            val type = stringResource(R.string.snow)
            Text(
                text = LocalDataFormatter.current.precipitation.getIntensity(
                    hourForecast.snow,
                    hourForecast.pop,
                    type
                )
            )
            Text(text = LocalDataFormatter.current.precipitation.getVolume(hourForecast.snow))
        } else {
            val type = stringResource(R.string.rain)
            Text(
                text = LocalDataFormatter.current.precipitation.getIntensity(
                    hourForecast.rain,
                    hourForecast.pop,
                    type
                )
            )
            Text(text = LocalDataFormatter.current.precipitation.getVolume(hourForecast.rain))
        }
    }
}

@Composable
fun EmptyForecast(onRefreshData: () -> Unit) {
    Button(onClick = { onRefreshData() }) {
        Text(text = "Refresh data")
    }
}
