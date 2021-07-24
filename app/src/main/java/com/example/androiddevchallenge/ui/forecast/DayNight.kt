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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.example.androiddevchallenge.data.FIRST_QUARTER
import com.example.androiddevchallenge.data.FULL_MOON
import com.example.androiddevchallenge.data.NEW_MOON
import com.example.androiddevchallenge.data.THIRD_QUARTER
import com.example.androiddevchallenge.data.WANING_CRESCENT
import com.example.androiddevchallenge.data.WANING_GIBBOUS
import com.example.androiddevchallenge.data.WAXING_CRESCENT
import com.example.androiddevchallenge.data.WAXING_GIBBOUS
import com.example.androiddevchallenge.ui.theme.moonColor
import com.example.androiddevchallenge.ui.theme.sunColor

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DayNight(
    currentHour: Int,
    sunriseHour: Int,
    sunsetHour: Int,
    moonPhaseId: Int,
    skyState: SkyState,
    isSouthernHemisphere: Boolean
) {
    val density = LocalDensity.current
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val smallSide = min(maxWidth, maxHeight)

        AnimatedVisibility(
            visible = skyState == SkyState.Night,
            enter = slideInVertically(
                initialOffsetY = { with(density) { (maxWidth * 1.5f).toPx().toInt() } },
                animationSpec = tween(
                    durationMillis = 200,
                    delayMillis = 100,
                    easing = FastOutSlowInEasing
                )
            ),
            exit = slideOutVertically(
                targetOffsetY = { with(density) { (maxWidth * 1.5f).toPx().toInt() } },
                animationSpec = tween(
                    durationMillis = 100,
                    easing = FastOutSlowInEasing
                )
            )

        ) {
            val size = smallSide * .5f
            val xOffset = maxWidth * .7f - size / 2
            val y by animateFloatAsState(
                targetValue =
                if (currentHour < sunriseHour) {
                    1f * currentHour / sunriseHour
                } else {
                    1f - 1f * (currentHour - sunsetHour) / (23 - sunsetHour)
                }
            )

            Moon(
                color = MaterialTheme.colors.moonColor,
                phase = LocalSettings.current.dataFormatter.moonPhase.decode(
                    moonPhaseId = moonPhaseId,
                    isBeforeSunrise = currentHour < sunriseHour
                ),
                modifier = Modifier
                    .size(size)
                    .offset(x = xOffset, y = maxHeight * y)
                    .graphicsLayer {
                        rotationY = if (isSouthernHemisphere) 180f else 0f
                    }

            )
        }

        AnimatedVisibility(
            visible = skyState != SkyState.Night,
            enter = slideInVertically(
                initialOffsetY = { with(density) { (maxHeight * 1.5f).toPx().toInt() } },
                animationSpec = tween(
                    durationMillis = 200,
                    delayMillis = 100,
                    easing = FastOutSlowInEasing
                )
            ),
            exit = slideOutVertically(
                targetOffsetY = { with(density) { (maxHeight * 3f).toPx().toInt() } },
                animationSpec = tween(
                    durationMillis = 100,
                    easing = LinearEasing
                )
            )

        ) {
            val size = smallSide * .8f
            val xOffset = maxWidth * .5f - size / 2
            val zenith = (sunsetHour - sunriseHour) / 2 + sunriseHour
            val y by animateFloatAsState(
                targetValue =
                if (currentHour <= zenith) {
                    1f - 1f * (currentHour - sunriseHour) / (zenith - sunriseHour)
                } else {
                    1f * (currentHour - zenith) / (sunsetHour - zenith)
                }
            )
            val yOffset = (maxHeight * y - size / 2).coerceAtLeast(size / 5)

            Sun(
                color = MaterialTheme.colors.sunColor,
                modifier = Modifier
                    .size(size)
                    .offset(
                        x = xOffset,
                        y = yOffset
                    )
            )
        }
    }
}

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

@Composable
fun Moon(modifier: Modifier = Modifier, color: Color, phase: Int = FULL_MOON) {
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
            NEW_MOON -> {
                Brush.radialGradient(
                    .65f to shadow,
                    .66f to color,
                    center = Offset(w50, h50)
                )
            }
            WAXING_CRESCENT -> Brush.radialGradient(
                .6f to shadow,
                .61f to color,
                radius = w50,
                center = Offset(w35, h50)
            )
            FIRST_QUARTER -> Brush.horizontalGradient(
                .5f to shadow,
                .51f to color,
                startX = 0f,
                endX = Float.POSITIVE_INFINITY
            )
            WAXING_GIBBOUS -> Brush.radialGradient(
                .6f to color,
                .61f to shadow,
                radius = w55,
                center = Offset(w65, h50)
            )
            FULL_MOON -> SolidColor(color)
            WANING_GIBBOUS -> Brush.radialGradient(
                .6f to color,
                .61f to shadow,
                radius = w60,
                center = Offset(w35, h50)
            )
            THIRD_QUARTER -> Brush.horizontalGradient(
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
        Moon(color = Color.Yellow, phase = NEW_MOON, modifier = Modifier.size(80.dp))
        Moon(
            color = Color.Yellow,
            phase = WAXING_CRESCENT,
            modifier = Modifier.size(80.dp)
        )
        Moon(color = Color.Yellow, phase = FIRST_QUARTER, modifier = Modifier.size(80.dp))
        Moon(color = Color.Yellow, phase = WAXING_GIBBOUS, modifier = Modifier.size(80.dp))
        Moon(color = Color.Yellow, phase = FULL_MOON, modifier = Modifier.size(80.dp))
        Moon(color = Color.Yellow, phase = WANING_GIBBOUS, modifier = Modifier.size(80.dp))
        Moon(color = Color.Yellow, phase = THIRD_QUARTER, modifier = Modifier.size(80.dp))
        Moon(
            color = Color.Yellow,
            phase = WANING_CRESCENT,
            modifier = Modifier.size(80.dp)
        )
    }
}
