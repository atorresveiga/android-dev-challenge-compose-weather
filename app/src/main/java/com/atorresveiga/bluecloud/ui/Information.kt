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
package com.atorresveiga.bluecloud.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.R
import com.atorresveiga.bluecloud.ui.forecast.Cloud
import com.atorresveiga.bluecloud.ui.forecast.Sun
import com.atorresveiga.bluecloud.ui.theme.cloudColor
import com.atorresveiga.bluecloud.ui.theme.sunColor
import com.google.accompanist.insets.systemBarsPadding

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
            val isLandScape = maxWidth > maxHeight
            val padding = if (isLandScape) 48.dp else 0.dp
            Sun(
                color = MaterialTheme.colors.sunColor,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            )
            val width = dimensionResource(id = R.dimen.cloud3)
            val xOffsetModifier = if (maxWidth > 580.dp) .2f else .12f
            Cloud(
                color = MaterialTheme.colors.cloudColor,
                modifier = Modifier
                    .size(width = width, height = width * .66f)
                    .offset(x = maxWidth * xOffsetModifier, y = maxHeight * .45f)
                    .alpha(.98f)
            )
        }
        content()
    }
}

@Composable
fun BlueCloudTitle(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    val style = if (booleanResource(id = R.bool.is_large_display)) {
        MaterialTheme.typography.h4
    } else {
        MaterialTheme.typography.h5
    }
    Text(
        modifier = modifier,
        text = text,
        style = style,
        textAlign = textAlign
    )
}
