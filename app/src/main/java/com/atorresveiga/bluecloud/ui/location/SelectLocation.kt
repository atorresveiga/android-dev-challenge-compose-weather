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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Place
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.R
import com.atorresveiga.bluecloud.model.Location
import com.atorresveiga.bluecloud.ui.common.BlueCloudButton
import com.atorresveiga.bluecloud.ui.common.BlueCloudTitle
import com.atorresveiga.bluecloud.ui.common.locations
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme

/**
 * SearchLocation composable to search for a location
 * @param lastSelectedLocations last selected locations list
 * @param foundLocations locations matching the query
 * @param modifier Modifier
 * @param isSearching whether a location search is taking place
 * @param searchQuery search query
 * @param onSelectLocation the callback that is triggered when a location is selected. The selected [Location]
 * comes as a parameter of the callback.
 * @param updateSearchQuery the callback that is triggered when a new search query is typed. The new search query
 * comes as a parameter of the callback.
 * @param findUserLocation command to search user's current location
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SelectLocation(
    lastSelectedLocations: List<Location>,
    foundLocations: List<Location>,
    modifier: Modifier = Modifier,
    isSearching: Boolean = false,
    searchQuery: String = "",
    onSelectLocation: (location: Location) -> Unit = {},
    updateSearchQuery: (searchQuery: String) -> Unit = {},
    findUserLocation: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .padding(top = 48.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        BlueCloudTitle(
            text = stringResource(id = R.string.select_location),
            textAlign = TextAlign.Center
        )

        SearchLocation(
            searchQuery = searchQuery,
            isSearching = isSearching,
            foundLocations = foundLocations,
            onSelectLocation = onSelectLocation,
            updateSearchQuery = updateSearchQuery,
            modifier = Modifier.padding(top = 48.dp)
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp)
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun SelectLocationPreview() {
    BlueCloudTheme {
        Surface {
            SelectLocation(
                lastSelectedLocations = locations,
                foundLocations = emptyList(),
                searchQuery = ""
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun SelectLocationSearchingPreview() {
    BlueCloudTheme {
        Surface {
            SelectLocation(
                lastSelectedLocations = locations,
                foundLocations = locations,
                searchQuery = " test"
            )
        }
    }
}
