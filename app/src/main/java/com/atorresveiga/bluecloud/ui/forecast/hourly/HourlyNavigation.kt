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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.model.HourForecast
import kotlin.math.roundToInt

enum class NavigationDirection { Forward, Backward }

/**
 * HourNavigation composable to navigate between the hours of the hourly forecast
 * @param hourlyForecast list of [HourForecast] to display
 * @param selected position of the current selected hour
 * @param interactionSource MutableInteractionSource
 * @param onSelectedChange the callback that is triggered when the selected hour change.An updated
 * position comes as a parameter of the callback
 * @param onDirectionChange the callback that is triggered when the navigation direction change.An updated
 * [NavigationDirection] comes as a parameter of the callback
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HourNavigation(
    hourlyForecast: List<HourForecast>,
    selected: Int,
    interactionSource: MutableInteractionSource,
    onSelectedChange: (index: Int) -> Unit,
    onDirectionChange: (direction: NavigationDirection) -> Unit
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthPx = with(density) { screenWidth.toPx() }

    // Get the selected hour offset
    val selectedOffset = -1 * selected * screenWidthPx / (hourlyForecast.size - 1)

    // Initialize offset in the selected hour offset
    var offset by remember { mutableStateOf(selectedOffset) }

    val horizontalScrollableState = rememberScrollableState { delta ->
        offset = (delta / 8 + offset).coerceIn(-1 * screenWidthPx, 0f)

        val index = (-1 * (hourlyForecast.size - 1) * offset / screenWidthPx).roundToInt()

        val direction = if (delta < 0) NavigationDirection.Forward else NavigationDirection.Backward

        onSelectedChange(index)
        onDirectionChange(direction)
        delta / 8
    }
    Spacer(
        Modifier
            .fillMaxSize()
            .clickable(
                onClick = {},
                indication = null,
                interactionSource = interactionSource
            )
            .scrollable(
                orientation = Orientation.Horizontal,
                state = horizontalScrollableState,
                interactionSource = interactionSource
            )
            .verticalScroll(state = rememberScrollState())
    )
}
