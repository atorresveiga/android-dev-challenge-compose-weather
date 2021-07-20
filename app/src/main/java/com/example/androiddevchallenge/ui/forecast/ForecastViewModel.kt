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
import com.example.androiddevchallenge.domain.GetSettingsUseCase
import com.example.androiddevchallenge.domain.RefreshDataUseCase
import com.example.androiddevchallenge.model.EMPTY_TIME
import com.example.androiddevchallenge.model.Forecast
import com.example.androiddevchallenge.model.SECONDS_IN_AN_HOUR
import com.example.androiddevchallenge.ui.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getForecastUseCase: GetForecastUseCase,
    private val refreshDataUseCase: RefreshDataUseCase,
    private val getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {

    private val mutableUiState: MutableStateFlow<ForecastViewState> =
        MutableStateFlow(CheckCurrentLocation)
    val uiState: StateFlow<ForecastViewState> = mutableUiState
    private val mutableSettings: MutableStateFlow<Settings?> = MutableStateFlow(null)
    private val mutableForecast: MutableSharedFlow<Forecast> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private var currentForecast: Forecast? = null
    private val mutableForecastStartTime: MutableSharedFlow<Long> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        viewModelScope.launch {
            launch {
                getSettingsUseCase.execute().collect { prefSettings ->
                    mutableSettings.value = prefSettings
                }
            }
            launch {
                getCurrentLocationUseCase.execute()
                    .catch { mutableUiState.value = NoLocationFound }
                    .collect { location ->
                        if (location != null) {
                            mutableForecastStartTime.emit(Clock.System.now().epochSeconds)
                        } else {
                            mutableUiState.value = NoLocationFound
                        }
                    }
            }
            launch {
                mutableSettings.filterNotNull()
                    .combine(mutableForecast) { settings, forecast ->
                        DisplayForecast(
                            isLoading = false,
                            forecast = forecast,
                            settings = settings
                        )
                    }.collect {
                        mutableUiState.value = it
                    }
            }
            launch {
                mutableForecastStartTime.flatMapLatest { startTime ->
                    getForecastUseCase.execute(startTime)
                }.catch {
                    mutableUiState.value = LoadingForecastError
                }.collect { cachedForecast ->
                    if (cachedForecast == null || cachedForecast.hourly.size < 35) {
                        onRefreshData(force = true)
                    } else {
                        currentForecast = cachedForecast
                        mutableForecast.emit(cachedForecast)
                    }
                }
            }
        }
    }

    fun onRefreshData(force: Boolean = false) {
        mutableUiState.value = DisplayForecast(
            isLoading = true,
            forecast = null,
            settings = mutableSettings.value ?: Settings()
        )
        // Refresh data only if is older than 6 hours
        val lastUpdated = currentForecast?.location?.lastUpdated ?: EMPTY_TIME
        val now = Clock.System.now().epochSeconds
        viewModelScope.launch {
            if (now - lastUpdated < SECONDS_IN_AN_HOUR * 6 && !force) {
                mutableForecastStartTime.emit(Clock.System.now().epochSeconds)
            } else {
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
                viewModelScope.launch {
                    mutableForecastStartTime.emit(Clock.System.now().epochSeconds)
                }
            }
        }
    }
}

sealed class ForecastViewState
object CheckCurrentLocation : ForecastViewState()
object NoLocationFound : ForecastViewState()
object LoadingForecastError : ForecastViewState()
data class DisplayForecast(
    val isLoading: Boolean,
    val forecast: Forecast?,
    val settings: Settings
) : ForecastViewState()
