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
import androidx.compose.ui.res.stringResource
import com.atorresveiga.bluecloud.R
import java.util.Locale

enum class PrecipitationForm { Rain, Snow, RainAndSnow }

class PrecipitationFormatter(private val weatherFormatter: WeatherFormatter) {

    fun isPrecipitation(weatherId: Int) = weatherId % 100 in 2..7

    @Composable
    fun getIntensityString(weatherId: Int, pop: Float): String {
        return when {
            isPrecipitation(weatherId) -> {
                weatherFormatter.getWeatherWithScale(weatherId)
                    // Ugly capitalize kotlin recommendation
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            }
            pop > 0.25f -> stringResource(R.string.chance_precipitation)
            else -> stringResource(R.string.no_precipitation)
        }
    }

    fun getIntensity(weatherId: Int): Float {
        val scaleId = (weatherId % 10000 - weatherId % 1000) / 1000
        val scalePos = (weatherId % 1000 - weatherId % 100) / 100
        return when {
            !isPrecipitation(weatherId) -> 0f
            scaleId == 0 -> .5f
            scalePos == 0 -> .1f
            scalePos == 1 -> .3f
            scalePos == 2 -> .8f
            else -> 1f
        }
    }

    fun getForm(weatherId: Int): PrecipitationForm {
        if (!isPrecipitation(weatherId)) throw IllegalArgumentException("$weatherId is not a precipitation id")
        return when (weatherId % 100) {
            6 -> PrecipitationForm.Snow
            7 -> PrecipitationForm.RainAndSnow
            else -> PrecipitationForm.Rain
        }
    }

    fun getVolume(volume: Float) = volume.toString().plus(" mm")
}
