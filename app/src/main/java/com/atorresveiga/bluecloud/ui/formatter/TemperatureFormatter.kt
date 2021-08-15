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
import com.atorresveiga.bluecloud.R
import kotlin.math.roundToInt

enum class TemperatureSystem(@StringRes val stringRes: Int) {
    Celsius(R.string.celsius),
    Fahrenheit(R.string.fahrenheit)
}

interface TemperatureFormatter {
    fun getValue(celsius: Float): String
}

class CelsiusTemperatureFormatter : TemperatureFormatter {
    override fun getValue(celsius: Float): String {
        val result = celsius.roundToInt()
        return "$result°C"
    }
}

class FahrenheitTemperatureFormatter : TemperatureFormatter {
    override fun getValue(celsius: Float): String {
        val fahrenheit = (celsius * 9f / 5f) + 32
        val result = fahrenheit.roundToInt()
        return "$result°F"
    }
}
