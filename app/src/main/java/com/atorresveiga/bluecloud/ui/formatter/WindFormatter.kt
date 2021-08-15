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

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.atorresveiga.bluecloud.R

enum class WindMeasurementSystem(@StringRes val stringRes: Int) {
    Meters(R.string.meters),
    Kilometers(R.string.kilometers),
    Miles(R.string.miles)
}

interface WindMeasurement {
    fun getValue(windSpeed: Float): Float

    @Composable
    fun getMeasurement(): String
}

class MetersWindMeasurement : WindMeasurement {
    override fun getValue(windSpeed: Float) = windSpeed

    @Composable
    override fun getMeasurement(): String =
        stringResource(id = WindMeasurementSystem.Meters.stringRes)
}

class KilometersWindMeasurement : WindMeasurement {
    override fun getValue(windSpeed: Float) = windSpeed * 3.6f

    @Composable
    override fun getMeasurement(): String =
        stringResource(id = WindMeasurementSystem.Kilometers.stringRes)
}

class MilesWindMeasurement : WindMeasurement {
    override fun getValue(windSpeed: Float) = windSpeed * 2.237f

    @Composable
    override fun getMeasurement(): String =
        stringResource(id = WindMeasurementSystem.Miles.stringRes)
}

class WindFormatter(windMeasurement: WindMeasurement) : WindMeasurement by windMeasurement {
    @Composable
    fun getScale(windSpeed: Float): String {
        // windSpeed is saved in m/s
        return when (windSpeed) {
            in 0f..0.277778f -> stringResource(R.string.calm_still) // < 1 k/h
            in 0.277778f..1.38889f -> stringResource(R.string.light_winds) // 1..5 k/h
            in 1.38889f..3.05556f -> stringResource(R.string.light_breeze) // 5..11 k/h
            in 3.05556f..7.77778f -> stringResource(R.string.gentle_breeze) // 11..19 & 19..28 k/h
            in 7.77778f..10.5556f -> stringResource(R.string.fresh_breeze) // 28..38 k/h
            in 10.5556f..13.6111f -> stringResource(R.string.strong_breeze) // 38..49 k/h
            in 13.6111f..16.9444f -> stringResource(R.string.moderate_gale) // 49..61 k/h
            in 16.9444f..20.5556f -> stringResource(R.string.fresh_gale) // 61..74 k/h
            in 20.5556f..24.4444f -> stringResource(R.string.strong_gale) // 74..88 k/h
            in 24.4444f..28.3333f -> stringResource(R.string.whole_gale) // 88..102 k/h
            in 28.3333f..32.7778f -> stringResource(R.string.storm) // 102..118 k/h
            else -> stringResource(R.string.hurricane) // > 118 k/h
        }
    }

    @Composable
    fun getDirection(windDegrees: Float): String {
        return when (windDegrees) {
            in 20f..30f -> stringResource(R.string.n_ne)
            in 30f..50f -> stringResource(R.string.ne)
            in 50f..70f -> stringResource(R.string.e_ne)
            in 70f..110f -> stringResource(R.string.e)
            in 110f..120f -> stringResource(R.string.e_se)
            in 120f..140f -> stringResource(R.string.se)
            in 140f..160f -> stringResource(R.string.s_se)
            in 160f..190f -> stringResource(R.string.s)
            in 190f..210f -> stringResource(R.string.s_sw)
            in 210f..230f -> stringResource(R.string.sw)
            in 230f..250f -> stringResource(R.string.w_sw)
            in 250f..280f -> stringResource(R.string.w)
            in 280f..300f -> stringResource(R.string.w_nw)
            in 300f..320f -> stringResource(R.string.nw)
            in 320f..340f -> stringResource(R.string.n_nw)
            else -> stringResource(R.string.n)
        }
    }
}
