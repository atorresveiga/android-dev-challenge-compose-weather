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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.model.HourForecast
import com.example.androiddevchallenge.ui.LocalDataFormatter
import java.util.Locale

@Composable
fun WeatherInformation(
    name: String,
    weatherId: Int,
    minTemperature: Float,
    maxTemperature: Float,
    hourForecast: HourForecast,
    timezoneId: String,
    modifier: Modifier = Modifier,
    onSelectLocation: () -> Unit = {},
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        SelectLocation(currentLocationName = name, onSelectLocation = onSelectLocation)
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = LocalDataFormatter.current.weather.getWeatherFullText(weatherId)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
            style = MaterialTheme.typography.h5
        )
        MaxMinTemperature(min = minTemperature, max = maxTemperature)
        Text(
            modifier = Modifier.offset(x = 8.dp),
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
            modifier = Modifier.padding(top = 8.dp),
            text = LocalDataFormatter.current.date.getDateHour(
                datetime = hourForecast.datetime,
                timezoneId = timezoneId
            )
        )
    }
}
