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
package com.example.androiddevchallenge.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.example.androiddevchallenge.R
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

class TemperatureFormatter {
    fun getValue(celsius: Float): String {
        return "${celsius.roundToInt()}°"
    }
}

class DateFormatter(var system: HourSystem = HourSystem.Twelve) {

    val timeZone = TimeZone.currentSystemDefault()

    @Composable
    fun getDateHour(datetime: Long): String {
        val localDateTime =
            Instant.fromEpochSeconds(datetime).toLocalDateTime(timeZone)
        val today = Clock.System.now().toLocalDateTime(timeZone)

        val day = when (localDateTime.date) {
            today.date -> stringResource(R.string.today)
            today.date.plus(1, DateTimeUnit.DAY) -> stringResource(R.string.tomorrow)
            else -> localDateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.US).capitalize(
                Locale.getDefault()
            )
        }

        val hour = getReadableHour(localDateTime.hour)

        return "$day $hour"
    }

    @Composable
    private fun getReadableHour(hour: Int): String {
        return when (system) {
            HourSystem.Twelve -> {
                val endString = when (hour) {
                    0 -> stringResource(R.string.midnight)
                    12 -> stringResource(R.string.noon)
                    in 1..11 -> "AM"
                    else -> "PM"
                }
                "${(if (hour % 12 == 0) 12 else hour % 12)}:00 $endString"
            }
            else -> {
                "${hour.toString().padStart(2, '0')} h"
            }
        }
    }

    fun getHour(datetime: Long): Int =
        Instant.fromEpochSeconds(datetime).toLocalDateTime(timeZone).hour

    enum class HourSystem {
        Twelve,
        TwentyFour
    }
}

class WindFormatter {
    @Composable
    fun getValue(windSpeed: Float): String {
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

class PrecipitationFormatter(private val weatherFormatter: WeatherFormatter) {

    fun isPrecipitation(weatherId: Int) = weatherId % 100 in 2..7

    @Composable
    fun getIntensityString(weatherId: Int, pop: Float): String {
        return when {
            isPrecipitation(weatherId) -> {
                weatherFormatter.getWeatherWithScale(weatherId)
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

enum class PrecipitationForm { Rain, Snow, RainAndSnow }

class ScaleFormatter {
    private val scales: HashMap<Int, Array<String>> = hashMapOf()

    private fun getResId(id: Int): Int? {
        return when (id) {
            1 -> R.array.cloud_scale
            2 -> R.array.precipitation_scale
            else -> null
        }
    }

    @Composable
    fun getScale(id: Int, pos: Int): String {
        if (!scales.containsKey(id)) {
            val resId = getResId(id) ?: return ""
            scales[id] = stringArrayResource(resId)
        }
        return scales[id]?.getOrElse(pos) { "" } ?: ""
    }
}

class WeatherFormatter(private val scaleFormatter: ScaleFormatter) {

    private lateinit var weather: Array<String>

    @Composable
    fun getWeatherWithScale(weatherId: Int): String {
        if (!this::weather.isInitialized) {
            weather = stringArrayResource(R.array.weather)
        }
        val weatherPos = weatherId % 100
        val scaleId = (weatherId % 10000 - weatherId % 1000) / 1000
        val scalePos = (weatherId % 1000 - weatherId % 100) / 100
        // Scale Id in weatherId is encode form 1..n, 0 is without scale
        val scale = scaleFormatter.getScale(scaleId, scalePos)
        return if (scale.isEmpty()) weather[weatherPos] else "$scale ${weather[weatherPos]}"
    }

    @Composable
    fun getWeatherFullText(weatherId: Int): String {
        val isShowerHasThunder = (weatherId % 100000 - weatherId % 10000) / 10000
        val builder = StringBuilder()
        builder.append(getWeatherWithScale(weatherId))
        if (isShowerHasThunder and 2 > 0) {
            builder.append(" " + stringResource(R.string.showers) + " ")
        }
        if (isShowerHasThunder and 1 > 0) {
            builder.append(" " + stringResource(R.string.with_thunder))
        }
        return builder.toString()
    }

    fun hasThunders(weatherId: Int): Boolean {
        return (weatherId % 100000 - weatherId % 10000) / 10000 and 1 > 0
    }
}

class LocationFormatter {
    fun getShortValue(name: String): String {
        val sections = name.split(",")
        return sections.first()
    }
}

class UVFormatter {
    private lateinit var uvScale: Array<String>

    @Composable
    fun getValue(uvi: Float): String {
        if (!this::uvScale.isInitialized) {
            uvScale = stringArrayResource(R.array.uv_scale)
        }
        val pos = when (uvi) {
            in 0f..2.99f -> 0
            in 2.99f..4.99f -> 1
            in 4.99f..7.99f -> 2
            else -> 3
        }
        return uvScale[pos]
    }
}

class DataFormatter(
    val temperature: TemperatureFormatter = TemperatureFormatter(),
    val date: DateFormatter = DateFormatter(),
    val wind: WindFormatter = WindFormatter(),
    val location: LocationFormatter = LocationFormatter(),
    val uvi: UVFormatter = UVFormatter()
) {
    val precipitation: PrecipitationFormatter
    val weather: WeatherFormatter

    init {
        val scaleFormatter = ScaleFormatter()
        weather = WeatherFormatter(scaleFormatter)
        precipitation = PrecipitationFormatter(weather)
    }
}

val LocalDataFormatter = compositionLocalOf<DataFormatter> { error("No data formatter found!") }
