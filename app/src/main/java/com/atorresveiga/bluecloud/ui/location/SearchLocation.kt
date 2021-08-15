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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.model.Location
import com.atorresveiga.bluecloud.ui.common.locations
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme

/**
 * SearchLocation composable to search for a location
 * @param foundLocations locations matching the query
 * @param modifier Modifier
 * @param isSearching whether a location search is taking place
 * @param searchQuery search query
 * @param onSelectLocation the callback that is triggered when a location is selected. The selected [Location]
 * comes as a parameter of the callback.
 * @param updateSearchQuery the callback that is triggered when a new search query is typed. The new search query
 * comes as a parameter of the callback.
 */
@Composable
fun SearchLocation(
    foundLocations: List<Location>,
    modifier: Modifier = Modifier,
    isSearching: Boolean = false,
    searchQuery: String = "",
    onSelectLocation: (location: Location) -> Unit = {},
    updateSearchQuery: (searchQuery: String) -> Unit = {}
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
                if (isSearching) {
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
            modifier = Modifier.fillMaxWidth()
        )

        if (foundLocations.isNotEmpty()) {
            LazyColumn(
                Modifier.background(
                    color = MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.BackgroundOpacity),
                    shape = MaterialTheme.shapes.small.copy(
                        topEnd = ZeroCornerSize,
                        topStart = ZeroCornerSize
                    )
                )
            ) {
                itemsIndexed(foundLocations) { index, location ->
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

@Preview
@Composable
fun SearchLocationPreview() {
    BlueCloudTheme {
        Surface {
            SearchLocation(foundLocations = locations, searchQuery = "loc")
        }
    }
}
