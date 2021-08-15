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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Place
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.ui.formatter.getLocationShortValue
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme

/**
 * SelectLocationButton composable used to change the location of where we want to know the weather forecast
 * @param currentLocationName the location of the current forecast
 * @param modifier Modifier
 * @param onSelectLocation command to select a new location
 * @param style TextStyle
 */
@Composable
fun SelectLocationButton(
    currentLocationName: String,
    modifier: Modifier = Modifier,
    onSelectLocation: () -> Unit = {},
    style: TextStyle = MaterialTheme.typography.h5
) {
    Row(
        modifier = modifier
            .clickable { onSelectLocation() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Place,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = currentLocationName.getLocationShortValue(),
            style = style
        )
    }
}

@Preview(widthDp = 300, heightDp = 50)
@Composable
fun SelectLocationButtonPreview() {
    BlueCloudTheme {
        Surface {
            SelectLocationButton(currentLocationName = "Location Name")
        }
    }
}
