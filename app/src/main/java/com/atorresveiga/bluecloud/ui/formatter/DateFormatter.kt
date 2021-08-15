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
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale

class DateFormatter(private val hourSystemFormatter: HourSystemFormatter) {
    @Composable
    fun getReadableHour(datetime: Long, timezoneId: String): String {
        val timeZone = TimeZone.of(timezoneId)
        val localDateTime =
            Instant.fromEpochSeconds(datetime).toLocalDateTime(timeZone)
        return hourSystemFormatter.getReadableHour(localDateTime.hour)
    }

    @Composable
    fun getReadableDate(datetime: Long, timezoneId: String): String {
        val timeZone = TimeZone.of(timezoneId)
        val localDateTime =
            Instant.fromEpochSeconds(datetime).toLocalDateTime(timeZone)
        val today = Clock.System.now().toLocalDateTime(timeZone)

        return when (localDateTime.date) {
            today.date -> stringResource(R.string.today)
            today.date.plus(1, DateTimeUnit.DAY) -> stringResource(R.string.tomorrow)
            else ->
                "${
                localDateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                    .replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                } ${localDateTime.date.dayOfMonth}"
        }
    }

    fun getHour(datetime: Long, timezoneId: String): Int {
        val timeZone = TimeZone.of(timezoneId)
        return Instant.fromEpochSeconds(datetime).toLocalDateTime(timeZone).hour
    }
}
