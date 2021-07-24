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
package com.atorresveiga.bluecloud.data

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.atorresveiga.bluecloud.model.EMPTY_TIME
import com.atorresveiga.bluecloud.model.Location
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("forecast")

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context) {

    private val dataStore = appContext.dataStore

    private val mutableName = stringPreferencesKey("name")
    private val mutableTimezoneId = stringPreferencesKey("timezone_id")
    private val mutableLatitude = doublePreferencesKey("latitude")
    private val mutableLongitude = doublePreferencesKey("longitude")
    private val mutableLastUpdated = longPreferencesKey("last_updated")

    private val mutableClouds = intPreferencesKey("clouds")
    private val mutableStormClouds = intPreferencesKey("storm_clouds")
    private val mutableHourlyPrecipitation = intPreferencesKey("hourly_precipitation")
    private val mutableDailyPrecipitation = intPreferencesKey("daily_precipitation")
    private val mutableHourSystem = intPreferencesKey("hour_system")
    private val mutableTemperatureSystem = intPreferencesKey("temperature_system")
    private val mutableWindSpeedSystem = intPreferencesKey("wind_speed_system")
    private val mutableDefaultDisplayView = intPreferencesKey("default_view")
    private val mutableDataSource = intPreferencesKey("data_source")

    suspend fun setCurrentLocation(location: Location) {
        dataStore.edit { preferences ->
            preferences[mutableName] = location.name
            preferences[mutableLatitude] = location.latitude
            preferences[mutableLongitude] = location.longitude
            preferences[mutableTimezoneId] = location.timezoneId
            preferences[mutableLastUpdated] = location.lastUpdated
        }
    }

    val currentLocation: Flow<Location?> = dataStore.data.map { preferences ->
        val timezoneValue = preferences[mutableName] ?: return@map null
        val latitudeValue = preferences[mutableLatitude] ?: return@map null
        val longitudeValue = preferences[mutableLongitude] ?: return@map null
        val timeZoneIdValue = preferences[mutableTimezoneId] ?: return@map null
        val lastUpdatedValue = preferences[mutableLastUpdated] ?: EMPTY_TIME
        Location(
            name = timezoneValue,
            latitude = latitudeValue,
            longitude = longitudeValue,
            timezoneId = timeZoneIdValue,
            lastUpdated = lastUpdatedValue
        )
    }

    val settings: Flow<Settings> = dataStore.data.map { preferences ->
        val default = Settings()
        val clouds = preferences[mutableClouds] ?: return@map default
        val stormClouds = preferences[mutableStormClouds] ?: return@map default
        val hourlyPrecipitation = preferences[mutableHourlyPrecipitation] ?: return@map default
        val dailyPrecipitation = preferences[mutableDailyPrecipitation] ?: return@map default
        val hourSystem = preferences[mutableHourSystem] ?: return@map default
        val temperatureSystem = preferences[mutableTemperatureSystem] ?: return@map default
        val windSpeedSystem = preferences[mutableWindSpeedSystem] ?: return@map default
        val defaultDisplayView = preferences[mutableDefaultDisplayView] ?: return@map default
        val dataSource = preferences[mutableDataSource] ?: return@map default

        Settings(
            clouds = clouds,
            stormClouds = stormClouds,
            hourlyPrecipitation = hourlyPrecipitation,
            dailyPrecipitation = dailyPrecipitation,
            hourSystem = hourSystem,
            temperatureSystem = temperatureSystem,
            windSpeedSystem = windSpeedSystem,
            defaultDisplayView = defaultDisplayView,
            dataSource = dataSource
        )
    }

    suspend fun updateSettings(settings: Settings) {
        dataStore.edit { preferences ->
            preferences[mutableClouds] = settings.clouds
            preferences[mutableStormClouds] = settings.stormClouds
            preferences[mutableHourlyPrecipitation] = settings.hourlyPrecipitation
            preferences[mutableDailyPrecipitation] = settings.dailyPrecipitation
            preferences[mutableHourSystem] = settings.hourSystem
            preferences[mutableTemperatureSystem] = settings.temperatureSystem
            preferences[mutableWindSpeedSystem] = settings.windSpeedSystem
            preferences[mutableDefaultDisplayView] = settings.defaultDisplayView
            preferences[mutableDataSource] = settings.dataSource
        }
    }
}
