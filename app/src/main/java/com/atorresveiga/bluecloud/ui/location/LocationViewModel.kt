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
package com.atorresveiga.bluecloud.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atorresveiga.bluecloud.domain.FindLocationTimezone
import com.atorresveiga.bluecloud.domain.FindUserLocationUseCase
import com.atorresveiga.bluecloud.domain.GetLastSelectedLocationsUseCase
import com.atorresveiga.bluecloud.domain.SearchLocationUseCase
import com.atorresveiga.bluecloud.domain.UpdateCurrentLocationUseCase
import com.atorresveiga.bluecloud.model.Location
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
    private val searchLocationUseCase: SearchLocationUseCase,
    private val findLocationTimezone: FindLocationTimezone
) : ViewModel() {

    private var lastSelectedLocations: List<Location> = emptyList()
    private val mutableQuery = MutableStateFlow("")
    private var foundLocations: List<Location> = emptyList()
    private val mutableUiState: MutableStateFlow<LocationViewState> = MutableStateFlow(
        SelectLocation(
            lastSelectedLocations = lastSelectedLocations,
            query = mutableQuery.value,
            isSearching = false,
            foundLocations = foundLocations,
            error = null
        )
    )

    val uiState: StateFlow<LocationViewState> = mutableUiState

    init {
        mutableQuery.mapLatest { searchQuery ->
            if (searchQuery.length < 3) {
                foundLocations = emptyList()
                (mutableUiState.value as? SelectLocation)?.let {
                    mutableUiState.value = it.copy(
                        isSearching = false,
                        query = searchQuery,
                        foundLocations = foundLocations,
                        error = null
                    )
                }
            } else {
                (mutableUiState.value as? SelectLocation)?.let {
                    mutableUiState.value = it.copy(
                        isSearching = true,
                        query = searchQuery
                    )
                }
                // Delay the search until the user stops typing for 2 second
                delay(2000)
                foundLocations = try {
                    searchLocationUseCase.execute(searchQuery)
                } catch (exception: Exception) {
                    // We don't care about exception, we must recover with a new query
                    emptyList()
                }
                (mutableUiState.value as? SelectLocation)?.let {
                    mutableUiState.value =
                        it.copy(
                            foundLocations = foundLocations,
                            isSearching = false,
                            error = null
                        )
                }
            }
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            getLastSelectedLocationsUseCase.execute().collect { cachedSelectedLocations ->
                lastSelectedLocations = cachedSelectedLocations
                (mutableUiState.value as? SelectLocation)?.let {
                    mutableUiState.value = it.copy(lastSelectedLocations = lastSelectedLocations)
                }
            }
        }
    }

    private fun showError(e: Exception) {
        mutableUiState.value = SelectLocation(
            lastSelectedLocations = lastSelectedLocations,
            query = mutableQuery.value,
            isSearching = false,
            foundLocations = foundLocations,
            error = e
        )
    }

    fun findUserLocation() {
        mutableUiState.value = FindingUserLocation
        viewModelScope.launch {
            try {
                findUserLocationUseCase.execute()
                mutableUiState.value = LocationSelected
            } catch (e: Exception) {
                showError(e)
            }
        }
    }

    fun updateSearchQuery(searchQuery: String) {
        mutableQuery.value = searchQuery
    }

    fun selectLocation(location: Location) {
        viewModelScope.launch {
            try {
                val selectedLocation = if (location.timezoneId.isEmpty()) {
                    mutableUiState.value = FindingLocationTimeZone
                    findLocationTimezone.execute(location)
                } else {
                    location
                }
                updateCurrentLocationUseCase.execute(selectedLocation)
                mutableUiState.value = LocationSelected
            } catch (e: Exception) {
                showError(e)
            }
        }
    }
}

sealed class LocationViewState
object FindingUserLocation : LocationViewState()
object FindingLocationTimeZone : LocationViewState()
object LocationSelected : LocationViewState()
data class SelectLocation(
    val lastSelectedLocations: List<Location>,
    val query: String,
    val isSearching: Boolean,
    val foundLocations: List<Location>,
    // This could be wrapped in an "Event" to prevent re-launch the exception on screen rotation
    val error: Exception?
) : LocationViewState()
