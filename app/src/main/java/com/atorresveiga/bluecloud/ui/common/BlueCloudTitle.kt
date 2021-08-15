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
package com.atorresveiga.bluecloud.ui.common

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

/**
 * BlueCloudTitle [Text] for titles in Blue Cloud app.
 * @param text text to display
 * @param modifier modifier
 * @param textAlign textAlign
 */
@Composable
fun BlueCloudTitle(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    screenSize: ScreenSize = getScreenSize()
) {
    LocalConfiguration.current.smallestScreenWidthDp
    val style = if (screenSize == ScreenSize.Large) {
        MaterialTheme.typography.h4
    } else {
        MaterialTheme.typography.h5
    }
    Text(
        modifier = modifier,
        text = text,
        style = style,
        textAlign = textAlign
    )
}

@Preview
@Composable
fun BlueCloudTitlePreview() {
    BlueCloudTitle("Blue Cloud Title", screenSize = ScreenSize.Small)
}

@Preview
@Composable
fun BlueCloudTitleLargePreview() {
    BlueCloudTitle("Blue Cloud Title", screenSize = ScreenSize.Large)
}
