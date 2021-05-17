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
package com.example.androiddevchallenge.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.forecast.Cloud
import com.example.androiddevchallenge.ui.forecast.Sun
import com.example.androiddevchallenge.ui.theme.cloudColor
import com.example.androiddevchallenge.ui.theme.sunColor
import dev.chrisbanes.accompanist.insets.systemBarsPadding

@Composable
fun Information(modifier: Modifier = Modifier, content: @Composable () -> Unit = {}) {
    Column(
        modifier = modifier
            .systemBarsPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.6f)
        ) {
            Sun(
                color = MaterialTheme.colors.sunColor,
                modifier = Modifier
                    .padding(48.dp)
                    .fillMaxSize()
            )

            Cloud(
                color = MaterialTheme.colors.cloudColor,
                modifier = Modifier
                    .size(width = 250.dp, height = 160.dp)
                    .offset(x = maxWidth * .2f, y = maxHeight * .5f)
                    .alpha(.98f)
            )
        }
        content()
    }
}
