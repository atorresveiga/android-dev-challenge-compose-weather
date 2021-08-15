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

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.atorresveiga.bluecloud.ui.common.ScreenSize
import com.atorresveiga.bluecloud.ui.common.getScreenSize

/**
 * SettingText styled [Text] for settings in Blue Cloud app.
 * @param text text to display
 * @param modifier modifier
 * @param textAlign textAlign
 * @param color text color
 * @param screenSize user device's [ScreenSize]
 */
@Composable
fun SettingText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = Color.Unspecified,
    screenSize: ScreenSize = getScreenSize()
) {
    val style =
        if (screenSize == ScreenSize.Large)
            MaterialTheme.typography.h6
        else
            MaterialTheme.typography.body1
    Text(
        text = text,
        style = style,
        modifier = modifier,
        textAlign = textAlign,
        color = color
    )
}

@Preview
@Composable
fun SettingTextPreview() {
    SettingText(text = "Setting")
}

@Preview
@Composable
fun SettingTextLargePreview() {
    SettingText(text = "Setting", screenSize = ScreenSize.Large)
}
