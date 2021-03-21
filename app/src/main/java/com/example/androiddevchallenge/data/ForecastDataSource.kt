package com.example.androiddevchallenge.data

import com.example.androiddevchallenge.model.Forecast
import kotlinx.coroutines.flow.Flow

interface ForecastDataSource {
    fun getForecast(latitude: Double, longitude: Double): Flow<Result<Forecast>>
}

