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
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.atorresveiga.bluecloud.R
import com.atorresveiga.bluecloud.ui.common.settings
import com.atorresveiga.bluecloud.ui.forecast.LocalSettings
import com.atorresveiga.bluecloud.ui.forecast.StormCloudWithLightning
import com.atorresveiga.bluecloud.ui.forecast.WeatherOffset
import com.atorresveiga.bluecloud.ui.forecast.generateRandomWeatherOffsets
import com.atorresveiga.bluecloud.ui.theme.lightningColor
import com.atorresveiga.bluecloud.ui.theme.stormCloudColor

/**
 * Clouds representation in the sky
 * @param weatherId encoded value containing if the weather has thunders. Full description in Forecast.kt.
 * @param direction in which direction the user is navigating (used in clouds visibility animation)
 * @param cloudiness cloudiness (percent)
 * @param modifier Modifier
 * @param clouds list of [WeatherOffset] representing the clouds
 * @param withStormClouds boolean to indicate if we should draw storm clouds within this group of clouds if the weather has thunders
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Clouds(
    weatherId: Int,
    direction: NavigationDirection,
    cloudiness: Float,
    modifier: Modifier = Modifier,
    clouds: List<WeatherOffset> = generateRandomWeatherOffsets(20),
    withStormClouds: Boolean = true
) {
    val hasThunders = LocalSettings.current.dataFormatter.weather.hasThunders(weatherId)
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {

        val density = LocalDensity.current
        val maxStormClouds = if (withStormClouds) LocalSettings.current.stormClouds else 0
        val total = clouds.size * cloudiness / 100

        clouds.forEachIndexed { index, cloudOffset ->

            val isReverse = index % 3 == 0

            // We show storm clouds only if withStormClouds is true
            val hasLightning = index > total - maxStormClouds && hasThunders

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

            val enterDirection = if (direction == NavigationDirection.Forward) 2 else -2
            val exitDirection = if (direction == NavigationDirection.Forward) -1 else 1

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

@Composable
fun getCloudModifiers(z: Int): Triple<Dp, Dp, Float> {
    val width: Dp
    val alpha: Float
    when (z) {
        4 -> {
            width = dimensionResource(id = R.dimen.cloud4)
            alpha = .98f
        }
        3 -> {
            width = dimensionResource(id = R.dimen.cloud3)
            alpha = .95f
        }
        2 -> {
            width = dimensionResource(id = R.dimen.cloud2)
            alpha = .55f
        }
        else -> {
            width = dimensionResource(id = R.dimen.cloud1)
            alpha = .4f
        }
    }
    return Triple(width, width * 1.1f, alpha)
}

@Preview(widthDp = 360, heightDp = 600)
@Composable
fun CloudsPreview() {
    CompositionLocalProvider(LocalSettings provides settings) {
        Clouds(weatherId = 0, cloudiness = 80f, direction = NavigationDirection.Forward)
    }
}
