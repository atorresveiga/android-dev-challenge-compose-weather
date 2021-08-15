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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atorresveiga.bluecloud.data.MoonPhase

/**
 * Moon drawing in a canvas
 * @param phase [MoonPhase] to display
 * @param color moon color
 * @param modifier Modifier
 */
@Composable
fun Moon(
    phase: MoonPhase,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {

        val h70 = size.height * .7f
        val h55 = size.height * .55f
        val h50 = size.height * .5f
        val h40 = size.height * .4f
        val h35 = size.height * .35f

        val w75 = size.width * .75f
        val w65 = size.width * .65f
        val w60 = size.width * .60f
        val w55 = size.width * .55f
        val w50 = size.width * .5f
        val w40 = size.width * .4f
        val w35 = size.width * .35f
        val w8 = size.width * .08f
        val w4 = size.width * .04f
        val w2 = size.width * .02f

        val shadow = Color(0x22000000)

        val brush = when (phase) {
            MoonPhase.NewMoon -> {
                Brush.radialGradient(
                    .65f to shadow,
                    .66f to color,
                    center = Offset(w50, h50)
                )
            }
            MoonPhase.WaxingCrescent -> Brush.radialGradient(
                .6f to shadow,
                .61f to color,
                radius = w50,
                center = Offset(w35, h50)
            )
            MoonPhase.FirstQuarter -> Brush.horizontalGradient(
                .5f to shadow,
                .51f to color,
                startX = 0f,
                endX = Float.POSITIVE_INFINITY
            )
            MoonPhase.WaxingGibbous -> Brush.radialGradient(
                .6f to color,
                .61f to shadow,
                radius = w55,
                center = Offset(w65, h50)
            )
            MoonPhase.FullMoon -> SolidColor(color)
            MoonPhase.WaningGibbous -> Brush.radialGradient(
                .6f to color,
                .61f to shadow,
                radius = w60,
                center = Offset(w35, h50)
            )
            MoonPhase.ThirdQuarter -> Brush.horizontalGradient(
                .49f to color,
                .5f to shadow,
                startX = 0f,
                endX = Float.POSITIVE_INFINITY
            )
            else -> Brush.radialGradient( // WANING_CRESCENT
                .6f to shadow,
                .61f to color,
                radius = w55,
                center = Offset(w65, h50)
            )
        }

        drawCircle(
            brush = brush,
            radius = w35
        )

        drawCircle(
            color = shadow,
            radius = w8,
            center = Offset(x = w55, y = h35)
        )

        drawCircle(
            color = shadow,
            radius = w4,
            center = Offset(x = w35, y = h40)
        )

        drawCircle(
            color = shadow,
            radius = w2,
            center = Offset(x = w55, y = h55)
        )

        drawCircle(
            color = shadow,
            radius = w8,
            center = Offset(x = w40, y = h70)
        )

        drawCircle(
            color = shadow,
            radius = w4,
            center = Offset(x = w65, y = h70)
        )

        drawCircle(
            color = shadow,
            radius = w2,
            center = Offset(x = w75, y = h40)
        )
    }
}

@Preview(widthDp = 640, heightDp = 80)
@Composable
fun MoonPreview() {
    Row {
        Moon(color = Color.Yellow, phase = MoonPhase.NewMoon, modifier = Modifier.size(80.dp))
        Moon(
            color = Color.Yellow,
            phase = MoonPhase.WaxingCrescent,
            modifier = Modifier.size(80.dp)
        )
        Moon(color = Color.Yellow, phase = MoonPhase.FirstQuarter, modifier = Modifier.size(80.dp))
        Moon(color = Color.Yellow, phase = MoonPhase.WaxingGibbous, modifier = Modifier.size(80.dp))
        Moon(color = Color.Yellow, phase = MoonPhase.FullMoon, modifier = Modifier.size(80.dp))
        Moon(color = Color.Yellow, phase = MoonPhase.WaningGibbous, modifier = Modifier.size(80.dp))
        Moon(color = Color.Yellow, phase = MoonPhase.ThirdQuarter, modifier = Modifier.size(80.dp))
        Moon(
            color = Color.Yellow,
            phase = MoonPhase.WaningCrescent,
            modifier = Modifier.size(80.dp)
        )
    }
}
