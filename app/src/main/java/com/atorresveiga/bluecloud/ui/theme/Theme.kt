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
package com.atorresveiga.bluecloud.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = blue900,
    onPrimary = amber50,
    secondary = deepPurple900,
    secondaryVariant = indigo900,
    surface = blue900,
    onSurface = amber50,
    onBackground = amber50
)

private val LightColorPalette = lightColors(
    primary = blue700,
    onPrimary = white,
    secondary = deepPurple700,
    secondaryVariant = indigo500,
    surface = blue700,
    onSurface = white,
    onBackground = blue700
)

val Colors.cloudColor: Color
    get() = if (isLight) white else amber50

val Colors.sunColor: Color
    get() = if (isLight) yellow500 else yellow200

val Colors.moonColor: Color
    get() = if (isLight) white else amber50

val Colors.cardsBackground: Color
    get() = if (isLight) blue700Light else blue900Dark

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
