package com.example.androiddevchallenge.ui.forecast

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.model.HourForecast
import java.time.format.TextStyle
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

@Composable
fun WindIndicator(hourForecast: HourForecast, color: Color, modifier: Modifier = Modifier) {
    // Multiply -1 to get counterclockwise direction
    val degrees by animateFloatAsState(targetValue = hourForecast.windDegrees * -1)
    Box(
        modifier = modifier
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

        val speed = hourForecast.windSpeed.roundToInt()
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = speed.toString(),
            style = MaterialTheme.typography.button
        )

        for (i in 0..speed * 4) {
            WindFlow(
                degrees = degrees,
                speed = hourForecast.windSpeed,
                color = color,
                isClockWise = i % 2 == 0,
                marginStart = if (i % 3 == 0) 0.dp else Random.nextInt(6, 8).dp
            )
        }
    }
}

@Composable
fun WindFlow(
    degrees: Float,
    speed: Float,
    color: Color,
    isClockWise: Boolean = false,
    marginStart: Dp
) {

    val infiniteTransition = rememberInfiniteTransition()
    val duration = (3000 / speed).coerceIn(400f, 1500f).roundToInt()
    val delay = (200 * Random.nextInt(0, 5))

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


    Spacer(modifier =
    Modifier
        .fillMaxSize()
        .drawBehind {
            val backgroundRadius = (size.minDimension) / 2
            val marginStartPX = marginStart.toPx()
            val start =
                (degrees).getCircularOffset(center, backgroundRadius - marginStartPX)

            val control1 =
                (degrees + 80 * clockWiseDirection).getCircularOffset(
                    center,
                    backgroundRadius - marginStartPX
                )
            val control2 =
                (degrees + 100 * clockWiseDirection).getCircularOffset(
                    center,
                    backgroundRadius - marginStartPX
                )
            val end =
                (degrees + 180 * clockWiseDirection).getCircularOffset(center, backgroundRadius)

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
    )
}

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

// TODO find a better way to do this
fun Long.toHourFormat(): String {
    val datetime = Instant.fromEpochSeconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    val currentTime = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())

    val date = when (datetime.date) {
        currentTime.date -> "Today"
        currentTime.date.plus(1, DateTimeUnit.DAY) -> "Tomorrow"
        else -> datetime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.US)
    }

    return "$date ${datetime.hour.toString().padStart(2, '0')}:00 h"
}