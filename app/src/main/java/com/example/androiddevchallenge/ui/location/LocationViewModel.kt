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
package com.example.androiddevchallenge.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.domain.FindUserLocationUseCase
import com.example.androiddevchallenge.domain.GetLastSelectedLocationsUseCase
import com.example.androiddevchallenge.domain.LocationNotFoundException
import com.example.androiddevchallenge.domain.SearchLocationUseCase
import com.example.androiddevchallenge.domain.UpdateCurrentLocationUseCase
import com.example.androiddevchallenge.model.Location
import com.example.androiddevchallenge.ui.Event
import com.example.androiddevchallenge.ui.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LocationViewModel @Inject constructor(
    private val findUserLocationUseCase: FindUserLocationUseCase,
    private val updateCurrentLocationUseCase: UpdateCurrentLocationUseCase,
    private val getLastSelectedLocationsUseCase: GetLastSelectedLocationsUseCase,
    private val searchLocationUseCase: SearchLocationUseCase
) : ViewModel() {

    private val mutableUiState: MutableStateFlow<LocationState> =
        MutableStateFlow(LocationState.SelectLocation)
    val uiState: StateFlow<LocationState> = mutableUiState

    private val mutableError: MutableStateFlow<Event<Int>?> = MutableStateFlow(null)
    val error: StateFlow<Event<Int>?> = mutableError

    private val mutableLastSelectedLocations: MutableStateFlow<List<Location>> =
        MutableStateFlow(emptyList())
    val lastSelectedLocations: StateFlow<List<Location>> = mutableLastSelectedLocations

    private val mutableQuery = MutableStateFlow("")
    val query: StateFlow<String> = mutableQuery

    val foundLocations: MutableStateFlow<Result<List<Location>>> =
        MutableStateFlow(Result.Success(emptyList()))

    init {
        query.mapLatest { searchQuery ->
            if (searchQuery.length < 3) {
                foundLocations.value = Result.Success(emptyList())
            } else {
                foundLocations.value = Result.Loading
                // Delay the search until the user stops typing for 2 second
                delay(2000)
                try {
                    foundLocations.value =
                        Result.Success(searchLocationUseCase.execute(searchQuery))
                } catch (exception: Exception) {
                    // We don't care about exception, we must recover with a new query
                    foundLocations.value = Result.Success(emptyList())
                }
            }
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            getLastSelectedLocationsUseCase.execute().collect { selectedLocations ->
                mutableLastSelectedLocations.value = selectedLocations
            }
        }
    }

    fun findUserLocation() {
        mutableUiState.value = LocationState.FindingUserLocation
        viewModelScope.launch {
            try {
                findUserLocationUseCase.execute()
                mutableUiState.value = LocationState.LocationSelected
            } catch (e: LocationNotFoundException) {
                mutableUiState.value = LocationState.SelectLocation
                mutableError.value = Event(R.string.location_not_found)
            }
        }
    }

    fun updateSearchQuery(searchQuery: String) {
        mutableQuery.value = searchQuery
    }

    fun selectLocation(location: Location) {
        viewModelScope.launch {
            updateCurrentLocationUseCase.execute(location)
            mutableUiState.value = LocationState.LocationSelected
        }
    }
}

enum class LocationState { SelectLocation, FindingUserLocation, LocationSelected }
