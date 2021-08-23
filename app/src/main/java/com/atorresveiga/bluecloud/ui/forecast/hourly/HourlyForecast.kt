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
package com.atorresveiga.bluecloud.ui.forecast.hourly

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.R
import com.atorresveiga.bluecloud.model.Forecast
import com.atorresveiga.bluecloud.ui.common.ScreenSize
import com.atorresveiga.bluecloud.ui.common.forecast
import com.atorresveiga.bluecloud.ui.common.getScreenSize
import com.atorresveiga.bluecloud.ui.common.settings
import com.atorresveiga.bluecloud.ui.forecast.ForecastView
import com.atorresveiga.bluecloud.ui.forecast.LocalSettings
import com.atorresveiga.bluecloud.ui.forecast.SelectForecastView
import com.atorresveiga.bluecloud.ui.settings.UpdateSettingsButton
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

const val InactiveDelay = 5000L
const val IdleDelay = 5000L

enum class UserInteraction { Active, Inactive, Idle }

/**
 * HourlyForecastScreen hourly view of the selected forecast
 * @param forecast forecast to to display
 * @param modifier Modifier
 * @param onSelectLocation command to select a new location
 * @param onUpdateSettings command to change app settings
 * @param onForecastViewChange the callback that is triggered when the forecast display view change.
 * An updated [ForecastView] comes as a parameter of the callback
 * @param screenSize user device's [ScreenSize]
 */
@Composable
fun HourlyForecastScreen(
    forecast: Forecast,
    modifier: Modifier = Modifier,
    onSelectLocation: () -> Unit = {},
    onUpdateSettings: () -> Unit = {},
    onForecastViewChange: (view: ForecastView) -> Unit = {},
    screenSize: ScreenSize = getScreenSize()
) {
    val indexForecast by remember { mutableStateOf(IndexForecast(forecast)) }

    Box(modifier = modifier.fillMaxSize()) {

        val (index, onIndexChange) = remember { mutableStateOf(0) }
        val (direction, onDirectionChange) = remember { mutableStateOf(NavigationDirection.Forward) }
        val (hourNavigationInteractionState, onHourNavigationInteractionChange) = remember {
            mutableStateOf(UserInteraction.Active)
        }
        val interactionSource = remember { MutableNavigationInteractionSource() }
        val selectedHour = indexForecast.hourly[index]
        val currentDay = indexForecast.getDayForecast(selectedHour.datetime)
        val hasNextHour = index != indexForecast.hourly.lastIndex
        val hasPreviousHour = index != 0
        val onMoveNextHour: () -> Unit = {
            if (hasNextHour) {
                onIndexChange(index + 1)
            }
        }
        val onMovePreviousHour: () -> Unit = {
            if (hasPreviousHour) {
                onIndexChange(index - 1)
            }
        }

        val weatherInfoModifier = if (screenSize == ScreenSize.Large) {
            Modifier.align(Alignment.Center)
        } else {
            Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
        }

        val controlsAlpha by animateFloatAsState(
            targetValue = when (hourNavigationInteractionState) {
                UserInteraction.Inactive -> .4f
                UserInteraction.Idle -> .1f
                else -> .6f
            }
        )

        val skyAlpha by animateFloatAsState(
            targetValue = when (hourNavigationInteractionState) {
                UserInteraction.Active -> .4f
                else -> 1f
            }
        )

        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        onHourNavigationInteractionChange(UserInteraction.Active)
                    }
                    else -> {
                        onHourNavigationInteractionChange(UserInteraction.Active)
                        delay(InactiveDelay)
                        onHourNavigationInteractionChange(UserInteraction.Inactive)
                        delay(IdleDelay)
                        onHourNavigationInteractionChange(UserInteraction.Idle)
                    }
                }
            }
        }

        HourNavigation(
            hourlyForecast = indexForecast.hourly,
            selected = index,
            onSelectedChange = onIndexChange,
            onDirectionChange = onDirectionChange,
            interactionSource = interactionSource
        )

        Sky(
            currentDayForecast = currentDay,
            currentHourForecast = selectedHour,
            direction = direction,
            timezoneId = indexForecast.location.timezoneId,
            isSouthernHemisphere = indexForecast.location.latitude < 0,
            modifier = Modifier.alpha(skyAlpha)
        )

        SelectForecastView(
            forecastView = ForecastView.HourlyView,
            onForecastViewChange = onForecastViewChange,
            modifier = Modifier
                .statusBarsPadding()
                .padding(
                    top = 4.dp,
                    start = dimensionResource(id = R.dimen.small_horizontal_padding)
                )
                .alpha(controlsAlpha)
        )

        WeatherInformation(
            locationName = indexForecast.location.name,
            datetime = selectedHour.datetime,
            weatherId = selectedHour.weatherId,
            temperature = selectedHour.temperature,
            feelsLike = selectedHour.feelsLike,
            uvi = selectedHour.uvi,
            humidity = selectedHour.humidity,
            minTemperature = currentDay.minTemperature,
            maxTemperature = currentDay.maxTemperature,
            timezoneId = indexForecast.location.timezoneId,
            modifier = weatherInfoModifier.alpha(controlsAlpha),
            onSelectLocation = onSelectLocation,
            hasNextHour = hasNextHour,
            hasPreviousHour = hasPreviousHour,
            onMoveNextHour = onMoveNextHour,
            onMovePreviousHour = onMovePreviousHour,
            interactionSource = interactionSource
        )

        PrecipitationInformation(
            selectedHour,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(
                    start = dimensionResource(id = R.dimen.horizontal_padding),
                    bottom = 16.dp
                )
                .alpha(controlsAlpha)
        )

        WindIndicator(
            selectedHour.windDegrees,
            selectedHour.windSpeed,
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier
                .navigationBarsPadding()
                .align(Alignment.BottomEnd)
                .padding(
                    end = dimensionResource(id = R.dimen.horizontal_padding),
                    bottom = 16.dp
                )
                .alpha(controlsAlpha)
        )

        UpdateSettingsButton(
            onClick = onUpdateSettings,
            modifier = Modifier
                .statusBarsPadding()
                .padding(
                    top = 4.dp,
                    end = dimensionResource(id = R.dimen.small_horizontal_padding)
                )
                .size(48.dp)
                .align(Alignment.TopEnd)
                .alpha(controlsAlpha)
        )
    }
}

@Preview(showSystemUi = true, device = Devices.PIXEL_4)
@Composable
fun HourlyForecastScreenPreview() {
    BlueCloudTheme {
        Surface {
            CompositionLocalProvider(LocalSettings provides settings) {
                HourlyForecastScreen(forecast = forecast, screenSize = ScreenSize.Small)
            }
        }
    }
}
