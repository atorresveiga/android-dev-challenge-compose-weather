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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.North
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.South
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
fun MainInformation(
    name: String,
    weatherId: Int,
    minTemperature: Float,
    maxTemperature: Float,
    hourForecast: HourForecast,
    modifier: Modifier = Modifier,
    onSelectLocation: () -> Unit = {}
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
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
                text = LocalDataFormatter.current.location.getShortValue(name),
                style = MaterialTheme.typography.h5
            )
        }
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = LocalDataFormatter.current.weather.getWeatherFullText(weatherId)
                .capitalize(Locale.getDefault()),
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
            text = LocalDataFormatter.current.date.getDateHour(datetime = hourForecast.datetime)
        )
    }
}
