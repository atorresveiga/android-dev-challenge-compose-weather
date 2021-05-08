package com.example.androiddevchallenge.data

import com.example.androiddevchallenge.model.Location
import javax.inject.Inject
import kotlinx.coroutines.delay

class MockLocationDataSource @Inject constructor() : LocationDataSource {
    override suspend fun getLocation(): Location? {
        delay(3000)
        return MockDataGenerator.getRandomLocation()
    }
}