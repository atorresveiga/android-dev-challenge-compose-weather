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

import com.atorresveiga.bluecloud.data.MoonPhase

object MoonPhaseFormatter {
    fun decode(moonPhaseId: Int, isBeforeSunrise: Boolean): MoonPhase {
        return when {
            moonPhaseId < 8 -> MoonPhase.values()[moonPhaseId]
            isBeforeSunrise -> MoonPhase.values()[moonPhaseId / 10]
            else -> MoonPhase.values()[moonPhaseId % 10]
        }
    }

    fun encode(phaseId: Int, previousDayPhase: Int = -1): Int {
        return when {
            phaseId != previousDayPhase && previousDayPhase != -1 -> previousDayPhase * 10 + phaseId
            else -> phaseId
        }
    }
}
