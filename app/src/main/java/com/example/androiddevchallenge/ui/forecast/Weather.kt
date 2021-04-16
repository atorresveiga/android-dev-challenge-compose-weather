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
package com.example.androiddevchallenge.ui.forecast

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Sun(modifier: Modifier = Modifier, color: Color) {
    val infiniteTransition = rememberInfiniteTransition()
    val sunFraction = 0.6f
    val ripple by infiniteTransition.animateFloat(
        initialValue = sunFraction,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2500,
                easing = FastOutLinearInEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )
    Canvas(modifier = modifier) {
        val maxRadius = size.minDimension * .5f
        val minRadius = maxRadius * sunFraction
        drawCircle(color = color, radius = maxRadius * ripple, alpha = 1 - ripple)
        drawCircle(color = color, radius = minRadius)
    }
}

@Preview(widthDp = 100, heightDp = 100)
@Composable
fun SunPreview() {
    Sun(modifier = Modifier.size(70.dp), Color.Yellow)
}

@Composable
fun Cloud(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {

        val horizontalMargin = (size.width * .15f) * .5f
        val verticalMargin = (size.height * .15f) * .5f

        val bottomCloudTop = (size.height - verticalMargin) * .4f
        val bottomCloudSize = (size.width - horizontalMargin) * .4f
        val topCloudLeft = horizontalMargin + bottomCloudSize * .5f

        val path = Path()
        path.addArc(
            oval = Rect(
                top = bottomCloudTop,
                left = horizontalMargin,
                right = horizontalMargin + bottomCloudSize,
                bottom = size.height - verticalMargin
            ),
            startAngleDegrees = 0f, // 47
            sweepAngleDegrees = 360f // 225
        )
        path.addArc(
            oval = Rect(
                top = bottomCloudTop,
                left = size.width - horizontalMargin - bottomCloudSize,
                right = size.width - horizontalMargin,
                bottom = size.height - verticalMargin
            ),
            startAngleDegrees = 0f, // 293
            sweepAngleDegrees = 360f // 195
        )

        path.addArc(
            oval = Rect(
                top = bottomCloudTop,
                left = (horizontalMargin + size.width) * .3f,
                right = (horizontalMargin + size.width) * .3f + bottomCloudSize,
                bottom = size.height - verticalMargin
            ),
            startAngleDegrees = 0f, // 45
            sweepAngleDegrees = 360f // 85
        )

        path.addArc(
            oval = Rect(
                top = verticalMargin,
                left = topCloudLeft,
                right = topCloudLeft + bottomCloudSize,
                bottom = verticalMargin + bottomCloudSize
            ),
            startAngleDegrees = 0f, // 176
            sweepAngleDegrees = 360f // 160
        )

        path.addArc(
            oval = Rect(
                top = verticalMargin + bottomCloudSize * .25f,
                left = topCloudLeft + bottomCloudSize - bottomCloudSize * .25f,
                right = topCloudLeft + bottomCloudSize - bottomCloudSize * .25f + bottomCloudSize * .75f,
                bottom = verticalMargin + bottomCloudSize
            ),
            startAngleDegrees = 0f, // 242
            sweepAngleDegrees = 360f // 111
        )

        drawPath(path = path, color = color)
    }
}

@Preview(widthDp = 150, heightDp = 100)
@Composable
fun CloudPreview() {
    Cloud(color = Color.White, modifier = Modifier.size(width = 120.dp, height = 30.dp))
}

@Composable
fun Lightning(modifier: Modifier = Modifier, color: Color) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 1000
                0f at 100
                1f at 300
                0f at 600
            },
            repeatMode = RepeatMode.Restart
        )
    )
    Canvas(modifier = modifier) {
        val horizontalMargin = (size.width * .15f) * .5f
        val verticalMargin = (size.height * .15f) * .5f

        val w50 = size.width * .5f
        val w45 = size.width * .45f
        val w20 = size.width * .2f
        val w10 = size.width * .1f

        val path = Path()
        path.moveTo(y = verticalMargin, x = w45)
        path.lineTo(y = verticalMargin, x = size.width - horizontalMargin - w10)
        path.lineTo(y = verticalMargin + w50, x = w50)
        path.lineTo(
            y = verticalMargin + w50,
            x = horizontalMargin + w20
        )

        path.moveTo(y = verticalMargin + w20, x = w50)
        path.lineTo(y = verticalMargin + w20, x = size.width - horizontalMargin)
        path.lineTo(y = size.height - verticalMargin, x = horizontalMargin + w20)

        drawPath(path = path, color = color, alpha = alpha)
    }
}

@Preview(widthDp = 75, heightDp = 100)
@Composable
fun LightningPreview() {
    Lightning(color = Color.Yellow)
}

@Composable
fun Moon(modifier: Modifier = Modifier, color: Color, phase: MoonPhase = MoonPhase.FullMoon) {
    Canvas(modifier = modifier) {

        val h50 = size.height * .5f

        val w65 = size.width * .65f
        val w60 = size.width * .60f
        val w55 = size.width * .55f
        val w50 = size.width * .5f
        val w35 = size.width * .35f

        val brush = when (phase) {
            MoonPhase.NewMoon -> {
                Brush.radialGradient(
                    .65f to Color(0x22000000),
                    .66f to color,
                    center = Offset(w50, h50)
                )
            }
            MoonPhase.WaxingCrescent -> Brush.radialGradient(
                .6f to Color(0x22000000),
                .61f to color,
                radius = w50,
                center = Offset(w35, h50)
            )
            MoonPhase.FirstQuarter -> Brush.horizontalGradient(
                .5f to Color(0x22000000),
                .51f to color,
                startX = 0f,
                endX = Float.POSITIVE_INFINITY
            )
            MoonPhase.WaxingGibbous -> Brush.radialGradient(
                .6f to color,
                .61f to Color(0x22000000),
                radius = w55,
                center = Offset(w65, h50)
            )
            MoonPhase.FullMoon -> SolidColor(color)
            MoonPhase.WaningGibbous -> Brush.radialGradient(
                .6f to color,
                .61f to Color(0x22000000),
                radius = w60,
                center = Offset(w35, h50)
            )
            MoonPhase.ThirdQuarter -> Brush.horizontalGradient(
                .49f to color,
                .5f to Color(0x22000000),
                startX = 0f,
                endX = Float.POSITIVE_INFINITY
            )
            MoonPhase.WaningCrescent -> Brush.radialGradient(
                .6f to Color(0x22000000),
                .61f to color,
                radius = w55,
                center = Offset(w65, h50)
            )
        }

        drawCircle(
            brush = brush,
            radius = w35
        )
    }
}

enum class MoonPhase {
    NewMoon, WaxingCrescent, FirstQuarter, WaxingGibbous, FullMoon, WaningGibbous, ThirdQuarter, WaningCrescent
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
