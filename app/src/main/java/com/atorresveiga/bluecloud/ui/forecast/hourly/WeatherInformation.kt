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
package com.atorresveiga.bluecloud.ui.forecast.hourly

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.R
import com.atorresveiga.bluecloud.ui.common.settings
import com.atorresveiga.bluecloud.ui.forecast.LocalSettings
import com.atorresveiga.bluecloud.ui.forecast.MaxMinTemperature
import com.atorresveiga.bluecloud.ui.location.SelectLocationButton
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme
import java.util.Locale

/**
 * WeatherInformation current hour weather's information
 * @param locationName forecast's location name
 * @param timezoneId forecast location's time zone id
 * @param datetime hour's datetime
 * @param weatherId hour's weatherId
 * @param minTemperature day's minimum temperature
 * @param maxTemperature day's maximum temperature
 * @param temperature current temperature
 * @param feelsLike current feels like temperature
 * @param humidity current humidity
 * @param uvi current uv index
 * @param hasNextHour if the user can navigate forward in the hourly forecast list
 * @param hasPreviousHour if the user can navigate backward in the hourly forecast list
 * @param interactionSource  [MutableInteractionSource] that will be used to dispatch events when the
 * user interacts with the composable
 * @param modifier Modifier
 * @param onSelectLocation command to select a new location
 * @param onMoveNextHour command to navigate to the next hour in the hourly forecast list
 * @param onMovePreviousHour command to navigate to the previous hour in the hourly forecast list
 */
@Composable
fun WeatherInformation(
    locationName: String,
    timezoneId: String,
    datetime: Long,
    weatherId: Int,
    minTemperature: Float,
    maxTemperature: Float,
    temperature: Float,
    feelsLike: Float,
    humidity: Float,
    uvi: Float,
    hasNextHour: Boolean,
    hasPreviousHour: Boolean,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
    onSelectLocation: () -> Unit = {},
    onMoveNextHour: () -> Unit = {},
    onMovePreviousHour: () -> Unit = {}
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        SelectLocationButton(
            currentLocationName = locationName,
            onSelectLocation = onSelectLocation
        )
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = LocalSettings.current.dataFormatter.date.getReadableDate(
                datetime = datetime,
                timezoneId = timezoneId
            ),
            style = MaterialTheme.typography.h5
        )
        Text(
            text = LocalSettings.current.dataFormatter.weather.getWeatherFullText(weatherId)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
            style = MaterialTheme.typography.h5
        )
        MaxMinTemperature(
            min = minTemperature,
            max = maxTemperature,
            modifier = Modifier.padding(top = 8.dp)
        )
        Hour(
            datetime = datetime,
            timezoneId = timezoneId,
            hasNextHour = hasNextHour,
            hasPreviousHour = hasPreviousHour,
            onMoveNextHour = onMoveNextHour,
            onMovePreviousHour = onMovePreviousHour,
            interactionSource = interactionSource
        )
        Text(
            text = stringResource(
                R.string.temperature_humidity,
                LocalSettings.current.dataFormatter.temperature.getValue(temperature),
                // Feels like's calculation is not accurate yet, so use humidity in the meantime
                LocalSettings.current.dataFormatter.humidity.getValue(humidity)
            ),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = stringResource(
                R.string.uvi,
                LocalSettings.current.dataFormatter.uvi.getValue(uvi),
                LocalSettings.current.dataFormatter.temperature.getValue(feelsLike)
            ),
            style = MaterialTheme.typography.h5
        )
    }
}

@Preview
@Composable
fun PreviewWeatherInformation() {
    BlueCloudTheme {
        Surface {
            CompositionLocalProvider(LocalSettings provides settings) {
                WeatherInformation(
                    locationName = "Belgrano",
                    timezoneId = "America/Argentina/Buenos_Aires",
                    datetime = 1621609658L,
                    weatherId = 1001,
                    minTemperature = -14.45f,
                    maxTemperature = -10.30f,
                    temperature = -12.56f,
                    feelsLike = -18.79f,
                    uvi = 4.5f,
                    humidity = 88.5f,
                    hasPreviousHour = true,
                    hasNextHour = true,
                    interactionSource = remember { MutableNavigationInteractionSource() }
                )
            }
        }
    }
}
