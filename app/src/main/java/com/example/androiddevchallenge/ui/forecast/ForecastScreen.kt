package com.example.androiddevchallenge.ui.forecast

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.example.androiddevchallenge.model.HourForecast

@Composable
fun ForecastScreen(hourlyForecast: List<HourForecast>) {
    Column {
        for (hour in hourlyForecast) {
            Text(hour.weather)
        }
    }
}

@Composable
fun EmptyForecast(onRefreshData: () -> Unit) {
    Button(onClick = { onRefreshData() }) {
        Text(text = "Refresh data")
    }
}