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
package com.atorresveiga.bluecloud.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme

/**
 * NavigationBackButton composable to navigate to the previous screen
 * @param onClick command to execute when the button is clicked
 * @param modifier Modifier
 */
@Composable
fun NavigationBackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Rounded.ArrowBack,
        contentDescription = null,
        modifier = modifier
            .clickable { onClick() }
            .padding(8.dp)
    )
}

@Preview
@Composable
fun NavigationBackButtonPreview() {
    BlueCloudTheme {
        Surface {
            NavigationBackButton(onClick = {})
        }
    }
}
