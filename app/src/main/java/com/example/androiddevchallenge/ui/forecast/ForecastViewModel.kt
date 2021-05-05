package com.example.androiddevchallenge.ui.forecast

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddevchallenge.domain.GetCurrentLocationUseCase
import com.example.androiddevchallenge.domain.GetForecastUseCase
import com.example.androiddevchallenge.domain.RefreshDataUseCase
import com.example.androiddevchallenge.model.Forecast
import com.example.androiddevchallenge.ui.DataFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getForecastUseCase: GetForecastUseCase,
    private val refreshDataUseCase: RefreshDataUseCase
) : ViewModel() {

    private val _uiState:MutableStateFlow<ForecastState> = MutableStateFlow(CheckCurrentLocation)
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
