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
package com.atorresveiga.bluecloud.ui.forecast.hourly

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.min
import com.atorresveiga.bluecloud.ui.common.settings
import com.atorresveiga.bluecloud.ui.forecast.LocalSettings
import com.atorresveiga.bluecloud.ui.forecast.Moon
import com.atorresveiga.bluecloud.ui.forecast.Sun
import com.atorresveiga.bluecloud.ui.theme.moonColor
import com.atorresveiga.bluecloud.ui.theme.sunColor

/**
 * DayNight shows, hides and position the sun and moon taking into account current hour, sunset hour
 * and sunrise hour (this sun and moon position is an aesthetic calculation and not a real one)
 * @param currentHour hour to represent
 * @param sunriseHour current day's sunrise hour
 * @param sunsetHour current day's sunset hour
 * @param moonPhaseId current day's moon phase id (MoonPhase ordinal)
 * @param sky current [Sky] state
 * @param isSouthernHemisphere if the current location is in the south hemisphere,some moon phases
 * are shown differently in each hemisphere
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DayNight(
    currentHour: Int,
    sunriseHour: Int,
    sunsetHour: Int,
    moonPhaseId: Int,
    sky: Sky,
    isSouthernHemisphere: Boolean
) {
    val density = LocalDensity.current
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val smallSide = min(maxWidth, maxHeight)

        AnimatedVisibility(
            visible = sky == Sky.Night,
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
            visible = sky != Sky.Night,
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

@Preview(widthDp = 360, heightDp = 600)
@Composable
fun DayNightDayPreview() {
    CompositionLocalProvider(LocalSettings provides settings) {
        DayNight(
            currentHour = 10,
            sunriseHour = 6,
            sunsetHour = 19,
            moonPhaseId = 2,
            sky = Sky.Day,
            isSouthernHemisphere = false
        )
    }
}

@Preview(widthDp = 360, heightDp = 600)
@Composable
fun DayNightNightPreview() {
    CompositionLocalProvider(LocalSettings provides settings) {
        DayNight(
            currentHour = 2,
            sunriseHour = 6,
            sunsetHour = 19,
            moonPhaseId = 1,
            sky = Sky.Night,
            isSouthernHemisphere = false
        )
    }
}

@Preview(widthDp = 360, heightDp = 600)
@Composable
fun DayNightNightSouthPreview() {
    CompositionLocalProvider(LocalSettings provides settings) {
        DayNight(
            currentHour = 2,
            sunriseHour = 6,
            sunsetHour = 19,
            moonPhaseId = 1,
            sky = Sky.Night,
            isSouthernHemisphere = true
        )
    }
}

@Preview(widthDp = 360, heightDp = 600)
@Composable
fun DayNightSunrisePreview() {
    CompositionLocalProvider(LocalSettings provides settings) {
        DayNight(
            currentHour = 6,
            sunriseHour = 6,
            sunsetHour = 19,
            moonPhaseId = 1,
            sky = Sky.Sunrise,
            isSouthernHemisphere = false
        )
    }
}

@Preview(widthDp = 360, heightDp = 600)
@Composable
fun DayNightSunsetPreview() {
    CompositionLocalProvider(LocalSettings provides settings) {
        DayNight(
            currentHour = 19,
            sunriseHour = 6,
            sunsetHour = 19,
            moonPhaseId = 1,
            sky = Sky.Sunset,
            isSouthernHemisphere = false
        )
    }
}
