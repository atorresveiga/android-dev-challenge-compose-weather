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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.R
import com.atorresveiga.bluecloud.data.Settings
import com.atorresveiga.bluecloud.ui.forecast.ForecastView
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme
import com.atorresveiga.bluecloud.ui.theme.cardsBackground

/**
 * VisualSettings visual settings section
 * @param settings app's settings
 * @param onUpdateSettings the callback that is triggered when the settings are updated. The updated [Settings]
 * comes as a parameter of the callback.
 * @param modifier Modifier
 * @param settingModifier Modifier to apply on settings items
 */
@Composable
fun VisualSettings(
    settings: Settings,
    onUpdateSettings: (settings: Settings) -> Unit,
    modifier: Modifier = Modifier,
    settingModifier: Modifier = Modifier
) {
    val defaultDisplayViews = ForecastView.values().map { stringResource(id = it.stringRes) }
    val clouds = remember { mutableStateOf(settings.clouds.toFloat()) }
    val stormClouds = remember { mutableStateOf(settings.stormClouds.toFloat()) }
    val hourlyPrecipitation =
        remember { mutableStateOf(settings.hourlyPrecipitation.toFloat()) }
    val dailyPrecipitation =
        remember { mutableStateOf(settings.dailyPrecipitation.toFloat()) }

    Card(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.cardsBackground
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SettingItem(
                name = stringResource(R.string.visual_settings),
                modifier = settingModifier
            ) {
                Divider()
            }
            SettingSwitch(
                name = stringResource(R.string.default_view),
                selected = settings.defaultDisplayView,
                items = defaultDisplayViews,
                onSelectionChange = { value ->
                    onUpdateSettings(
                        settings.copy(defaultDisplayView = value)
                    )
                },
                modifier = settingModifier
            )
            SettingNumber(
                name = stringResource(R.string.cloud_number),
                value = clouds.value,
                onValueChange = { value ->
                    clouds.value = value
                    onUpdateSettings(settings.copy(clouds = value.toInt()))
                },
                valueRange = 20f..35f,
                modifier = settingModifier
            )
            SettingNumber(
                name = stringResource(R.string.storm_cloud_number),
                value = stormClouds.value,
                onValueChange = { value ->
                    stormClouds.value = value
                    onUpdateSettings(settings.copy(stormClouds = value.toInt()))
                },
                valueRange = 5f..15f,
                modifier = settingModifier
            )
            SettingNumber(
                name = stringResource(R.string.precipitation_number),
                value = hourlyPrecipitation.value,
                onValueChange = { value ->
                    hourlyPrecipitation.value = value
                    onUpdateSettings(settings.copy(hourlyPrecipitation = value.toInt()))
                },
                valueRange = 50f..250f,
                modifier = settingModifier
            )
            SettingNumber(
                name = stringResource(R.string.list_item_precipitation_number),
                value = dailyPrecipitation.value,
                onValueChange = { value ->
                    dailyPrecipitation.value = value
                    onUpdateSettings(settings.copy(dailyPrecipitation = value.toInt()))
                },
                valueRange = 25f..80f,
                modifier = settingModifier
            )
        }
    }
}

@Preview
@Composable
fun VisualSettingsPreview() {
    BlueCloudTheme {
        Surface {
            VisualSettings(
                settings = Settings(),
                onUpdateSettings = {}
            )
        }
    }
}
