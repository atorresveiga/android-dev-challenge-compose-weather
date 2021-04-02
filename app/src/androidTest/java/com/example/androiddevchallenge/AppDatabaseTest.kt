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
import com.example.androiddevchallenge.data.MockDataUtil
import com.example.androiddevchallenge.data.toHourForecastEntity
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

        val locationForecast = MockDataUtil
            .createHourlyForecast(startEpoch = 1616407200, hours = 5)
            .map { it.toHourForecastEntity(latitude, longitude) }

        forecastDAO.saveHourlyForecast(locationForecast)

        val wrongLocation = forecastDAO.getHourlyForecastFrom(100.0, 100.0).first()
        val rightLocation = forecastDAO.getHourlyForecastFrom(0.0, 0.0).first()

        assertThat(wrongLocation).isEmpty()
        assertThat(rightLocation).isEqualTo(locationForecast)
    }

    @Test
    @Throws(Exception::class)
    fun clearOlderThanTest() = runBlocking {

        val latitude = 0.0
        val longitude = 0.0

        val oldForecast = MockDataUtil
            .createHourlyForecast(startEpoch = 1616407200, hours = 3)
            .map { it.toHourForecastEntity(latitude, longitude) }

        val newDatetime = oldForecast.last().datetime + 3600

        val newForecast = MockDataUtil
            .createHourlyForecast(startEpoch = newDatetime, hours = 3)
            .map { it.toHourForecastEntity(latitude, longitude) }

        forecastDAO.saveHourlyForecast(oldForecast + newForecast)
        forecastDAO.clearOlderThan(newDatetime)

        val currentForecast = forecastDAO.getHourlyForecastFrom(latitude, longitude).first()
        assertThat(currentForecast).isEqualTo(newForecast)
    }
}
