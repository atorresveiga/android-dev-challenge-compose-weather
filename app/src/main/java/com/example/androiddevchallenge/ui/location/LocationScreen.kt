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
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.model.Location
import com.example.androiddevchallenge.model.MoonPhase
import com.example.androiddevchallenge.ui.LocalDataFormatter
import com.example.androiddevchallenge.ui.forecast.Cloud
import com.example.androiddevchallenge.ui.forecast.Moon
import dev.chrisbanes.accompanist.insets.systemBarsPadding

fun checkLocationAccess(context: Context): Boolean =
    ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

@Composable
fun LocationScreen(
    viewModel: LocationViewModel,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {

        val context = LocalContext.current
        viewModel.updateLocationAccess(checkLocationAccess(context))

        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.run {
                    updateLocationAccess(true)
                    findUserLocation()
                }
            } else {
                // Permission Denied: Do something
                viewModel.updateLocationAccess(false)
            }
        }

        val state by viewModel.uiState.collectAsState()

        when (state) {
            LocationState.SelectLocation -> {
                //MoonBackground()
                SelectLocation(
                    lastSelectedLocations = viewModel.lastSelectedLocations,
                    findUserLocation = viewModel::findUserLocation,
                    onSelectLocation = viewModel::selectLocation
                )
            }
            LocationState.NeedLocationAccess -> {
                Button(onClick = { launcher.launch(Manifest.permission.ACCESS_COARSE_LOCATION) }) {
                    Text("I understand")
                }
            }
            LocationState.FindingLocation -> {
                Text("Finding Location")
            }
            LocationState.LocationSelected -> navController.popBackStack()
        }
    }
}

@Composable
fun MoonBackground() {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val size = min(maxWidth, maxHeight) * .8f
        val infiniteTransition = rememberInfiniteTransition()
        val target = 250.dp / maxWidth * -1

        val x by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = target,
            animationSpec = infiniteRepeatable(
                tween(durationMillis = 40000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        val x2 by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = target,
            animationSpec = infiniteRepeatable(
                tween(durationMillis = 44000, easing = LinearEasing, delayMillis = 2000),
                repeatMode = RepeatMode.Restart,
            )
        )
        val x3 by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = target,
            animationSpec = infiniteRepeatable(
                tween(durationMillis = 50000, easing = LinearEasing, delayMillis = 4000),
                repeatMode = RepeatMode.Restart
            )
        )

        Moon(
            phase = MoonPhase.FullMoon,
            color = Color.White,
            modifier = Modifier
                .size(size)
                .alpha(.2f)
                .align(Alignment.TopCenter)
        )

        Cloud(
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .offset(x = size * x, y = size * .1f)
                .size(250.dp, 160.dp)
        )

        Cloud(
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .offset(x = size * x2, y = size * .4f)
                .size(250.dp, 160.dp)
        )

        Cloud(
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .offset(x = size * x3, y = size * .5f)
                .size(250.dp, 160.dp)
        )
    }
}

@Composable
fun SelectLocation(
    lastSelectedLocations: List<Location>,
    onSelectLocation: (location:Location) -> Unit,
    findUserLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = 100.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
            .widthIn(max = 600.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text(
            text = stringResource(id = R.string.select_location),
            style = MaterialTheme.typography.h3,
            textAlign = TextAlign.Center
        )

        val textState = remember { mutableStateOf("") }

        TextField(
            value = textState.value,
            onValueChange = { textState.value = it },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
            },
            trailingIcon = {
                BlueCloudButton(
                    onClick = { findUserLocation() }
                ) {
                    Text("Map")
                }
            },
            modifier = Modifier
                .padding(top = 48.dp)
                .fillMaxWidth()
        )

        BlueCloudButton(
            onClick = { findUserLocation() },
            modifier = Modifier.padding(top = 48.dp)

        ) {
            Icon(
                imageVector = Icons.Rounded.Place,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Your current location")
        }

        LastSelectedLocation(
            lastSelectedLocations = lastSelectedLocations,
            onSelectLocation = onSelectLocation,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LastSelectedLocation(
    lastSelectedLocations: List<Location>,
    onSelectLocation: (location:Location) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(top = 48.dp)) {
        Text(
            "Last selected locations",
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.subtitle1
        )

        LazyColumn {
            items(lastSelectedLocations) { location ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectLocation(location) }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Place,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 10.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            LocalDataFormatter.current.timezone.getValue(location.timezone),
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.subtitle1
                        )
                        Text(
                            text = "${location.latitude} ${location.longitude}",
                            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                        )
                    }
                }
                Divider()
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
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.primary
        ),
        content = content
    )
}