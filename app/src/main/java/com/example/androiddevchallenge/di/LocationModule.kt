package com.example.androiddevchallenge.di

import com.example.androiddevchallenge.data.GMSLocationDataSource
import com.example.androiddevchallenge.data.LocationDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class LocationModule {
    @Binds
    abstract fun bindAnalyticsService(
        locationDataSource: GMSLocationDataSource
    ): LocationDataSource
}