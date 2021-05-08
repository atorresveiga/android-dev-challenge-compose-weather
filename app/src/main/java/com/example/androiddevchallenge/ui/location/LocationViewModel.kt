package com.example.androiddevchallenge.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.domain.FindLocationUseCase
import com.example.androiddevchallenge.domain.LocationNotFoundException
import com.example.androiddevchallenge.model.Location
import com.example.androiddevchallenge.ui.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val findLocationUseCase: FindLocationUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationState.SelectLocation)
    val uiState: StateFlow<LocationState> = _uiState

    private val _error: MutableStateFlow<Event<Int?>> = MutableStateFlow(Event(null))
    val error: StateFlow<Event<Int?>> = _error

    val lastSelectedLocations = listOf(
        Location(
            timezone = "Americas/Argentina/Buenos_Aires",
            latitude = -34.5477769,
            longitude = -58.4515826,
        ),
        Location(
            timezone = "America/Chicago",
            latitude = -22.955536,
            longitude = -43.1847027
        ),
        Location(
            timezone = "Americas/Cuba/La_Habana",
            latitude = 23.1206009,
            longitude = -82.4065344
        )
    )

    fun findUserLocation() {
        _uiState.value = LocationState.FindingLocation
        viewModelScope.launch {
            try {
                findLocationUseCase.execute()
                _uiState.value = LocationState.LocationSelected
            } catch (e: LocationNotFoundException) {
                _uiState.value = LocationState.SelectLocation
                _error.value = Event(R.string.location_not_found )
            }
        }
    }


    fun selectLocation(location: Location) {
        //TODO update current location
    }

}

enum class LocationState { SelectLocation, FindingLocation, LocationSelected }