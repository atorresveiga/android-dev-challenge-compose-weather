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
package com.example.androiddevchallenge.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.ui.BlueCloudTitle
import com.example.androiddevchallenge.ui.ForecastDataSource
import com.example.androiddevchallenge.ui.Settings
import com.example.androiddevchallenge.ui.theme.cardsBackground
import com.example.androiddevchallenge.ui.translatableString
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.systemBarsPadding

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navController: NavController
) {
    val settings = viewModel.settings.collectAsState().value
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        if (settings != null) {
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
                    updateSettings = viewModel::updateSettings,
                    settingModifier = settingModifier
                )

                VisualSettings(
                    settings = settings,
                    updateSettings = viewModel::updateSettings,
                    settingModifier = settingModifier,
                    modifier = Modifier
                        .padding(top = 24.dp)
                )
                GeneralSettings(
                    settings = settings,
                    updateSettings = viewModel::updateSettings,
                    settingModifier = settingModifier,
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .navigationBarsPadding()
                )
            }
        }
    }
}

@Composable
fun MeasurementSettings(
    settings: Settings,
    updateSettings: (settings: Settings) -> Unit,
    modifier: Modifier = Modifier,
    settingModifier: Modifier = Modifier
) {
    val timeSystems = stringArrayResource(id = R.array.hour_system).toList()
    val temperatureSystems = stringArrayResource(id = R.array.temperature_system).toList()
    val windSpeedSystems = stringArrayResource(id = R.array.wind_speed_system).toList()

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
                    updateSettings(
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
                    updateSettings(
                        settings.copy(hourSystem = value)
                    )
                },
                modifier = settingModifier
            )
            SettingSwitch(
                name = stringResource(R.string.wind_speed_system),
                selected = settings.windSpeedSystem,
                items = windSpeedSystems,
                onSelectionChange = { value ->
                    updateSettings(
                        settings.copy(windSpeedSystem = value)
                    )
                },
                modifier = settingModifier
            )
        }
    }
}

@Composable
fun VisualSettings(
    settings: Settings,
    updateSettings: (settings: Settings) -> Unit,
    modifier: Modifier = Modifier,
    settingModifier: Modifier = Modifier
) {

    val defaultDisplayViews = stringArrayResource(id = R.array.display_forecast).toList()
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
                    updateSettings(
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
                    updateSettings(settings.copy(clouds = value.toInt()))
                },
                valueRange = 20f..35f,
                modifier = settingModifier
            )
            SettingNumber(
                name = stringResource(R.string.storm_cloud_number),
                value = stormClouds.value,
                onValueChange = { value ->
                    stormClouds.value = value
                    updateSettings(settings.copy(stormClouds = value.toInt()))
                },
                valueRange = 5f..15f,
                modifier = settingModifier
            )
            SettingNumber(
                name = stringResource(R.string.precipitation_number),
                value = hourlyPrecipitation.value,
                onValueChange = { value ->
                    hourlyPrecipitation.value = value
                    updateSettings(settings.copy(hourlyPrecipitation = value.toInt()))
                },
                valueRange = 50f..250f,
                modifier = settingModifier
            )
            SettingNumber(
                name = stringResource(R.string.list_item_precipitation_number),
                value = dailyPrecipitation.value,
                onValueChange = { value ->
                    dailyPrecipitation.value = value
                    updateSettings(settings.copy(dailyPrecipitation = value.toInt()))
                },
                valueRange = 25f..80f,
                modifier = settingModifier
            )
        }
    }
}

@Composable
fun GeneralSettings(
    settings: Settings,
    updateSettings: (settings: Settings) -> Unit,
    modifier: Modifier = Modifier,
    settingModifier: Modifier = Modifier
) {
    val dataSources = ForecastDataSource.values().map { it.translatableString() }

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
                selected = settings.dataSource.ordinal,
                items = dataSources,
                onSelectionChange = { value ->
                    updateSettings(
                        settings.copy(dataSource = ForecastDataSource.values()[value])
                    )
                },
                modifier = settingModifier
            )
        }
    }
}

@Composable
fun SettingItem(
    name: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.padding(horizontal = 8.dp, vertical = 16.dp)) {
        SettingText(text = name, modifier = Modifier.padding(bottom = 8.dp))
        content()
    }
}

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

@Composable
fun SettingText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = Color.Unspecified
) {
    val style =
        if (booleanResource(id = R.bool.is_large_display)) MaterialTheme.typography.h6 else MaterialTheme.typography.body1
    Text(
        text = text,
        style = style,
        modifier = modifier,
        textAlign = textAlign,
        color = color
    )
}

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
fun SettingNumberPreview() {
    val (value, onChange) = remember { mutableStateOf(5f) }
    SettingNumber(
        name = "Preview",
        value = value,
        onValueChange = onChange,
        valueRange = 5f..150f
    )
}

@Preview(widthDp = 350, heightDp = 100)
@Composable
fun SettingSwitchPreview() {
    val (value, onChange) = remember { mutableStateOf(0) }
    val items = listOf("First", "Second", "Third")
    SettingSwitch(
        name = "Switch",
        selected = value,
        items = items,
        onSelectionChange = onChange
    )
}
