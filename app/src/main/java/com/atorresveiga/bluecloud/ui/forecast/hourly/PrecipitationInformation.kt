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

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.atorresveiga.bluecloud.model.HourForecast
import com.atorresveiga.bluecloud.ui.common.hour1
import com.atorresveiga.bluecloud.ui.common.settings
import com.atorresveiga.bluecloud.ui.forecast.LocalSettings

/**
 * PrecipitationInformation composable used to change the app settings
 * @param hourForecast current hour forecast
 * @param modifier Modifier
 */
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

@Preview
@Composable
fun PrecipitationInformationPreview() {
    CompositionLocalProvider(LocalSettings provides settings) {
        PrecipitationInformation(hourForecast = hour1)
    }
}
