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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme

/**
 * SettingSwitch setting with a list of possible values
 * @param name setting title
 * @param selected setting current value
 * @param items setting allowed values
 * @param onSelectionChange the callback that is triggered when this setting's value change. The updated value position
 * comes as a parameter of the callback.
 * @param modifier Modifier
 */
@Composable
fun SettingSwitch(
    name: String,
    selected: Int,
    items: List<String>,
    onSelectionChange: (value: Int) -> Unit,
    modifier: Modifier = Modifier
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
        ) {
            items.forEachIndexed { index, item ->

                val itemModifier: Modifier
                val textColor: Color
                if (index == selected) {
                    itemModifier = Modifier.background(
                        color = MaterialTheme.colors.onPrimary,
                        shape = MaterialTheme.shapes.small
                    )
                    textColor = MaterialTheme.colors.primary
                } else {
                    itemModifier = Modifier
                    textColor = Color.Unspecified
                }

                SettingText(
                    text = item,
                    modifier = itemModifier
                        .weight(1f)
                        .clickable { onSelectionChange(index) }
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center,
                    color = textColor
                )
            }
        }
    }
}

@Preview(widthDp = 350, heightDp = 100)
@Composable
fun SettingSwitchPreview() {
    BlueCloudTheme {
        Surface {
            val (value, onChange) = remember { mutableStateOf(0) }
            val items = listOf("First", "Second", "Third")
            SettingSwitch(
                name = "Switch",
                selected = value,
                items = items,
                onSelectionChange = onChange
            )
        }
    }
}
