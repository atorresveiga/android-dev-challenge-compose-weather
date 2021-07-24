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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atorresveiga.bluecloud.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

/**
 * Wind indicator control
 * @param windDegrees wind direction, degrees (meteorological)
 * @param windSpeed wind speed.
 * @param color text & shape colors
 * @param modifier Modifier
 */
@Composable
fun WindIndicator(
    windDegrees: Float,
    windSpeed: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        // In meteorological degrees North is 0°
        val degrees by animateFloatAsState(targetValue = windDegrees - 90f)
        Box(
            Modifier
                .size(72.dp)
                .drawBehind {
                    val pointerSize = size.minDimension * .10f
                    val strokeWidth = 4.dp.toPx()
                    val margin = 8.dp.toPx()
                    val backgroundRadius =
                        (size.minDimension - (pointerSize * 4) - strokeWidth - margin) / 2

                    val pointerRadius = backgroundRadius + 4.dp.toPx()

                    val pointerPath = Path()
                    val pointerStartOffset =
                        (degrees - 160).getCircularOffset(center, pointerRadius)
                    val pointerEndOffset =
                        (degrees - 200).getCircularOffset(center, pointerRadius)
                    val pointerControlOffset =
                        (degrees - 180).getCircularOffset(center, pointerRadius + pointerSize * .2f)
                    val pointerTopOffset =
                        (degrees - 180).getCircularOffset(center, pointerRadius + pointerSize)

                    pointerPath.moveTo(pointerStartOffset.x, pointerStartOffset.y)
                    pointerPath.quadraticBezierTo(
                        x1 = pointerControlOffset.x,
                        y1 = pointerControlOffset.y,
                        x2 = pointerEndOffset.x,
                        y2 = pointerEndOffset.y
                    )
                    pointerPath.lineTo(pointerTopOffset.x, pointerTopOffset.y)
                    pointerPath.close()

                    drawPath(
                        path = pointerPath,
                        color = color
                    )

                    drawCircle(
                        color = color,
                        radius = backgroundRadius,
                        center = center,
                        style = Stroke(width = strokeWidth)
                    )
                }
        ) {

            val speed =
                LocalSettings.current.dataFormatter.wind.getValue(windSpeed).roundToInt().toString()
            val style = if (speed.length > 2)
                MaterialTheme.typography.button.copy(fontSize = 12.sp)
            else
                MaterialTheme.typography.button

            Text(
                modifier = Modifier.align(Alignment.Center),
                text = speed,
                style = style
            )

            for (i in 0..(windSpeed.toInt() * 3).coerceAtMost(27)) {
                WindFlow(
                    windDegrees = degrees,
                    windSpeed = windSpeed,
                    color = color,
                    isClockWise = i % 2 == 0,
                    margin = if (i % 3 == 0) 0.dp else Random.nextInt(0, 8).dp
                )
            }
        }
        Text(
            text = LocalSettings.current.dataFormatter.wind.getScale(windSpeed)
        )
        Text(
            text = stringResource(
                R.string.wind_direction,
                LocalSettings.current.dataFormatter.wind.getDirection(windDegrees),
                LocalSettings.current.dataFormatter.wind.getMeasurement()
            )
        )
    }
}

/**
 * Wind flow animation
 * @param windDegrees wind direction, degrees (meteorological)
 * @param windSpeed wind speed.
 * @param isClockWise boolean to indicate if the animation is clockwise or counterclockwise
 * @param margin margin of the inner circle. Affects the start point & control points of the bezier
 */
@Composable
fun WindFlow(
    windDegrees: Float,
    windSpeed: Float,
    color: Color,
    isClockWise: Boolean = false,
    margin: Dp
) {

    val infiniteTransition = rememberInfiniteTransition()
    val duration = (3300 / windSpeed).coerceIn(250f, 1500f).roundToInt()
    val delay = Random.nextInt(duration / 2)

    val time by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = duration,
                delayMillis = delay,
                easing = FastOutLinearInEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = duration
                delayMillis = delay
                1f at (duration * .5f).roundToInt()
            },
            repeatMode = RepeatMode.Restart
        )
    )

    val clockWiseDirection = if (isClockWise) 1 else -1

    Canvas(modifier = Modifier.fillMaxSize()) {
        val backgroundRadius = (size.minDimension) / 2
        val marginPX = margin.toPx()
        val start =
            (windDegrees).getCircularOffset(center, backgroundRadius - marginPX)

        val control1 =
            (windDegrees + 80 * clockWiseDirection).getCircularOffset(
                center,
                backgroundRadius - marginPX
            )
        val control2 =
            (windDegrees + 100 * clockWiseDirection).getCircularOffset(
                center,
                backgroundRadius - marginPX
            )
        val end =
            (windDegrees + 180 * clockWiseDirection).getCircularOffset(
                center,
                backgroundRadius
            )

        val offset = calculateBezier(
            time = time,
            start = start,
            control1 = control1,
            control2 = control2,
            end = end
        )

        val offset2 = calculateBezier(
            time = time - .08f,
            start = start,
            control1 = control1,
            control2 = control2,
            end = end
        )
        drawLine(
            color = color,
            start = offset,
            end = offset2,
            alpha = alpha
        )
    }
}

/**
 * Util function to calculate [WindFlow] bezier
 * @param time get position in time from 0f to 1f
 * @param start start point
 * @param control1 first control point
 * @param control2 second control point
 * @param end end point
 */
fun calculateBezier(
    time: Float,
    start: Offset,
    control1: Offset,
    control2: Offset,
    end: Offset
): Offset {
    val m: Float = 1f - time

    var x = start.x * m.pow(3)
    var y = start.y * m.pow(3)

    x += 3 * m.pow(2) * time * control1.x
    y += 3 * m.pow(2) * time * control1.y

    x += 3 * m * time.pow(2) * control2.x
    y += 3 * m * time.pow(2) * control2.y

    x += time.pow(3) * end.x
    y += time.pow(3) * end.y

    return Offset(x = x, y = y)
}

/**
 * Util function to calculate the (x,y) position of a degree in a circumference
 * @param center (x,y) position of the center of the circumference
 * @param radius of the circumference
 */
fun Float.getCircularOffset(
    center: Offset = Offset(0f, 0f),
    radius: Float
): Offset {
    val x = center.x + radius * cos(this * PI / 180).toFloat()
    val y = center.y + radius * sin(this * PI / 180).toFloat()
    return Offset(x, y)
}
