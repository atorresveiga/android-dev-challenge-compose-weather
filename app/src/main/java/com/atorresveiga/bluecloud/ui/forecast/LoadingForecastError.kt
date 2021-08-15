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
package com.atorresveiga.bluecloud.ui.forecast

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.R
import com.atorresveiga.bluecloud.ui.common.BlueCloudButton
import com.atorresveiga.bluecloud.ui.common.BlueCloudTitle
import com.atorresveiga.bluecloud.ui.common.Information
import com.atorresveiga.bluecloud.ui.theme.BlueCloudTheme

/**
 * LoadingForecastError error window to show when something goes wrong when loading the forecast data
 * @param retry following action to execute when loading the forecast data fails
 */
@Composable
fun LoadingForecastError(retry: () -> Unit) {
    Information {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BlueCloudTitle(
                modifier = Modifier
                    .padding(top = 16.dp),
                text = stringResource(id = R.string.loading_forecast_error),
                textAlign = TextAlign.Center
            )
            BlueCloudButton(
                onClick = { retry() },
                modifier = Modifier
                    .padding(top = 24.dp)
            ) {
                Text(text = stringResource(id = R.string.update_forecast))
            }
        }
    }
}

@Preview(showSystemUi = true, device = Devices.PIXEL_4)
@Composable
fun LoadingForecastErrorPreview() {
    BlueCloudTheme {
        Surface {
            LoadingForecastError(retry = {})
        }
    }
}
