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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.R
import com.atorresveiga.bluecloud.data.Settings
import com.atorresveiga.bluecloud.ui.formatter.HourSystem
import com.atorresveiga.bluecloud.ui.formatter.TemperatureSystem
import com.atorresveiga.bluecloud.ui.formatter.WindMeasurementSystem
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme
import com.atorresveiga.bluecloud.ui.theme.cardsBackground

/**
 * MeasurementSettings measurement settings section
 * @param settings app's settings
 * @param onUpdateSettings the callback that is triggered when the settings are updated. The updated [Settings]
 * comes as a parameter of the callback.
 * @param modifier Modifier
 * @param settingModifier Modifier to apply on settings items
 */
@Composable
fun MeasurementSettings(
    settings: Settings,
    onUpdateSettings: (settings: Settings) -> Unit,
    modifier: Modifier = Modifier,
    settingModifier: Modifier = Modifier
) {
    val timeSystems = HourSystem.values().map { stringResource(id = it.stringRes) }
    val temperatureSystems = TemperatureSystem.values().map { stringResource(id = it.stringRes) }
    val windMeasurementSystems =
        WindMeasurementSystem.values().map { stringResource(id = it.stringRes) }

    Card(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.cardsBackground
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SettingItem(
                name = stringResource(R.string.measurements_settings),
                modifier = settingModifier
            ) {
                Divider()
            }
            SettingSwitch(
                name = stringResource(R.string.temperature_system),
                selected = settings.temperatureSystem,
                items = temperatureSystems,
                onSelectionChange = { value ->
                    onUpdateSettings(
                        settings.copy(temperatureSystem = value)
                    )
                },
                modifier = settingModifier
            )
            SettingSwitch(
                name = stringResource(R.string.time_format),
                selected = settings.hourSystem,
                items = timeSystems,
                onSelectionChange = { value ->
                    onUpdateSettings(
                        settings.copy(hourSystem = value)
                    )
                },
                modifier = settingModifier
            )
            SettingSwitch(
                name = stringResource(R.string.wind_speed_system),
                selected = settings.windSpeedSystem,
                items = windMeasurementSystems,
                onSelectionChange = { value ->
                    onUpdateSettings(
                        settings.copy(windSpeedSystem = value)
                    )
                },
                modifier = settingModifier
            )
        }
    }
}

@Preview
@Composable
fun MeasurementSettingsPreview() {
    BlueCloudTheme {
        Surface {
            MeasurementSettings(
                settings = Settings(),
                onUpdateSettings = {}
            )
        }
    }
}
