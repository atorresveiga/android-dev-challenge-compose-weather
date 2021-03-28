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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddevchallenge.data.Result
import com.example.androiddevchallenge.domain.FindLocationUseCase
import com.example.androiddevchallenge.domain.GetCurrentLocationUseCase
import com.example.androiddevchallenge.domain.GetForecastUseCase
import com.example.androiddevchallenge.domain.RefreshDataUseCase
import com.example.androiddevchallenge.model.Forecast
import com.example.androiddevchallenge.ui.DataFormatter
import com.example.androiddevchallenge.ui.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val findLocationUseCase: FindLocationUseCase,
    private val refreshDataUseCase: RefreshDataUseCase,
    private val getForecastUseCase: GetForecastUseCase
) : ViewModel() {

    private val _requestLocationAccess = MutableLiveData<Event<Unit>>()
    val requestLocationAccess: LiveData<Event<Unit>>
        get() = _requestLocationAccess

    var isLocationAccessGranted = false

    var forecast by mutableStateOf<Forecast?>(null)

    var dataFormatter by mutableStateOf(DataFormatter())

    var state by mutableStateOf(State.Initializing)
        private set

    init {
        viewModelScope.launch {
            launch {
                getCurrentLocationUseCase.invoke(Unit).collect { result ->
                    val needsUserLocation = result is Result.Success && result.data == null
                    when {
                        needsUserLocation && isLocationAccessGranted -> {
                            findUserLocation()
                        }
                        needsUserLocation && !isLocationAccessGranted -> {
                            state = State.NeedLocationAccess
                        }
                        result is Result.Success && result.data != null -> {
                            state = State.Ready
                            getForecast()
                        }
                        result is Result.Error -> {
                            // Show Error
                        }
                    }
                }
            }
        }
    }

    fun requestLocationAccess() {
        _requestLocationAccess.value = Event(Unit)
    }

    fun onLocationAccessGranted() {
        isLocationAccessGranted = true
        findUserLocation()
    }

    private fun getForecast() {
        viewModelScope.launch {
            getForecastUseCase.invoke(Unit).collect { result ->
                if (result is Result.Success) {
                    result.data?.let { data ->
                        if (data.hourly.size > 35) {
                            forecast = data
                        } else {
                            onRefreshData()
                        }
                    } ?: onRefreshData()
                }
            }
        }
    }

    fun onRefreshData() {
        viewModelScope.launch {
            refreshDataUseCase.invoke(Unit)
        }
    }

    private fun findUserLocation() {
        state = State.FindingLocation
        viewModelScope.launch {
            findLocationUseCase.invoke(Unit)
        }
    }
}

enum class State {
    Initializing, NeedLocationAccess, FindingLocation, Syncing, Ready
}
