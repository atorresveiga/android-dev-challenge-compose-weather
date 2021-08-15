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
package com.atorresveiga.bluecloud.ui.forecast

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.North
import androidx.compose.material.icons.rounded.South
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.ui.common.settings

/**
 * MaxMinTemperature day's range temperatures
 * @param min day's minimum temperature
 * @param max day's maximum temperature
 * @param modifier Modifier
 * @param style TextStyle
 */
@Composable
fun MaxMinTemperature(
    min: Float,
    max: Float,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.h5
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.South,
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(end = 16.dp),
            text = LocalSettings.current.dataFormatter.temperature.getValue(min),
            style = style
        )
        Icon(
            imageVector = Icons.Rounded.North,
            contentDescription = null
        )
        Text(
            text = LocalSettings.current.dataFormatter.temperature.getValue(max),
            style = style
        )
    }
}

@Preview(widthDp = 180, heightDp = 50)
@Composable
fun MaxMinTemperaturePreview() {
    CompositionLocalProvider(LocalSettings provides settings) {
        MaxMinTemperature(min = -20f, max = 50f)
    }
}
