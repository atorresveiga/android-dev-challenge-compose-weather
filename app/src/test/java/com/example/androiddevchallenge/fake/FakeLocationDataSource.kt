package com.example.androiddevchallenge.fake

import com.example.androiddevchallenge.data.LocationDataSource
import com.example.androiddevchallenge.model.Location

class FakeLocationDataSource(): LocationDataSource {
    var location: Location? = null
    var fail: Exception? = null
    override suspend fun getLocation(): Location? {
        fail?.let { throw it }
        return location
    }
}