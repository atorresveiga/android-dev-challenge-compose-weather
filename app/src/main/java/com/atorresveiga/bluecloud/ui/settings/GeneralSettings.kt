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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.R
import com.atorresveiga.bluecloud.data.ForecastDataSource
import com.atorresveiga.bluecloud.data.Settings
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme
import com.atorresveiga.bluecloud.ui.theme.cardsBackground

/**
 * GeneralSettings general settings section
 * @param settings app's settings
 * @param onUpdateSettings the callback that is triggered when the settings are updated. The updated [Settings]
 * comes as a parameter of the callback.
 * @param modifier Modifier
 * @param settingModifier Modifier to apply on settings items
 */
@Composable
fun GeneralSettings(
    settings: Settings,
    onUpdateSettings: (settings: Settings) -> Unit,
    modifier: Modifier = Modifier,
    settingModifier: Modifier = Modifier
) {
    val dataSources = ForecastDataSource.values().map {
        when (it) {
            ForecastDataSource.MetNo -> stringResource(id = R.string.met_no)
            ForecastDataSource.OpenWeather -> stringResource(id = R.string.open_weather)
        }
    }

    val description = when (ForecastDataSource.values()[settings.dataSource]) {
        ForecastDataSource.MetNo -> stringResource(id = R.string.met_no_description)
        ForecastDataSource.OpenWeather -> stringResource(id = R.string.open_weather_description)
    }

    Card(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.cardsBackground
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SettingItem(
                name = stringResource(R.string.general_settings),
                modifier = settingModifier
            ) {
                Divider()
            }
            SettingSwitch(
                name = stringResource(R.string.data_sources),
                selected = settings.dataSource,
                items = dataSources,
                onSelectionChange = { value ->
                    onUpdateSettings(
                        settings.copy(dataSource = value)
                    )
                },
                modifier = settingModifier
            )

            Text(text = description, modifier = settingModifier.padding(16.dp))
        }
    }
}

@Preview
@Composable
fun GeneralSettingsPreview() {
    BlueCloudTheme {
        Surface {
            GeneralSettings(
                settings = Settings(),
                onUpdateSettings = {}
            )
        }
    }
}
