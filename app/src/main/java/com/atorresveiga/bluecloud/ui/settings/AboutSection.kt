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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.BuildConfig
import com.atorresveiga.bluecloud.R
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme

/**
 * AboutSection composable with app's version and code link
 * @param modifier Modifier
 */
@Composable
fun AboutSection(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.subtitle1
        )
        Text(
            text = stringResource(
                id = R.string.version,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE
            )
        )

        Text(
            text = stringResource(id = R.string.view_on_github),
            style = MaterialTheme.typography.body1.copy(textDecoration = TextDecoration.Underline),
            modifier = Modifier
                .padding(top = 8.dp)
                .clickable {
                    uriHandler.openUri("https://github.com/atorresveiga/android-dev-challenge-compose-weather")
                }
                .padding(16.dp)
        )
    }
}

@Preview
@Composable
fun AboutSectionPreview() {
    BlueCloudTheme {
        Surface {
            AboutSection()
        }
    }
}
