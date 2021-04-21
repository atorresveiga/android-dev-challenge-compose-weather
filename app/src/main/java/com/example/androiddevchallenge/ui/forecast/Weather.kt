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
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.random.Random

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

/**
 * A data class that holds the weather offset data.
 * @param x coordinate x where this precipitation start (percent)
 * @param y coordinate y where this precipitation should start (percent)
 * @param z a coordinate that represents deep relative to the screen, where 1 is the far and 4 near
 */
data class WeatherOffset(val x: Float, val y: Float, val z: Int)

/**
 * Utils function to generate random precipitation offset data
 */
fun generateRandomWeatherOffsets(size: Int): List<WeatherOffset> {
    val result: MutableList<WeatherOffset> = mutableListOf()
    for (i in 1..size) {
        result.add(
            WeatherOffset(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                z = Random.nextInt(1, 5)
            )
        )
    }
    return result
}
