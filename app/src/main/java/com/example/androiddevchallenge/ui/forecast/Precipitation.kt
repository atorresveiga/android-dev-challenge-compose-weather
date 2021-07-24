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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.androiddevchallenge.data.RAIN_AND_SNOW
import com.example.androiddevchallenge.data.SNOW
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * This composable is the representation of a raindrop
 * @param weatherOffset the position of this raindrop in the screen
 */
@Composable
fun RainDrop(weatherOffset: WeatherOffset, sceneHeight: Float = -1f) {
    val infiniteTransition = rememberInfiniteTransition()

    val length = if (weatherOffset.z > 2) .015f else .01f
    val color = if (weatherOffset.z > 1) Color(0x88FFFFFF) else Color(0x88000000)
    val duration = Random.nextInt(1000, 2500)

    val y by infiniteTransition.animateFloat(
        initialValue = weatherOffset.y * -1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = duration
            },
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val height = if (sceneHeight > 0) sceneHeight else size.height
        drawLine(
            strokeWidth = 2f,
            color = color,
            start = Offset(x = size.width * weatherOffset.x, y = size.height * y),
            end = Offset(
                x = size.width * weatherOffset.x,
                y = size.height * y + height * length
            )
        )
    }
}

/**
 * This composable is the representation of a snowflake
 * @param weatherOffset the position of this raindrop in the screen
 */
@Composable
fun SnowFlake(weatherOffset: WeatherOffset, sceneHeight: Float = -1f) {
    val infiniteTransition = rememberInfiniteTransition()
    val duration = Random.nextInt(2000, 4000)
    val direction = if (Random.nextBoolean()) -1 else 1
    val length = if (weatherOffset.z > 2) .004f else .002f
    val color = Color(0x88FFFFFF)

    val x by infiniteTransition.animateFloat(
        initialValue = weatherOffset.x,
        targetValue = weatherOffset.x + direction / 10f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = duration
            },
            repeatMode = RepeatMode.Restart
        )
    )
    val y by infiniteTransition.animateFloat(
        initialValue = weatherOffset.y * -1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = duration,
                easing = FastOutLinearInEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val height = if (sceneHeight > 0) sceneHeight else size.height
        drawCircle(
            color = color,
            center = Offset(x = size.width * x, y = size.height * y),
            radius = height * length
        )
    }
}

/**
 * This composable is the representation of a precipitation (rain,snow,etc)
 * @param weatherId encoded value containing precipitation form and intensity. Full description in Forecast.kt
 * @param windDegrees wind direction, degrees (meteorological)
 * @param windSpeed wind speed.
 * @param modifier Modifier
 */
@Composable
fun Precipitation(
    weatherId: Int,
    windSpeed: Float,
    windDegrees: Float,
    precipitation: List<WeatherOffset>,
    modifier: Modifier = Modifier,
    sceneHeight: Float = -1f
) {
    val amount =
        (precipitation.size * LocalSettings.current.dataFormatter.precipitation.getIntensity(weatherId)).roundToInt()
    val form = LocalSettings.current.dataFormatter.precipitation.getForm(weatherId)

    val isWindSpeedStrong = windSpeed > 2

    val newRotation = when {
        isWindSpeedStrong && windDegrees in 25f..150f -> 25f
        isWindSpeedStrong && windDegrees in 200f..335f -> -25f
        else -> 0f
    }

    val rotation by animateFloatAsState(targetValue = newRotation)
    Box(modifier = modifier.rotate(rotation)) {
        val precipitationToDisplay = precipitation.take(amount)
        precipitationToDisplay.forEachIndexed { index, p ->
            when (form) {
                SNOW -> SnowFlake(p, sceneHeight)
                RAIN_AND_SNOW -> {
                    if (index < precipitationToDisplay.size / 2) {
                        RainDrop(p, sceneHeight)
                    } else {
                        SnowFlake(p, sceneHeight)
                    }
                }
                else -> RainDrop(p, sceneHeight)
            }
        }
    }
}

@Preview(widthDp = 100, heightDp = 100, backgroundColor = 0xFF000000, showBackground = true)
@Composable
fun PrecipitationPreview() {
    val precipitation = generateRandomWeatherOffsets(100)
    for (i in precipitation) {
        SnowFlake(weatherOffset = i)
    }
}
