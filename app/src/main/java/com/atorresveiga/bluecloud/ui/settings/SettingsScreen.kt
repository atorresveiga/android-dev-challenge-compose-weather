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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.atorresveiga.bluecloud.R
import com.atorresveiga.bluecloud.data.Settings
import com.atorresveiga.bluecloud.ui.common.BlueCloudTitle
import com.atorresveiga.bluecloud.ui.common.BooleanProvider
import com.atorresveiga.bluecloud.ui.common.settings
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.systemBarsPadding

/**
 * SettingsScreen screen to change app's settings
 * @param viewModel settings screen's view model
 * @param navController [NavController] to manages app navigation
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navController: NavController
) {
    val settings by viewModel.settings.collectAsState()
    SettingsScreen(
        settings = settings,
        onUpdateSettings = viewModel::updateSettings
    )
}

@Composable
fun SettingsScreen(settings: Settings?, onUpdateSettings: (settings: Settings) -> Unit) {

    if (settings == null) return

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        val scrollState = rememberScrollState()

        val alpha = 1f - scrollState.value.toFloat() / (scrollState.maxValue * .1f)

        BlueCloudTitle(
            text = stringResource(id = R.string.settings),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .systemBarsPadding()
                .padding(top = 48.dp, bottom = 16.dp)
                .alpha(alpha = alpha)
        )
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            val settingModifier = Modifier
                .widthIn(max = 600.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally)

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
            MeasurementSettings(
                settings = settings,
                onUpdateSettings = onUpdateSettings,
                settingModifier = settingModifier
            )
            VisualSettings(
                settings = settings,
                onUpdateSettings = onUpdateSettings,
                settingModifier = settingModifier,
                modifier = Modifier
                    .padding(top = 24.dp)
            )
            GeneralSettings(
                settings = settings,
                onUpdateSettings = onUpdateSettings,
                settingModifier = settingModifier,
                modifier = Modifier
                    .padding(top = 24.dp)
                    .navigationBarsPadding()
            )
        }
    }
}

@Preview(showSystemUi = true, device = Devices.PIXEL_4)
@Preview(showSystemUi = true, device = Devices.PIXEL_C)
@Composable
fun SettingsScreenPreview(@PreviewParameter(BooleanProvider::class) isDarkTheme: Boolean) {
    BlueCloudTheme(darkTheme = isDarkTheme) {
        Surface {
            SettingsScreen(settings = settings, onUpdateSettings = {})
        }
    }
}
