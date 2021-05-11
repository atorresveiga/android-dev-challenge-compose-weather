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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddevchallenge.domain.GetCurrentLocationUseCase
import com.example.androiddevchallenge.domain.GetForecastUseCase
import com.example.androiddevchallenge.domain.RefreshDataUseCase
import com.example.androiddevchallenge.model.Forecast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getForecastUseCase: GetForecastUseCase,
    private val refreshDataUseCase: RefreshDataUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<ForecastState> = MutableStateFlow(CheckCurrentLocation)
    val uiState: StateFlow<ForecastState> = _uiState

    init {
        viewModelScope.launch {
            getCurrentLocationUseCase.execute()
                .collect { location ->
                    if (location != null) {
                        getForecast()
                    } else {
                        _uiState.value = NoLocationFound
                    }
                }
        }
    }

    private fun getForecast() {
        viewModelScope.launch {
            getForecastUseCase.execute().collect { cachedForecast ->
                if (cachedForecast == null || cachedForecast.hourly.size < 35) {
                    onRefreshData()
                } else {
                    _uiState.value = DisplayForecast(cachedForecast)
                }
            }
        }
    }

    fun onRefreshData() {
        _uiState.value = LoadingForecast
        viewModelScope.launch {
            refreshDataUseCase.execute()
        }
    }
}

sealed class ForecastState
object CheckCurrentLocation : ForecastState()
object NoLocationFound : ForecastState()
object LoadingForecast : ForecastState()
data class DisplayForecast(val forecast: Forecast) : ForecastState()
