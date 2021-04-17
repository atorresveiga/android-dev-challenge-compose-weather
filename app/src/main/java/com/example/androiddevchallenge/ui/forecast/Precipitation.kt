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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.androiddevchallenge.ui.LocalDataFormatter
import com.example.androiddevchallenge.ui.PrecipitationForm
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * This composable is the representation of a raindrop
 * @param precipitationOffset the position of this raindrop in the screen
 */
@Composable
fun RainDrop(precipitationOffset: PrecipitationOffset) {
    val infiniteTransition = rememberInfiniteTransition()

    val length = if (precipitationOffset.z > 2) .015f else .01f
    val color = if (precipitationOffset.z > 1) Color(0x88FFFFFF) else Color(0x88000000)
    val duration = Random.nextInt(1000, 2500)

    val y by infiniteTransition.animateFloat(
        initialValue = precipitationOffset.y * -1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = duration
            },
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawLine(
            strokeWidth = 2f,
            color = color,
            start = Offset(x = size.width * precipitationOffset.x, y = size.height * y),
            end = Offset(
                x = size.width * precipitationOffset.x,
                y = size.height * y + size.height * length
            )
        )
    }
}

/**
 * This composable is the representation of a snowflake
 * @param precipitationOffset the position of this raindrop in the screen
 */
@Composable
fun SnowFlake(precipitationOffset: PrecipitationOffset) {
    val infiniteTransition = rememberInfiniteTransition()
    val duration = Random.nextInt(2000, 4000)
    val direction = if (Random.nextBoolean()) -1 else 1
    val length = if (precipitationOffset.z > 2) .004f else .002f
    val color = Color(0x88FFFFFF)

    val x by infiniteTransition.animateFloat(
        initialValue = precipitationOffset.x,
        targetValue = precipitationOffset.x + direction / 10f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = duration
            },
            repeatMode = RepeatMode.Restart
        )
    )
    val y by infiniteTransition.animateFloat(
        initialValue = precipitationOffset.y * -1f,
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
        drawCircle(
            color = color,
            center = Offset(x = size.width * x, y = size.height * y),
            radius = size.height * length
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
    modifier: Modifier = Modifier
) {
    val precipitation by remember { mutableStateOf(generateRandomPrecipitation()) }
    val amount =
        (precipitation.size * LocalDataFormatter.current.precipitation.getIntensity(weatherId)).roundToInt()
    val form = LocalDataFormatter.current.precipitation.getForm(weatherId)

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
                PrecipitationForm.Snow -> SnowFlake(p)
                PrecipitationForm.Rain -> RainDrop(p)
                PrecipitationForm.RainAndSnow -> {
                    if (index < precipitationToDisplay.size / 2) {
                        RainDrop(p)
                    } else {
                        SnowFlake(p)
                    }
                }
            }
        }
    }
}

/**
 * A data class that holds the precipitation offset data.
 * @param x coordinate x where this precipitation start (percent)
 * @param y coordinate y where this precipitation should start (percent)
 * @param z a coordinate that represents deep relative to the screen, where 1 is the far and 4 near
 */
data class PrecipitationOffset(val x: Float, val y: Float, val z: Int)

/**
 * Utils function to generate random precipitation offset data
 */
private fun generateRandomPrecipitation(): List<PrecipitationOffset> {
    val result: MutableList<PrecipitationOffset> = mutableListOf()
    for (i in 0..250) {
        result.add(
            PrecipitationOffset(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                z = Random.nextInt(1, 4)
            )
        )
    }
    return result
}

@Preview(widthDp = 100, heightDp = 100, backgroundColor = 0xFF000000, showBackground = true)
@Composable
fun PrecipitationPreview() {
    val precipitation = generateRandomPrecipitation()
    for (i in precipitation) {
        SnowFlake(precipitationOffset = i)
    }
}
