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
package com.example.androiddevchallenge.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import com.example.androiddevchallenge.ui.DataFormatter
import com.example.androiddevchallenge.ui.LocalDataFormatter
import com.example.androiddevchallenge.ui.NavGraph
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.google.accompanist.insets.ProvideWindowInsets
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MyTheme {
                ProvideWindowInsets {
                    CompositionLocalProvider(LocalDataFormatter provides DataFormatter()) {
                        BlueCloudApp()
                    }
                }
            }
        }
    }
}

@Composable
fun BlueCloudApp() {
    Surface {
        NavGraph()
    }
}
