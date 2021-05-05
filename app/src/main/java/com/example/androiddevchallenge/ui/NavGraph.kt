package com.example.androiddevchallenge.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androiddevchallenge.ui.forecast.ForecastScreen
import com.example.androiddevchallenge.ui.forecast.ForecastViewModel
import com.example.androiddevchallenge.ui.location.LocationScreen
import com.example.androiddevchallenge.ui.location.LocationViewModel

/**
 * Destinations used in the BlueCloudApp.
 */
object BlueCloudDestinations {
    const val LOCATION_ROUTE = "location"
    const val FORECAST_ROUTE = "forecast"
}

@Composable
fun NavGraph(startDestination: String = BlueCloudDestinations.FORECAST_ROUTE) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(BlueCloudDestinations.LOCATION_ROUTE) {
            val locationViewModel = hiltNavGraphViewModel<LocationViewModel>()
            LocationScreen(
                viewModel = locationViewModel,
                navController = navController
            )
        }
        composable(BlueCloudDestinations.FORECAST_ROUTE) {
            val forecastViewModel = hiltNavGraphViewModel<ForecastViewModel>()
                ForecastScreen(
                    viewModel = forecastViewModel,
                    navController = navController
                )
        }
    }
}