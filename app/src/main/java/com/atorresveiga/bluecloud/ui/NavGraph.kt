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
package com.atorresveiga.bluecloud.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.atorresveiga.bluecloud.ui.forecast.ForecastScreen
import com.atorresveiga.bluecloud.ui.forecast.ForecastViewModel
import com.atorresveiga.bluecloud.ui.location.LocationScreen
import com.atorresveiga.bluecloud.ui.location.LocationViewModel
import com.atorresveiga.bluecloud.ui.settings.SettingsScreen
import com.atorresveiga.bluecloud.ui.settings.SettingsViewModel

/**
 * Destinations used in the BlueCloudApp.
 */
object BlueCloudDestinations {
    const val LocationRoute = "location"
    const val ForecastRoute = "forecast"
    const val SettingsRoute = "settings"
}

@Composable
fun NavGraph(startDestination: String = BlueCloudDestinations.ForecastRoute) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(BlueCloudDestinations.LocationRoute) {
            val locationViewModel = hiltViewModel<LocationViewModel>()
            LocationScreen(
                viewModel = locationViewModel,
                navController = navController
            )
        }
        composable(BlueCloudDestinations.ForecastRoute) {
            val forecastViewModel = hiltViewModel<ForecastViewModel>()
            ForecastScreen(
                viewModel = forecastViewModel,
                navController = navController
            )
        }
        composable(BlueCloudDestinations.SettingsRoute) {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            SettingsScreen(
                viewModel = settingsViewModel,
                navController = navController
            )
        }
    }
}
