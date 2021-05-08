package com.example.androiddevchallenge.di

import com.example.androiddevchallenge.data.LocationDataSource
import com.example.androiddevchallenge.data.MockLocationDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class LocationModule {
    @Binds
    abstract fun bindLocationService(
        locationDataSource: MockLocationDataSource
    ): LocationDataSource
}

