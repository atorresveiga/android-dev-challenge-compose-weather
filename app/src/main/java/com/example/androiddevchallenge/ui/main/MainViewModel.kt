package com.example.androiddevchallenge.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddevchallenge.data.Result
import com.example.androiddevchallenge.domain.GetLocationUseCase
import com.example.androiddevchallenge.model.Location
import com.example.androiddevchallenge.ui.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getLocationUseCase: GetLocationUseCase
) : ViewModel() {

    private val _requestLocationAccess = MutableLiveData<Event<Unit>>()
    val requestLocationAccess: LiveData<Event<Unit>>
        get() = _requestLocationAccess

    var isLocationAccessGranted = false

    var currentLocation by mutableStateOf<Location?>(null)

    var state by mutableStateOf(State.Initializing)
        private set

    init {
        viewModelScope.launch {
            getLocationUseCase.invoke(Unit).collect { result ->
                val needsUserLocation = result is Result.Success && result.data == null
                when {
                    needsUserLocation && isLocationAccessGranted -> {
                        state = State.FindingLocation
                    }
                    needsUserLocation && !isLocationAccessGranted -> {
                        state = State.NeedLocationAccess
                    }
                    result is Result.Success && result.data != null -> {
                        state = State.Ready
                    }
                    result is Result.Error -> {
                        // Show Error
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
        state = State.FindingLocation
    }

}

enum class State {
    Initializing, NeedLocationAccess, FindingLocation, Syncing, Ready
}