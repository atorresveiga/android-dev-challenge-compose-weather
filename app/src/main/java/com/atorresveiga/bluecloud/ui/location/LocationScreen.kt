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
package com.atorresveiga.bluecloud.ui.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.atorresveiga.bluecloud.R
import com.atorresveiga.bluecloud.domain.LocationNotFoundException
import com.atorresveiga.bluecloud.model.Location
import com.atorresveiga.bluecloud.ui.common.BlueCloudTitle
import com.atorresveiga.bluecloud.ui.common.BooleanProvider
import com.atorresveiga.bluecloud.ui.common.Information
import com.atorresveiga.bluecloud.ui.common.NavigationBackButton
import com.atorresveiga.bluecloud.ui.common.ScaffoldWithErrorSnackBar
import com.atorresveiga.bluecloud.ui.common.locations
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.systemBarsPadding

/**
 * LocationScreen screen to select the location from which we want to know the forecast
 * @param viewModel location screen's view model
 * @param navController [NavController] to manages app navigation
 */
@Composable
fun LocationScreen(
    viewModel: LocationViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val findUserLocationWithPermission =
        wrapFindLocationWithPermission(viewModel::findUserLocation)
    val onLocationSelected: () -> Unit = { navController.popBackStack() }

    LocationScreen(
        uiState = uiState,
        findUserLocation = findUserLocationWithPermission,
        updateSearchQuery = viewModel::updateSearchQuery,
        onSelectLocation = viewModel::selectLocation,
        onLocationSelected = onLocationSelected,
        onNavigationBack = { navController.popBackStack() }
    )
}

@Composable
fun LocationScreen(
    uiState: LocationViewState,
    findUserLocation: () -> Unit,
    updateSearchQuery: (searchQuery: String) -> Unit,
    onSelectLocation: (location: Location) -> Unit,
    onLocationSelected: () -> Unit,
    onNavigationBack: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    when (uiState) {
        is SelectLocationState -> {
            val error = uiState.error
            val lastSelectedLocations = uiState.lastSelectedLocations
            val foundLocations = uiState.foundLocations
            val searchQuery = uiState.query
            val isSearching = uiState.isSearching

            error?.let { exception ->
                // Show Error
                val errorString =
                    if (exception is LocationNotFoundException)
                        stringResource(id = R.string.location_not_found)
                    else stringResource(id = R.string.default_error)

                LaunchedEffect(exception) {
                    scaffoldState.snackbarHostState.showSnackbar(errorString)
                }
            }
            Box(modifier = Modifier.fillMaxWidth()) {

                ScaffoldWithErrorSnackBar(
                    scaffoldState = scaffoldState,
                    modifier = Modifier
                        .padding(top = 48.dp)
                        .navigationBarsPadding()
                        .fillMaxSize()
                ) {
                    SelectLocation(
                        lastSelectedLocations = lastSelectedLocations,
                        isSearching = isSearching,
                        foundLocations = foundLocations,
                        searchQuery = searchQuery,
                        updateSearchQuery = updateSearchQuery,
                        findUserLocation = findUserLocation,
                        onSelectLocation = onSelectLocation,
                    )
                }
                NavigationBackButton(
                    onClick = onNavigationBack,
                    modifier = Modifier
                        .systemBarsPadding()
                        .padding(top = 4.dp, start = 8.dp)
                        .size(48.dp)

                )
            }
        }
        FindingUserLocationState, FindingLocationTimeZoneState -> {
            val resId =
                if (uiState == FindingUserLocationState)
                    R.string.finding_location
                else
                    R.string.finding_timezone

            Information {
                BlueCloudTitle(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    text = stringResource(id = resId),
                    textAlign = TextAlign.Center
                )
            }
        }
        LocationSelectedState -> {
            LaunchedEffect(true) {
                onLocationSelected()
            }
        }
    }
}

fun checkLocationAccess(context: Context): Boolean =
    ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

@Composable
fun wrapFindLocationWithPermission(findUserLocation: () -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            findUserLocation()
        }
    }
    return {
        if (checkLocationAccess(context)) {
            findUserLocation()
        } else {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}

@Preview(showSystemUi = true, device = Devices.PIXEL_4)
@Preview(showSystemUi = true, device = Devices.PIXEL_C)
@Composable
fun LocationScreenPreview(@PreviewParameter(BooleanProvider::class) isDarkTheme: Boolean) {
    BlueCloudTheme(darkTheme = isDarkTheme) {
        Surface {
            LocationScreen(
                uiState = SelectLocationState(
                    foundLocations = emptyList(),
                    lastSelectedLocations = locations,
                    query = "",
                    isSearching = false,
                    error = null
                ),
                findUserLocation = {},
                updateSearchQuery = {},
                onSelectLocation = {},
                onLocationSelected = {},
                onNavigationBack = {}
            )
        }
    }
}
