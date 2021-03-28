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
package com.example.androiddevchallenge.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.example.androiddevchallenge.model.Forecast
import com.example.androiddevchallenge.ui.access.NeedsLocationAccessScreen
import com.example.androiddevchallenge.ui.forecast.EmptyForecast
import com.example.androiddevchallenge.ui.forecast.ForecastScreen
import com.example.androiddevchallenge.ui.main.State

@Composable
fun BlueCloudApp(
    state: State,
    forecast: Forecast?,
    onRequestPermission: () -> Unit,
    onRefreshData: () -> Unit
) {
    Surface(color = MaterialTheme.colors.background) {
        when (state) {
            State.NeedLocationAccess -> {
                NeedsLocationAccessScreen(onRequestPermission = onRequestPermission)
            }
            State.FindingLocation -> {
                Text(text = "Yeah!! Finding Location")
            }
            State.Ready -> {
                forecast?.let {
                    ForecastScreen(it)
                } ?: EmptyForecast(onRefreshData)
            }
            else -> {
                Text(text = state.toString())
            }
        }
    }
}
