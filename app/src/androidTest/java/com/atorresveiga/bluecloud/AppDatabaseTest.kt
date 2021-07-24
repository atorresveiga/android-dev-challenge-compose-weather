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
package com.atorresveiga.bluecloud

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.atorresveiga.bluecloud.data.AppDatabase
import com.atorresveiga.bluecloud.data.DayForecastEntity
import com.atorresveiga.bluecloud.data.ForecastDAO
import com.atorresveiga.bluecloud.data.HourForecastEntity
import com.atorresveiga.bluecloud.data.LocationEntity
import com.atorresveiga.bluecloud.model.SECONDS_IN_AN_HOUR
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
    private lateinit var hourlyForecastEntities: List<HourForecastEntity>
    private lateinit var dailyForecastEntities: List<DayForecastEntity>

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        forecastDAO = db.forecastDAO()

        hourlyForecastEntities = List(size = 5) { index ->
            HourForecastEntity(
                datetime = 1616407200L + SECONDS_IN_AN_HOUR * index,
                temperature = 20f,
                feelsLike = 20f,
                pressure = 10f,
                humidity = 90f,
                uvi = 1f,
                clouds = 10f,
                visibility = 1000,
                windSpeed = 6f,
                windDegrees = 45f,
                weatherId = 0,
                pop = 0f,
                precipitation = 0f,
                latitude = 0.0,
                longitude = 0.0,
                dataSource = 0
            )
        }

        dailyForecastEntities = List(size = 5) { index ->
            val datetime = 1616407200L + 86400 * index
            val sunrise = datetime + SECONDS_IN_AN_HOUR * 6
            val sunset = sunrise + SECONDS_IN_AN_HOUR * 12
            DayForecastEntity(
                datetime = datetime,
                minTemperature = 10f,
                maxTemperature = 25f,
                pressure = 10f,
                humidity = 90f,
                uvi = 1f,
                clouds = 10f,
                windSpeed = 6f,
                windDegrees = 45f,
                weatherId = 0,
                precipitation = 0f,
                moonPhaseId = 1,
                latitude = 0.0,
                longitude = 0.0,
                dataSource = 0,
                sunrise = sunrise,
                sunset = sunset
            )
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun getHourlyForecastFromLocation() = runBlocking {
        // When hourlyForecastEntities are saved
        forecastDAO.saveHourlyForecast(hourlyForecastEntities)

        // Then hourlyForecastEntities are indexed by location and data source
        val wrongLocation = forecastDAO.getHourlyForecastFrom(100.0, 100.0, 1616407200, 0).first()
        val wrongDataSource = forecastDAO.getHourlyForecastFrom(0.0, 0.0, 1616407200, 1).first()
        val right = forecastDAO.getHourlyForecastFrom(0.0, 0.0, 1616407200, 0).first()
        assertThat(wrongLocation).isEmpty()
        assertThat(wrongDataSource).isEmpty()
        assertThat(right).isEqualTo(hourlyForecastEntities)
    }

    @Test
    @Throws(Exception::class)
    fun getDailyForecastFromLocation() = runBlocking {
        // When dailyForecastEntities are saved
        forecastDAO.saveDailyForecast(dailyForecastEntities)

        // Then dailyForecastEntities are indexed by location and data source
        val wrongLocation = forecastDAO.getDailyForecastFrom(100.0, 100.0, 1616407200, 0).first()
        val wrongDataSource = forecastDAO.getDailyForecastFrom(100.0, 100.0, 1616407200, 1).first()
        val right = forecastDAO.getDailyForecastFrom(0.0, 0.0, 1616407200, 0).first()
        assertThat(wrongLocation).isEmpty()
        assertThat(wrongDataSource).isEmpty()
        assertThat(right).isEqualTo(dailyForecastEntities)
    }

    @Test
    @Throws(Exception::class)
    fun clearHourlyForecastOlderThanTest() = runBlocking {

        val index = 3
        val hour = hourlyForecastEntities[index]
        forecastDAO.saveHourlyForecast(hourlyForecastEntities)

        // When clearOlderThan is called
        forecastDAO.clearOlderThan(hour.datetime)
        val currentHourlyForecast =
            forecastDAO.getHourlyForecastFrom(
                hour.latitude,
                hour.longitude,
                1616407200,
                hour.dataSource
            )
                .first()

        // Then all older hourlyForecastEntities are dropped
        assertThat(currentHourlyForecast).isEqualTo(
            hourlyForecastEntities.subList(
                index - 1,
                hourlyForecastEntities.size
            )
        )
    }

    @Test
    @Throws(Exception::class)
    fun clearDailyForecastOlderThanTest() = runBlocking {

        val index = 3
        val day = dailyForecastEntities[index]
        forecastDAO.saveDailyForecast(dailyForecastEntities)

        // When clearOlderThan is called
        forecastDAO.clearOlderThan(day.datetime)

        val currentDailyForecast =
            forecastDAO.getDailyForecastFrom(
                day.latitude,
                day.longitude,
                1616407200,
                day.dataSource
            ).first()

        // Then all older dailyForecastEntities are dropped
        assertThat(currentDailyForecast).isEqualTo(
            dailyForecastEntities.subList(
                index - 1,
                dailyForecastEntities.size
            )
        )
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
                    timezoneId = "$i timezone",
                    datetime = datetime + 100 * i,
                    lastUpdated = datetime
                )
            )
        }

        // When getLocations is called
        val selectedLocations = forecastDAO.getLocations().first()

        // Then only the last 5 selected location are returned
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
                timezoneId = "$i timezone",
                datetime = datetime + 100 * i,
                lastUpdated = datetime
            )
            forecastDAO.saveLocation(entity)
            locations.add(entity)
        }

        // When getLocations is called
        forecastDAO.clearOlderThan(datetime)
        val selectedLocations = forecastDAO.getLocations().first()

        // Then only the last 5 selected location are returned
        assertThat(selectedLocations).containsNoneIn(locations.take(3))
    }
}
