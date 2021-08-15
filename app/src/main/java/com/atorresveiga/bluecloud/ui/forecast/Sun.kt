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

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Sun drawing in a canvas
 * @param color color of the cloud
 * @param modifier Modifier
 * @param animate whether or not we should animate the sun
 */
@Composable
fun Sun(color: Color, modifier: Modifier = Modifier, animate: Boolean = true) {
    val sunFraction = 0.6f
    if (animate) {
        val infiniteTransition = rememberInfiniteTransition()
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
    } else {
        Canvas(modifier = modifier) {
            val maxRadius = size.minDimension * .5f
            val minRadius = maxRadius * sunFraction
            drawCircle(color = color, radius = minRadius)
        }
    }
}

@Preview(widthDp = 100, heightDp = 100)
@Composable
fun SunPreview() {
    Sun(modifier = Modifier.size(70.dp), color = Color.Yellow)
}
