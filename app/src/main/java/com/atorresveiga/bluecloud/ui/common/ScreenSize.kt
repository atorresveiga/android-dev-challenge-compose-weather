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
package com.atorresveiga.bluecloud.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

/**
 * ScreenSize util class to check if the user device has a Large or Small display.
 * Should we use booleanResource(R.dimen.is_large) approach instead?
 */
enum class ScreenSize { Small, Large }

@Composable
fun getScreenSize() =
    if (LocalConfiguration.current.smallestScreenWidthDp >= 600) ScreenSize.Large else ScreenSize.Small
