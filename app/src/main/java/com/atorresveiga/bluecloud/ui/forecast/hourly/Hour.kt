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
package com.atorresveiga.bluecloud.ui.forecast.hourly

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.R
import com.atorresveiga.bluecloud.ui.common.settings
import com.atorresveiga.bluecloud.ui.forecast.LocalSettings
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme

/**
 * Hour displays current hour and can navigate to the next or previous hour
 * @param timezoneId forecast location's time zone id
 * @param datetime hour's datetime
 * @param hasNextHour if the user can navigate forward in the hourly forecast list
 * @param hasPreviousHour if the user can navigate backward in the hourly forecast list
 * @param interactionSource  [MutableInteractionSource] that will be used to dispatch events when the
 * user interacts with the composable
 * @param modifier Modifier
 * @param onMoveNextHour command to navigate to the next hour in the hourly forecast list
 * @param onMovePreviousHour command to navigate to the previous hour in the hourly forecast list
 */
@Composable
fun Hour(
    datetime: Long,
    timezoneId: String,
    hasNextHour: Boolean,
    hasPreviousHour: Boolean,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
    onMoveNextHour: () -> Unit = {},
    onMovePreviousHour: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        if (hasPreviousHour) {
            Icon(
                imageVector = Icons.Rounded.ChevronLeft,
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 8.dp, end = 8.dp)
                    .clickable(
                        onClick = { onMovePreviousHour() },
                        indication = null,
                        interactionSource = interactionSource
                    )
                    .size(dimensionResource(id = R.dimen.hour_navigation_icon))
            )
        } else {
            Spacer(
                modifier = Modifier
                    .padding(top = 8.dp, start = 8.dp)
                    .size(dimensionResource(id = R.dimen.hour_navigation_icon))
            )
        }

        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text = LocalSettings.current.dataFormatter.date.getReadableHour(
                datetime = datetime,
                timezoneId = timezoneId
            ),
            style = MaterialTheme.typography.h2
        )

        if (hasNextHour) {
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 8.dp, start = 8.dp)
                    .clickable(
                        onClick = { onMoveNextHour() },
                        indication = null,
                        interactionSource = interactionSource
                    )
                    .size(dimensionResource(id = R.dimen.hour_navigation_icon))
            )
        } else {
            Spacer(
                modifier = Modifier
                    .padding(top = 8.dp, start = 8.dp)
                    .size(dimensionResource(id = R.dimen.hour_navigation_icon))
            )
        }
    }
}

@Preview
@Composable
fun HourPreview() {
    BlueCloudTheme {
        Surface {
            CompositionLocalProvider(LocalSettings provides settings) {
                Hour(
                    timezoneId = "America/Argentina/Buenos_Aires",
                    datetime = 1621609658L,
                    hasPreviousHour = true,
                    hasNextHour = true,
                    interactionSource = remember { MutableNavigationInteractionSource() }
                )
            }
        }
    }
}
