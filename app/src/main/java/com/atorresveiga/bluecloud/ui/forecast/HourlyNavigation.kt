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
package com.atorresveiga.bluecloud.ui.forecast

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.data.ACTIVE
import com.atorresveiga.bluecloud.data.BACKWARD
import com.atorresveiga.bluecloud.data.FORWARD
import com.atorresveiga.bluecloud.data.IDLE
import com.atorresveiga.bluecloud.data.INACTIVE
import com.atorresveiga.bluecloud.model.HourForecast
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onSubscription
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HourNavigation(
    hourlyForecast: List<HourForecast>,
    selected: Int,
    onSelectedChange: (index: Int) -> Unit,
    onDirectionChange: (direction: Int) -> Unit,
    onHourNavigationInteractionChange: (state: Int) -> Unit
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthPx = with(density) { screenWidth.toPx() }

    // Get the selected hour offset
    val selectedOffset = -1 * selected * screenWidthPx / (hourlyForecast.size - 1)

    // Initialize offset in the selected hour offset
    var offset by remember { mutableStateOf(selectedOffset) }

    val interactionSource = remember { MutableNavigationInteractionSource() }

    val horizontalScrollableState = rememberScrollableState { delta ->
        offset = (delta / 8 + offset).coerceIn(-1 * screenWidthPx, 0f)

        val index = (-1 * (hourlyForecast.size - 1) * offset / screenWidthPx).roundToInt()

        val direction = if (delta < 0) FORWARD else BACKWARD

        onSelectedChange(index)
        onDirectionChange(direction)
        delta / 8
    }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collectLatest { interaction ->
            Log.d("Interaction", interaction.toString())
            when (interaction) {
                is PressInteraction.Press -> {
                    onHourNavigationInteractionChange(ACTIVE)
                }
                else -> {
                    onHourNavigationInteractionChange(ACTIVE)
                    delay(2000)
                    onHourNavigationInteractionChange(INACTIVE)
                    delay(4000)
                    onHourNavigationInteractionChange(IDLE)
                }
            }
        }
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

class OnStart : Interaction

@Stable
private class MutableNavigationInteractionSource : MutableInteractionSource {

    private val mutableInteractions = MutableSharedFlow<Interaction>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override val interactions = mutableInteractions.onSubscription { emit(OnStart()) }

    override suspend fun emit(interaction: Interaction) {
        mutableInteractions.emit(interaction)
    }

    override fun tryEmit(interaction: Interaction): Boolean {
        return mutableInteractions.tryEmit(interaction)
    }
}
