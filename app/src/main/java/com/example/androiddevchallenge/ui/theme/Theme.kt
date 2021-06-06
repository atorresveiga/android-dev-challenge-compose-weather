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
package com.example.androiddevchallenge.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = blue900,
    secondary = deepPurple900,
    surface = blue900,
    onSurface = amber50,
    onPrimary = amber50,
    secondaryVariant = indigo900
)

private val LightColorPalette = lightColors(
    primary = blue700,
    secondary = deepPurple700,
    surface = blue700,
    onPrimary = white,
    onSurface = white,
    secondaryVariant = indigo500
)

val Colors.cloudColor: Color
    get() = if (isLight) white else amber50

val Colors.sunColor: Color
    get() = if (isLight) yellow500 else yellow200

val Colors.moonColor: Color
    get() = if (isLight) white else amber50

@Suppress("UNUSED")
val Colors.stormCloudColor: Color
    get() = gray

@Suppress("UNUSED")
val Colors.lightningColor: Color
    get() = yellow500

@Suppress("UNUSED")
val Colors.overlay: Color
    get() = alphaBlack

@Composable
fun MyTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
