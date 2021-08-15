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

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Stable
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onSubscription

class OnStart : Interaction

/**
 * MutableNavigationInteractionSource a MutableInteractionSource that emits a [OnStart] Interaction
 * when is Subscribed. Is used to track user interaction and change the HourNavigationInteraction state.
 */
@Stable
class MutableNavigationInteractionSource : MutableInteractionSource {

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
