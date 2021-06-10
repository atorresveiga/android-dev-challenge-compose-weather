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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddevchallenge.domain.GetCurrentLocationUseCase
import com.example.androiddevchallenge.domain.GetForecastUseCase
import com.example.androiddevchallenge.domain.RefreshDataUseCase
import com.example.androiddevchallenge.model.EMPTY_TIME
import com.example.androiddevchallenge.model.Forecast
import com.example.androiddevchallenge.model.SECONDS_IN_AN_HOUR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getForecastUseCase: GetForecastUseCase,
    private val refreshDataUseCase: RefreshDataUseCase
) : ViewModel() {

    private val mutableUiState: MutableStateFlow<ForecastViewState> =
        MutableStateFlow(CheckCurrentLocation)
    val uiState: StateFlow<ForecastViewState> = mutableUiState

    private var currentForecast: Forecast? = null

    init {
        viewModelScope.launch {
            try {
                getCurrentLocationUseCase.execute()
                    .collect { location ->
                        if (location != null) {
                            getForecast()
                        } else {
                            mutableUiState.value = NoLocationFound
                        }
                    }
            } catch (e: Exception) {
                mutableUiState.value = NoLocationFound
            }
        }
    }

    private fun getForecast() {
        viewModelScope.launch {
            try {
                getForecastUseCase.execute(
                    startTime = Clock.System.now().epochSeconds
                )
                    .collect { cachedForecast ->
                        if (cachedForecast == null || cachedForecast.hourly.size < 35) {
                            onRefreshData(force = true)
                        } else {
                            mutableUiState.value =
                                DisplayForecast(isLoading = false, forecast = cachedForecast)
                            currentForecast = cachedForecast
                        }
                    }
            } catch (e: Exception) {
                mutableUiState.value = LoadingForecastError
            }
        }
    }

    fun onRefreshData(force: Boolean = false) {
        mutableUiState.value = DisplayForecast(isLoading = true, forecast = null)
        // Refresh data only if is older than 6 hours
        val lastUpdated = currentForecast?.location?.lastUpdated ?: EMPTY_TIME
        val now = Clock.System.now().epochSeconds
        if (now - lastUpdated < SECONDS_IN_AN_HOUR * 6 && !force) {
            getForecast()
        } else {
            viewModelScope.launch {
                try {
                    refreshDataUseCase.execute()
                } catch (e: Exception) {
                    mutableUiState.value = LoadingForecastError
                }
            }
        }
    }

    fun displayCurrentForecast() {
        // Display forecast for the current hour
        currentForecast?.let { forecast ->
            val currentTime = Clock.System.now().epochSeconds
            val firstForecastHour = forecast.hourly.first().datetime
            if (currentTime - firstForecastHour > SECONDS_IN_AN_HOUR) {
                getForecast()
            }
        }
    }
}

sealed class ForecastViewState
object CheckCurrentLocation : ForecastViewState()
object NoLocationFound : ForecastViewState()
object LoadingForecastError : ForecastViewState()
data class DisplayForecast(val isLoading: Boolean, val forecast: Forecast?) : ForecastViewState()
