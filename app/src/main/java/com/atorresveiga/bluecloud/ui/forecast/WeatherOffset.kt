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

import kotlin.random.Random

/**
 * A data class that holds the weather offset data.
 * @param x coordinate x where this (rain,cloud,etc) start (percent)
 * @param y coordinate y where this (rain,cloud,etc) should start (percent)
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
