package com.example.androiddevchallenge.ui.access

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NeedsLocationAccessScreen(onRequestPermission: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()){
        Button(onClick = { onRequestPermission() }) {
            Text("I understand")
        }
    }
}