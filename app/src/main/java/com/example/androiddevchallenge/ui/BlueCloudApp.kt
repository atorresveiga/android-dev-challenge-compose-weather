package com.example.androiddevchallenge.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.androiddevchallenge.ui.access.NeedsLocationAccess
import com.example.androiddevchallenge.ui.main.State
import com.example.androiddevchallenge.ui.theme.MyTheme

// Start building your app here!
@Composable
fun BlueCloudApp(state: State, onRequestPermission: () -> Unit) {
    Surface(color = MaterialTheme.colors.background) {
        when (state) {
            State.NeedLocationAccess -> {
                NeedsLocationAccess(onRequestPermission = onRequestPermission)
            }
            State.FindingLocation -> {
                Text(text = "Yeah!! Finding Location")
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
        BlueCloudApp(State.Ready) {}
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        BlueCloudApp(State.Ready) {}
    }
}