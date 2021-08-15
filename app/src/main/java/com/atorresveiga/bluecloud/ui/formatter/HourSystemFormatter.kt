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
import com.atorresveiga.bluecloud.R

enum class HourSystem(@StringRes val stringRes: Int) {
    Twelve(R.string.hour_system_12),
    TwentyFour(R.string.hour_system_24)
}

interface HourSystemFormatter {
    @Composable
    fun getReadableHour(hour: Int): String
}

class TwelveHourSystemFormatter : HourSystemFormatter {
    @Composable
    override fun getReadableHour(hour: Int): String {
        val endString = when (hour) {
            0 -> "AM" // stringResource(R.string.midnight)
            12 -> "M" // stringResource(R.string.noon)
            in 1..11 -> "AM"
            else -> "PM"
        }
        return "${(if (hour % 12 == 0) 12 else hour % 12)}:00 $endString"
    }
}

class TwentyFourHourSystemFormatter : HourSystemFormatter {
    @Composable
    override fun getReadableHour(hour: Int) = "${hour.toString().padStart(2, '0')} h"
}
