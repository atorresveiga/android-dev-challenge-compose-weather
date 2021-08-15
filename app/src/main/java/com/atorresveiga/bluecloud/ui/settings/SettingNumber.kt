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
package com.atorresveiga.bluecloud.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme

/**
 * SettingNumber setting whose value is a number
 * @param name setting title
 * @param value setting current value
 * @param onValueChange the callback that is triggered when this setting's value change. The updated value
 * comes as a parameter of the callback.
 * @param modifier Modifier
 * @param valueRange allowed value range for this setting
 */
@Composable
fun SettingNumber(
    name: String,
    value: Float,
    onValueChange: (value: Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f
) {
    SettingItem(
        name = name,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.BackgroundOpacity),
                    shape = MaterialTheme.shapes.small
                )
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value.toInt().toString(),
                modifier = Modifier.width(48.dp),
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center
            )
            Slider(
                value = value,
                onValueChange = { onValueChange(it) },
                valueRange = valueRange,
                modifier = Modifier.padding(horizontal = 12.dp),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colors.onPrimary,
                    activeTrackColor = MaterialTheme.colors.onPrimary
                )
            )
        }
    }
}

@Preview(widthDp = 350, heightDp = 100)
@Composable
fun SettingNumberPreview() {
    BlueCloudTheme {
        Surface {
            val (value, onChange) = remember { mutableStateOf(5f) }
            SettingNumber(
                name = "Preview",
                value = value,
                onValueChange = onChange,
                valueRange = 5f..150f
            )
        }
    }
}
