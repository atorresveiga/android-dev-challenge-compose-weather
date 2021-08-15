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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Place
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.model.Location
import com.atorresveiga.bluecloud.ui.common.location1
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme

/**
 * LastSelectedLocation composable to show last selected locations
 * @param location locations to display
 * @param onClickLocation the callback that is triggered when this location is clicked. This [Location]
 * comes as a parameter of the callback.
 * @param modifier Modifier
 */
@Composable
fun LocationListItem(
    location: Location,
    modifier: Modifier = Modifier,
    onClickLocation: (location: Location) -> Unit = {}
) {
    Row(
        modifier = modifier
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

@Preview
@Composable
fun LocationListItemPreview() {
    BlueCloudTheme {
        Surface {
            LocationListItem(location = location1)
        }
    }
}
