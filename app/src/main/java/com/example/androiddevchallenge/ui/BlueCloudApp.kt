package com.example.androiddevchallenge.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.androiddevchallenge.model.Forecast
import com.example.androiddevchallenge.ui.access.NeedsLocationAccessScreen
import com.example.androiddevchallenge.ui.forecast.EmptyForecast
import com.example.androiddevchallenge.ui.forecast.ForecastScreen
import com.example.androiddevchallenge.ui.main.State
import com.example.androiddevchallenge.ui.theme.MyTheme

// Start building your app here!
@Composable
fun BlueCloudApp(
    state: State,
    forecast: Forecast?,
    onRequestPermission: () -> Unit,
    onRefreshData: () -> Unit
) {
    Surface(color = MaterialTheme.colors.background) {
        when (state) {
            State.NeedLocationAccess -> {
                NeedsLocationAccessScreen(onRequestPermission = onRequestPermission)
            }
            State.FindingLocation -> {
                Text(text = "Yeah!! Finding Location")
            }
            State.Ready -> {
                forecast?.let {
                    ForecastScreen(it.hourly)
                } ?: EmptyForecast(onRefreshData)
            }
            else -> {
                Text(text = state.toString())
            }
        }

    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        BlueCloudApp(State.Ready, null, {  }) {}
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        BlueCloudApp(State.Ready, null, {  }) {}
    }
}