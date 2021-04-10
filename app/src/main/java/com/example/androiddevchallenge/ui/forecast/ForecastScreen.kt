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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.North
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.South
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.model.DayForecast
import com.example.androiddevchallenge.model.Forecast
import com.example.androiddevchallenge.model.HourForecast
import com.example.androiddevchallenge.ui.LocalDataFormatter
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.systemBarsPadding
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun ForecastScreen(forecast: Forecast) {
    val indexForecast = IndexForecast(forecast)
    Box(modifier = Modifier.fillMaxSize()) {

        val (index, onIndexChange) = remember { mutableStateOf(0) }
        HourNavigation(indexForecast.hourly, index, onIndexChange)

        val selectedHour = indexForecast.hourly[index]
        val currentDay = indexForecast.getDayForecast(selectedHour.datetime)

        MainInformation(
            timezone = indexForecast.location.timezone,
            weatherId = currentDay.weatherId,
            hourForecast = selectedHour,
            minTemperature = currentDay.minTemperature,
            maxTemperature = currentDay.maxTemperature,
            modifier = Modifier.align(Alignment.Center)
        )
        Precipitation(
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
fun MainInformation(
    timezone: String,
    weatherId: Int,
    minTemperature: Float,
    maxTemperature: Float,
    hourForecast: HourForecast,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Place,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = LocalDataFormatter.current.timezone.getValue(timezone),
                style = MaterialTheme.typography.h5
            )
        }
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = LocalDataFormatter.current.date.getDateHour(datetime = hourForecast.datetime)
        )
        Text(
            modifier = Modifier.offset(x = 16.dp),
            text = LocalDataFormatter.current.temperature.getValue(hourForecast.temperature),
            style = MaterialTheme.typography.h1

        )
        Text(
            text = stringResource(
                R.string.feels_like_uvi,
                LocalDataFormatter.current.temperature.getValue(hourForecast.feelsLike),
                LocalDataFormatter.current.uvi.getValue(hourForecast.uvi)
            ),
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = LocalDataFormatter.current.weather.getWeatherFullText(weatherId),
            style = MaterialTheme.typography.h5
        )
        Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.South,
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(end = 16.dp),
                text = LocalDataFormatter.current.temperature.getValue(minTemperature),
                style = MaterialTheme.typography.h5
            )
            Icon(
                imageVector = Icons.Rounded.North,
                contentDescription = null
            )
            Text(
                text = LocalDataFormatter.current.temperature.getValue(maxTemperature),
                style = MaterialTheme.typography.h5
            )
        }
    }
}

@Composable
fun Precipitation(hourForecast: HourForecast, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = LocalDataFormatter.current.precipitation.getIntensity(
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

@Composable
fun EmptyForecast(onRefreshData: () -> Unit) {
    Button(onClick = { onRefreshData() }) {
        Text(text = "Refresh data")
    }
}

class IndexForecast(forecast: Forecast) {
    val location = forecast.location
    private val daily = forecast.daily
    val hourly = forecast.hourly

    private val dayIndex: HashMap<Long, Int> = hashMapOf()

    init {
        daily.forEachIndexed { index, dayForecast ->
            val timezone = TimeZone.currentSystemDefault()
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
