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
import com.atorresveiga.bluecloud.R

class ScaleFormatter {
    private fun getResId(id: Int): Int? {
        return when (id) {
            1 -> R.array.cloud_scale
            2 -> R.array.precipitation_scale
            else -> null
        }
    }
    @Composable
    fun getScale(id: Int, pos: Int): String {
        val resId = getResId(id) ?: return ""
        val scales = stringArrayResource(resId)
        return scales.getOrElse(pos) { "" }
    }
}
