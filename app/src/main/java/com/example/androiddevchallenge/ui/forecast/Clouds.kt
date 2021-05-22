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
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.LocalDataFormatter
import com.example.androiddevchallenge.ui.theme.lightningColor
import com.example.androiddevchallenge.ui.theme.stormCloudColor

/**
 * Clouds representation in the sky
 * @param cloudiness cloudiness (percent)
 * @param weatherId encoded value containing if the weather has thunders. Full description in Forecast.kt.
 * @param direction in which direction the user is navigating (used in clouds visibility animation)
 * @param modifier Modifier
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Clouds(
    cloudiness: Int,
    weatherId: Int,
    direction: Direction,
    modifier: Modifier = Modifier
) {

    val clouds by remember { mutableStateOf(generateRandomWeatherOffsets(50)) }
    val hasThunders = LocalDataFormatter.current.weather.hasThunders(weatherId)

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {

        val total = clouds.size * cloudiness / 100

        val density = LocalDensity.current

        clouds.forEachIndexed { index, cloudOffset ->

            val isReverse = index % 3 == 0
            val hasLightning = index > total - 5 && hasThunders && cloudOffset.z > 2

            val (width, height, alpha) = getCloudModifiers(cloudOffset.z)

            val x = (maxWidth * cloudOffset.x) - width / 2
            val y = maxHeight * cloudOffset.y

            var cloudModifier = Modifier
                .size(width = width, height = height)
                .offset(x = x, y = y)
                .alpha(alpha)

            if (isReverse && !hasLightning) {
                cloudModifier = cloudModifier.graphicsLayer(rotationY = 180f)
            }

            val enterDirection = if (direction == Direction.FORWARD) 2 else -2
            val exitDirection = if (direction == Direction.FORWARD) -1 else 1

            AnimatedVisibility(
                visible = index < total,
                enter = slideInHorizontally(
                    initialOffsetX = {
                        with(density) {
                            ((maxWidth + width) * enterDirection).toPx().toInt()
                        }
                    },
                    animationSpec = tween(
                        durationMillis = 200 * cloudOffset.z,
                        easing = LinearOutSlowInEasing
                    )
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = {
                        with(density) {
                            ((maxWidth + width + x) * exitDirection).toPx().toInt()
                        }
                    },
                    animationSpec = tween(
                        durationMillis = 100 * cloudOffset.z,
                        easing = LinearEasing
                    )
                )

            ) {
                StormCloudWithLightning(
                    color = MaterialTheme.colors.stormCloudColor,
                    lightningColor = MaterialTheme.colors.lightningColor,
                    lightningDuration = 8000 / cloudOffset.z,
                    modifier = cloudModifier,
                    drawLightning = hasLightning
                )
            }
        }
    }
}

/**
 * Function to generate width,height and alpha of a cloud by taking into account the cloud z offset
 * @param z cloudiness (percent)
 */
fun getCloudModifiers(z: Int): Triple<Dp, Dp, Float> {
    return when (z) {
        4 -> {
            Triple(300.dp, 320.dp, .98f)
        }
        3 -> {
            Triple(280.dp, 300.dp, .95f)
        }
        2 -> {
            Triple(250.dp, 270.dp, .55f)
        }
        else -> {
            Triple(200.dp, 220.dp, .4f)
        }
    }
}

/**
 * Cloud drawing in a canvas
 * @param color color of the cloud
 * @param modifier Modifier
 */
@Composable
fun Cloud(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {

        val horizontalMargin = (size.width * .15f) * .5f
        val verticalMargin = (size.height * .15f) * .5f

        val bottomCloudTop = (size.height - verticalMargin) * .4f
        val bottomCloudSize = (size.width - horizontalMargin) * .4f
        val topCloudLeft = horizontalMargin + bottomCloudSize * .5f

        val path = Path()

        path.arcTo(
            rect = Rect(
                top = bottomCloudTop,
                left = horizontalMargin,
                right = horizontalMargin + bottomCloudSize,
                bottom = size.height - verticalMargin
            ),
            startAngleDegrees = 47f,
            sweepAngleDegrees = 225f,
            forceMoveTo = false
        )
        path.arcTo(
            rect = Rect(
                top = bottomCloudTop,
                left = size.width - horizontalMargin - bottomCloudSize,
                right = size.width - horizontalMargin,
                bottom = size.height - verticalMargin
            ),
            startAngleDegrees = 290f,
            sweepAngleDegrees = 200f,
            forceMoveTo = false
        )

        path.arcTo(
            rect = Rect(
                top = bottomCloudTop,
                left = (horizontalMargin + size.width) * .3f,
                right = (horizontalMargin + size.width) * .3f + bottomCloudSize,
                bottom = size.height - verticalMargin
            ),
            startAngleDegrees = 45f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        path.arcTo(
            rect = Rect(
                top = verticalMargin,
                left = topCloudLeft,
                right = topCloudLeft + bottomCloudSize,
                bottom = verticalMargin + bottomCloudSize
            ),
            startAngleDegrees = 176f,
            sweepAngleDegrees = 160f,
            forceMoveTo = false
        )

        path.arcTo(
            rect = Rect(
                top = verticalMargin + bottomCloudSize * .25f,
                left = topCloudLeft + bottomCloudSize - bottomCloudSize * .25f,
                right = topCloudLeft + bottomCloudSize - bottomCloudSize * .25f + bottomCloudSize * .75f,
                bottom = verticalMargin + bottomCloudSize
            ),
            startAngleDegrees = 242f,
            sweepAngleDegrees = 111f,
            forceMoveTo = false
        )

        path.close()
        drawPath(path = path, color = color)
    }
}

@Preview(widthDp = 300, heightDp = 200)
@Composable
fun CloudPreview() {
    Cloud(color = Color.White, modifier = Modifier.size(width = 120.dp, height = 30.dp))
}

/**
 * Lightning drawing in a canvas
 * @param color color of the lightning
 * @param alpha alpha of the lightning
 * @param modifier Modifier
 */
@Composable
fun Lightning(color: Color, alpha: Float, modifier: Modifier = Modifier) {
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
    Lightning(color = Color.Yellow, alpha = 1f)
}

/**
 * StormCloud is a [Cloud] with lightning reflection
 * @param color color of the cloud
 * @param lightningColor color of the lightning
 * @param lightningAlpha alpha of the lightning
 * @param modifier Modifier
 */
@Composable
fun StormCloud(
    color: Color,
    lightningColor: Color,
    lightningAlpha: Float,
    modifier: Modifier = Modifier
) {
    Cloud(
        color = color,
        modifier = modifier.drawWithContent {

            this.drawContent()

            val horizontalMargin = (size.width * .15f) * .5f
            val verticalMargin = (size.height * .15f) * .5f
            val bottomCloudTop = (size.height - verticalMargin) * .4f
            val bottomCloudSize = (size.width - horizontalMargin) * .4f

            val lightning = Path()
            lightning.arcTo(
                rect = Rect(
                    top = bottomCloudTop,
                    left = horizontalMargin,
                    right = horizontalMargin + bottomCloudSize,
                    bottom = size.height - verticalMargin
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )

            lightning.arcTo(
                rect = Rect(
                    top = bottomCloudTop,
                    left = size.width - horizontalMargin - bottomCloudSize,
                    right = size.width - horizontalMargin,
                    bottom = size.height - verticalMargin
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )

            lightning.arcTo(
                rect = Rect(
                    top = bottomCloudTop,
                    left = (horizontalMargin + size.width) * .3f,
                    right = (horizontalMargin + size.width) * .3f + bottomCloudSize,
                    bottom = size.height - verticalMargin
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )

            drawPath(
                path = lightning,
                brush = Brush.verticalGradient(
                    0.7f to Color.Transparent,
                    .9f to lightningColor,
                    startY = 0.0f,
                    endY = Float.POSITIVE_INFINITY
                ),
                alpha = lightningAlpha
            )
        }
    )
}

@Preview(widthDp = 150, heightDp = 100)
@Composable
fun StormCloudPreview() {
    StormCloud(
        color = Color.White,
        lightningColor = Color.Yellow,
        lightningAlpha = 1f
    )
}

/**
 * Hoisting [StormCloudWithLightning] with an infinite animation of the lightning alpha to simulate
 * a real lightning
 * @param color color of the cloud
 * @param lightningColor color of the lightning
 * @param lightningDuration duration of the alpha animation
 * @param modifier Modifier
 * @param drawLightning when we should draw the Lightning.
 */
@Composable
fun StormCloudWithLightning(
    color: Color,
    lightningColor: Color,
    lightningDuration: Int,
    modifier: Modifier = Modifier,
    drawLightning: Boolean = true
) {
    val alpha = if (drawLightning) {
        val infiniteTransition = rememberInfiniteTransition()
        val value by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                keyframes {
                    durationMillis = lightningDuration
                    0f at lightningDuration / 3
                    1f at lightningDuration / 5
                    0f at lightningDuration / 7
                },
                repeatMode = RepeatMode.Restart
            )
        )
        value
    } else {
        0f
    }
    StormCloudWithLightning(
        color = color,
        lightningColor = lightningColor,
        lightningAlpha = alpha,
        modifier = modifier,
        drawLightning = drawLightning
    )
}

/**
 * StormCloud with a lightning
 * @param color color of the cloud
 * @param lightningColor color of the lightning
 * @param lightningAlpha alpha of the lightning
 * @param modifier Modifier
 * @param drawLightning when we should draw the Lightning.
 */
@Composable
fun StormCloudWithLightning(
    color: Color,
    lightningColor: Color,
    lightningAlpha: Float,
    modifier: Modifier = Modifier,
    drawLightning: Boolean = true
) {
    BoxWithConstraints(modifier = modifier) {
        StormCloud(
            color = color,
            lightningColor = lightningColor.copy(alpha = .8f),
            lightningAlpha = lightningAlpha,
            modifier = Modifier
                .width(maxWidth)
                .height(maxHeight * .62f)
        )
        if (drawLightning) {
            Lightning(
                color = lightningColor,
                alpha = lightningAlpha,
                modifier = Modifier
                    .height(maxHeight * .45f)
                    .width(maxWidth * .35f)
                    .offset(x = maxWidth * .3f, y = maxHeight * .5f)
            )
        }
    }
}

@Preview(widthDp = 300, heightDp = 320)
@Composable
fun StormCloudWithLightningPreview() {
    StormCloudWithLightning(
        color = Color(0x88555555),
        lightningColor = Color.Yellow,
        lightningAlpha = 1f
    )
}
