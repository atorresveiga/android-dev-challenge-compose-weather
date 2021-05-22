/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.androiddevchallenge.data.AppDatabase
import com.example.androiddevchallenge.data.ForecastDAO
import com.example.androiddevchallenge.data.LocationEntity
import com.example.androiddevchallenge.data.MockDataGenerator
import com.example.androiddevchallenge.data.toDayForecast
import com.example.androiddevchallenge.data.toDayForecastEntity
import com.example.androiddevchallenge.data.toHourForecast
import com.example.androiddevchallenge.data.toHourForecastEntity
import com.example.androiddevchallenge.model.Location
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    private lateinit var forecastDAO: ForecastDAO
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        forecastDAO = db.forecastDAO()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun getHourlyForecastFromLocation() = runBlocking {

        val latitude = 0.0
        val longitude = 0.0

        val locationForecast = MockDataGenerator
            .createHourlyForecast(startEpoch = 1616407200, hours = 5, weatherId = 0)
            .map { it.toHourForecastEntity(latitude, longitude) }

        forecastDAO.saveHourlyForecast(locationForecast)

        val wrongLocation = forecastDAO.getHourlyForecastFrom(100.0, 100.0).first()
        val rightLocation = forecastDAO.getHourlyForecastFrom(0.0, 0.0).first()

        assertThat(wrongLocation).isEmpty()
        assertThat(rightLocation).isEqualTo(locationForecast)
    }

    @Test
    @Throws(Exception::class)
    fun getDailyForecastFromLocation() = runBlocking {

        val latitude = 0.0
        val longitude = 0.0

        val locationForecast = MockDataGenerator
            .createDailyForecast(startEpoch = 1616407200, days = 5)
            .map { it.toDayForecastEntity(latitude, longitude) }

        forecastDAO.saveDailyForecast(locationForecast)

        val wrongLocation = forecastDAO.getDailyForecastFrom(100.0, 100.0).first()
        val rightLocation = forecastDAO.getDailyForecastFrom(0.0, 0.0).first()

        assertThat(wrongLocation).isEmpty()
        assertThat(rightLocation).isEqualTo(locationForecast)
    }

    @Test
    @Throws(Exception::class)
    fun clearHourlyForecastOlderThanTest() = runBlocking {

        val location = Location("test_timezone", 0.0, 0.0)

        val oldForecast = MockDataGenerator
            .createForecast(startEpoch = 1616407200, days = 5, hours = 5, location = location)

        val newDatetime = oldForecast.hourly.last().datetime + 3600

        val newForecast = MockDataGenerator
            .createForecast(startEpoch = newDatetime, days = 5, hours = 5, location = location)

        val hourlyEntities = (oldForecast.hourly + newForecast.hourly)
            .map {
                it.toHourForecastEntity(
                    location.latitude,
                    location.longitude
                )
            }

        forecastDAO.saveHourlyForecast(hourlyEntities)

        forecastDAO.clearOlderThan(newDatetime)

        val currentHourlyForecast =
            forecastDAO.getHourlyForecastFrom(location.latitude, location.longitude).first()

        assertThat(currentHourlyForecast.map { it.toHourForecast() }).isEqualTo(newForecast.hourly)
    }

    @Test
    @Throws(Exception::class)
    fun clearDailyForecastOlderThanTest() = runBlocking {

        val location = Location("test_timezone", 0.0, 0.0)

        val oldForecast = MockDataGenerator
            .createForecast(startEpoch = 1616407200, days = 5, hours = 5, location = location)

        val newDatetime = 1616407200 + 86400 * 8L

        val newForecast = MockDataGenerator
            .createForecast(startEpoch = newDatetime, days = 5, hours = 5, location = location)

        val dailyEntities = (oldForecast.daily + newForecast.daily)
            .map {
                it.toDayForecastEntity(
                    location.latitude,
                    location.longitude
                )
            }

        forecastDAO.saveDailyForecast(dailyEntities)
        forecastDAO.clearOlderThan(newDatetime)

        val currentDailyForecast =
            forecastDAO.getDailyForecastFrom(location.latitude, location.longitude).first()

        assertThat(currentDailyForecast.map { it.toDayForecast() }).isEqualTo(newForecast.daily)
    }

    @Test
    @Throws(Exception::class)
    fun getSelectedLocations() = runBlocking {
        val datetime = 1616407200L
        for (i in 0..7) {
            forecastDAO.saveLocation(
                LocationEntity(
                    name = "$i timezone",
                    latitude = i.toDouble(),
                    longitude = i.toDouble(),
                    datetime = datetime + 100 * i
                )
            )
        }
        val selectedLocations = forecastDAO.getLocations().first()
        assertThat(selectedLocations.size).isEqualTo(5)
    }

    @Test
    @Throws(Exception::class)
    fun clearSelectedLocations() = runBlocking {
        val datetime = 1616407200L
        val locations = mutableListOf<LocationEntity>()
        for (i in 0..7) {
            val entity = LocationEntity(
                name = "$i timezone",
                latitude = i.toDouble(),
                longitude = i.toDouble(),
                datetime = datetime + 100 * i
            )
            forecastDAO.saveLocation(entity)
            locations.add(entity)
        }

        forecastDAO.clearOlderThan(datetime)
        val selectedLocations = forecastDAO.getLocations().first()
        assertThat(selectedLocations).containsNoneIn(locations.take(3))
    }
}
