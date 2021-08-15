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
package com.atorresveiga.bluecloud.ui.formatter

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.atorresveiga.bluecloud.R

class WeatherFormatter(private val scaleFormatter: ScaleFormatter) {

    @Composable
    fun getWeatherWithScale(weatherId: Int): String {

        val weather = stringArrayResource(R.array.weather)

        val weatherPos = weatherId % 100
        val scaleId = (weatherId % 10000 - weatherId % 1000) / 1000
        val scalePos = (weatherId % 1000 - weatherId % 100) / 100
        // Scale Id in weatherId is encode form 1..n, 0 is without scale
        val scale = scaleFormatter.getScale(scaleId, scalePos)
        return stringResource(R.string.weather_with_scale, scale, weather[weatherPos]).trimStart()
    }

    @Composable
    fun getWeatherFullText(weatherId: Int): String {
        val isShowerHasThunder = (weatherId % 100000 - weatherId % 10000) / 10000
        val builder = StringBuilder()
        builder.append(getWeatherWithScale(weatherId))
        if (isShowerHasThunder and 2 > 0) {
            builder.append(" " + stringResource(R.string.with_thunder) + " ")
        }
        if (isShowerHasThunder and 1 > 0) {
            builder.append(" " + stringResource(R.string.showers))
        }
        return builder.toString()
    }

    fun hasThunders(weatherId: Int): Boolean {
        return (weatherId % 100000 - weatherId % 10000) / 10000 and 2 > 0
    }
}
