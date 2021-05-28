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
package com.example.androiddevchallenge.ui.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.model.Location
import com.example.androiddevchallenge.ui.Information
import com.example.androiddevchallenge.ui.Result
import com.google.accompanist.insets.systemBarsPadding

@Composable
fun LocationScreen(
    viewModel: LocationViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        LocationState.SelectLocation -> {

            val error = viewModel.error.collectAsState()
            val scaffoldState = rememberScaffoldState()
            val lastSelectedLocations by viewModel.lastSelectedLocations.collectAsState()
            val foundLocations by viewModel.foundLocations.collectAsState()
            val searchQuery by viewModel.query.collectAsState()
            val findUserLocationWithPermission =
                wrapFindLocationWithPermission(viewModel::findUserLocation)

            error.value?.getContentIfNotHandled()?.let { resId ->
                val errorString = stringResource(id = resId)
                LaunchedEffect(resId) {
                    scaffoldState.snackbarHostState.showSnackbar(errorString)
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Scaffold(
                    scaffoldState = scaffoldState,
                    backgroundColor = MaterialTheme.colors.surface,
                    snackbarHost = { hostState ->
                        SnackbarHost(hostState = hostState) { data ->
                            Snackbar(
                                snackbarData = data,
                                backgroundColor = MaterialTheme.colors.error,
                                contentColor = MaterialTheme.colors.onError
                            )
                        }
                    },
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .systemBarsPadding()
                ) {
                    SelectLocation(
                        lastSelectedLocations = lastSelectedLocations,
                        foundLocations = foundLocations,
                        searchQuery = searchQuery,
                        updateSearchQuery = viewModel::updateSearchQuery,
                        findUserLocation = findUserLocationWithPermission,
                        onSelectLocation = viewModel::selectLocation,
                    )
                }
            }
        }
        LocationState.FindingUserLocation, LocationState.FindingLocationTimeZone -> {

            val resId =
                if (uiState == LocationState.FindingUserLocation)
                    R.string.finding_location
                else
                    R.string.finding_timezone

            Information {
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    text = stringResource(id = resId),
                    style = MaterialTheme.typography.h4,
                    textAlign = TextAlign.Center
                )
            }
        }

        LocationState.LocationSelected -> navController.popBackStack()
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SelectLocation(
    lastSelectedLocations: List<Location>,
    foundLocations: Result<List<Location>>,
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    updateSearchQuery: (searchQuery: String) -> Unit,
    onSelectLocation: (location: Location) -> Unit,
    findUserLocation: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(top = 48.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(id = R.string.select_location),
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.Center
        )

        SearchLocation(
            searchQuery = searchQuery,
            foundLocations = foundLocations,
            onSelectLocation = onSelectLocation,
            updateSearchQuery = updateSearchQuery
        )

        AnimatedVisibility(
            visible = searchQuery.isEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {

            Column(modifier = Modifier.fillMaxWidth()) {

                BlueCloudButton(
                    onClick = { findUserLocation() },
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .align(Alignment.End)

                ) {
                    Icon(
                        imageVector = Icons.Rounded.Place,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(stringResource(R.string.find_your_location))
                }

                if (lastSelectedLocations.isNotEmpty()) {
                    LastSelectedLocation(
                        lastSelectedLocations = lastSelectedLocations,
                        onSelectLocation = onSelectLocation,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LastSelectedLocation(
    lastSelectedLocations: List<Location>,
    onSelectLocation: (location: Location) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(top = 48.dp)) {
        Text(
            stringResource(R.string.last_selected_locations),
            modifier = Modifier.padding(bottom = 16.dp),
            style = MaterialTheme.typography.subtitle1
        )

        LazyColumn(
            Modifier.background(
                color = MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.BackgroundOpacity),
                shape = MaterialTheme.shapes.small
            )
        ) {
            itemsIndexed(lastSelectedLocations) { index, location ->
                key(location.name) {
                    if (index != 0) {
                        Divider()
                    }
                    LocationListItem(location = location, onClickLocation = onSelectLocation)
                }
            }
        }
    }
}

@Composable
fun BlueCloudButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = { onClick() },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.onPrimary,
            contentColor = MaterialTheme.colors.primary
        ),
        content = content
    )
}

@Composable
fun SearchLocation(
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    foundLocations: Result<List<Location>>,
    onSelectLocation: (location: Location) -> Unit,
    updateSearchQuery: (searchQuery: String) -> Unit
) {
    Column(modifier = modifier) {
        TextField(
            value = searchQuery,
            onValueChange = {
                updateSearchQuery(it)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                )
            },
            trailingIcon = {
                if (foundLocations is Result.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colors.onSurface.copy(alpha = LocalContentAlpha.current)
                    )
                } else if (searchQuery.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                updateSearchQuery("")
                            }
                    )
                }
            },
            singleLine = true,
            modifier = Modifier
                .padding(top = 48.dp)
                .fillMaxWidth()
        )

        if (foundLocations is Result.Success && foundLocations.data.isNotEmpty()) {
            LazyColumn(
                Modifier.background(
                    color = MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.BackgroundOpacity),
                    shape = MaterialTheme.shapes.small.copy(
                        topEnd = ZeroCornerSize,
                        topStart = ZeroCornerSize
                    )
                )
            ) {
                itemsIndexed(foundLocations.data) { index, location ->
                    key(location.name) {
                        if (index != 0) {
                            Divider()
                        }
                        LocationListItem(location = location, onClickLocation = onSelectLocation)
                    }
                }
            }
        }
    }
}

@Composable
fun LocationListItem(location: Location, onClickLocation: (location: Location) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickLocation(location) }
    ) {
        Icon(
            imageVector = Icons.Rounded.Place,
            contentDescription = null,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 10.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                location.name,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.subtitle1
            )
            Text(
                text = "${location.latitude} ${location.longitude}",
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
        }
    }
}
