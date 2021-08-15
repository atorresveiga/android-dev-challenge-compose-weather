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

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.atorresveiga.bluecloud.R
import com.atorresveiga.bluecloud.ui.common.BlueCloudButton
import com.atorresveiga.bluecloud.ui.common.ScreenSize
import com.atorresveiga.bluecloud.ui.common.getScreenSize

enum class ForecastView(@StringRes val stringRes: Int) {
    HourlyView(R.string.hourly),
    DailyView(R.string.daily)
}

/**
 * SelectForecastView representation in the sky
 * @param forecastView current forecast display view
 * @param onForecastViewChange the callback that is triggered when the forecast display view change.
 * An updated [ForecastView] comes as a parameter of the callback
 * @param modifier Modifier
 * @param screenSize user device's [ScreenSize]
 */
@Composable
fun SelectForecastView(
    forecastView: ForecastView,
    modifier: Modifier = Modifier,
    onForecastViewChange: (view: ForecastView) -> Unit = {},
    screenSize: ScreenSize = getScreenSize()
) {
    if (screenSize == ScreenSize.Large) {
        LargeSelectForecastView(
            forecastView = forecastView,
            onForecastViewChange = onForecastViewChange,
            modifier = modifier
        )
    } else {
        SmallSelectForecastView(
            forecastView = forecastView,
            onForecastViewChange = onForecastViewChange,
            modifier = modifier
        )
    }
}

@Composable
fun SmallSelectForecastView(
    forecastView: ForecastView,
    onForecastViewChange: (view: ForecastView) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val dismiss = { expanded = false }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) { // Anchor view
            Text(
                text = stringResource(id = forecastView.stringRes),
                style = MaterialTheme.typography.h6
            ) // City name label
            Icon(
                modifier = Modifier.padding(top = 8.dp),
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null
            )
        }
    }

    if (expanded) {
        Dialog(onDismissRequest = dismiss) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    ForecastView.values()
                        .forEach { view ->
                            Text(
                                text = stringResource(id = view.stringRes),
                                style = if (forecastView == view)
                                    MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold)
                                else
                                    MaterialTheme.typography.h5,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .clickable {
                                        expanded = false
                                        onForecastViewChange(view)
                                    }
                                    .padding(8.dp)
                            )
                        }
                }

                BlueCloudButton(
                    onClick = dismiss,
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .size(64.dp)
                        .align(Alignment.BottomCenter)

                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun LargeSelectForecastView(
    forecastView: ForecastView,
    onForecastViewChange: (view: ForecastView) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = ForecastView.HourlyView.stringRes),
            style = MaterialTheme.typography.h5,
            modifier = Modifier
                .clickable { onForecastViewChange(ForecastView.HourlyView) }
                .padding(8.dp)
                .alpha(if (forecastView == ForecastView.HourlyView) 1f else .5f)
        )
        Text(
            text = "/",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.alpha(.5f)
        )
        Text(
            text = stringResource(id = ForecastView.DailyView.stringRes),
            style = MaterialTheme.typography.h5,
            modifier = Modifier
                .clickable { onForecastViewChange(ForecastView.DailyView) }
                .padding(8.dp)
                .alpha(if (forecastView == ForecastView.DailyView) 1f else .5f)
        )
    }
}

@Preview(widthDp = 100, heightDp = 50)
@Composable
fun SelectForecastViewPreview() {
    SelectForecastView(
        forecastView = ForecastView.HourlyView,
        screenSize = ScreenSize.Small
    )
}

@Preview(widthDp = 200, heightDp = 50)
@Composable
fun SelectForecastViewLargePreview() {
    SelectForecastView(
        forecastView = ForecastView.HourlyView,
        screenSize = ScreenSize.Large
    )
}
