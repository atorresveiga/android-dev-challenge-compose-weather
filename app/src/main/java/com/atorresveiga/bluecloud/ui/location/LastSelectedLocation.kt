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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.R
import com.atorresveiga.bluecloud.model.Location
import com.atorresveiga.bluecloud.ui.common.locations
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme

/**
 * LastSelectedLocation composable to show last selected locations
 * @param lastSelectedLocations last selected locations list
 * @param onSelectLocation the callback that is triggered when a location is selected. An updated [Location]
 * comes as a parameter of the callback.
 * @param modifier Modifier
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LastSelectedLocation(
    lastSelectedLocations: List<Location>,
    modifier: Modifier = Modifier,
    onSelectLocation: (location: Location) -> Unit = {}
) {
    Column(modifier = modifier) {
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
                    LocationListItem(
                        location = location,
                        onClickLocation = onSelectLocation,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun LastSelectedLocationPreview() {
    BlueCloudTheme {
        Surface {
            LastSelectedLocation(lastSelectedLocations = locations, modifier = Modifier.padding(8.dp))
        }
    }
}
