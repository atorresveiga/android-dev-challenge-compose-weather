package com.example.androiddevchallenge.ui.forecast

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.model.HourForecast
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun ForecastScreen(hourlyForecast: List<HourForecast>, selectedIndex: Int = 0) {

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenWidthPx = with(density) { screenWidth.toPx() }

    // Get the selected hour offset
    val selectedOffset = -1 * selectedIndex * screenWidthPx / (hourlyForecast.size - 1)

    // Initialize offset in the selected hour offset
    var offset by remember { mutableStateOf(selectedOffset) }

    val index = (-1 * (hourlyForecast.size - 1) * offset / screenWidthPx).roundToInt()

    Box(
        Modifier
            .fillMaxSize()
            .scrollable(
                orientation = Orientation.Horizontal,
                // Scrollable state: describes how to consume
                // scrolling delta and update offset (max offset to screenWidthPx)
                state = rememberScrollableState { delta ->
                    offset = (delta / 4 + offset).coerceIn(-1 * screenWidthPx, 0f)
                    delta / 4
                }
            )
    ) {
        val selectedHour = hourlyForecast[index]
        CurrentTemperature(selectedHour, modifier = Modifier.align(Alignment.Center))
        Rain(
            selectedHour, modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 16.dp)
        )
        WindIndicator(
            selectedHour,
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 8.dp, top = 8.dp)
        )
    }
}


@Composable
fun CurrentTemperature(hourForecast: HourForecast, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = hourForecast.datetime.toHourFormat())
        Text(text = hourForecast.temperature.toTemperature(), style = MaterialTheme.typography.h1)
        Text(text = hourForecast.feelsLike.toTemperature())
    }
}

@Composable
fun Rain(hourForecast: HourForecast, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = hourForecast.pop.toString())
        Text(text = hourForecast.rain.toString())
    }
}

@Composable
fun EmptyForecast(onRefreshData: () -> Unit) {
    Button(onClick = { onRefreshData() }) {
        Text(text = "Refresh data")
    }
}

fun Float.toTemperature(): String {
    return this.roundToInt().toString().plus("°")
}

fun Float.getCircularOffset(
    center: Offset = Offset(0f, 0f),
    radius: Float
): Offset {
    val x = center.x + radius * cos(this * PI / 180).toFloat()
    val y = center.y + radius * sin(this * PI / 180).toFloat()
    return Offset(x, y)
}


