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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.data.ACTIVE
import com.example.androiddevchallenge.data.FORWARD
import com.example.androiddevchallenge.data.IDLE
import com.example.androiddevchallenge.data.INACTIVE
import com.example.androiddevchallenge.model.DayForecast
import com.example.androiddevchallenge.model.Forecast
import com.example.androiddevchallenge.model.HourForecast
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun HourlyForecastScreen(
    forecast: Forecast,
    modifier: Modifier = Modifier,
    onSelectLocation: () -> Unit = {},
    updateSettings: () -> Unit,
    forecastDisplayView: Int,
    onDisplayViewChange: (view: Int) -> Unit,
) {
    val indexForecast = IndexForecast(forecast)

    Box(modifier = modifier.fillMaxSize()) {

        val (index, onIndexChange) = remember { mutableStateOf(0) }
        val (direction, onDirectionChange) = remember { mutableStateOf(FORWARD) }
        val (hourNavigationInteractionState, onHourNavigationInteractionChange) = remember {
            mutableStateOf(ACTIVE)
        }
        val selectedHour = indexForecast.hourly[index]
        val currentDay = indexForecast.getDayForecast(selectedHour.datetime)

        val weatherInfoModifier = if (booleanResource(id = R.bool.is_large_display)) {
            Modifier.align(Alignment.Center)
        } else {
            Modifier
                .padding(top = 160.dp)
                .align(Alignment.TopCenter)
        }

        val controlsAlpha by animateFloatAsState(
            targetValue = when (hourNavigationInteractionState) {
                INACTIVE -> .4f
                IDLE -> .1f
                else -> .6f
            }
        )

        val skyAlpha by animateFloatAsState(
            targetValue = when (hourNavigationInteractionState) {
                ACTIVE -> .4f
                else -> 1f
            }
        )

        HourNavigation(
            hourlyForecast = indexForecast.hourly,
            selected = index,
            onSelectedChange = onIndexChange,
            onDirectionChange = onDirectionChange,
            onHourNavigationInteractionChange = onHourNavigationInteractionChange
        )

        Sky(
            currentDayForecast = currentDay,
            currentHourForecast = selectedHour,
            direction = direction,
            timezoneId = indexForecast.location.timezoneId,
            isSouthernHemisphere = indexForecast.location.latitude < 0,
            modifier = Modifier.alpha(skyAlpha)
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
                .alpha(controlsAlpha)
        )

        WeatherInformation(
            locationName = indexForecast.location.name,
            datetime = selectedHour.datetime,
            weatherId = currentDay.weatherId,
            temperature = selectedHour.temperature,
            feelsLike = selectedHour.feelsLike,
            uvi = selectedHour.uvi,
            humidity = selectedHour.humidity,
            minTemperature = currentDay.minTemperature,
            maxTemperature = currentDay.maxTemperature,
            timezoneId = indexForecast.location.timezoneId,
            modifier = weatherInfoModifier.alpha(controlsAlpha),
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
                .alpha(controlsAlpha)
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
                .alpha(controlsAlpha)
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
                .alpha(controlsAlpha)
        )
    }
}

@Composable
fun PrecipitationInformation(hourForecast: HourForecast, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = LocalSettings.current.dataFormatter.precipitation.getIntensityString(
                hourForecast.weatherId, hourForecast.pop
            )
        )
        Text(
            text = LocalSettings.current.dataFormatter.precipitation.getVolume(hourForecast.precipitation)
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
