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
package com.example.androiddevchallenge.data

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.androiddevchallenge.model.Location
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("forecast")

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context) {

    private val _dataStore = appContext.dataStore

    private val _timezone = stringPreferencesKey("timezone")
    private val _latitude = doublePreferencesKey("latitude")
    private val _longitude = doublePreferencesKey("longitude")
    private val _lastUpdated = longPreferencesKey("last_updated")

    suspend fun setCurrentLocation(location: Location) {
        _dataStore.edit { preferences ->
            preferences[_timezone] = location.timezone
            preferences[_latitude] = location.latitude
            preferences[_longitude] = location.longitude
        }
    }

    suspend fun setLastUpdated(datetime: Long) {
        _dataStore.edit { preferences ->
            preferences[_lastUpdated] = datetime
        }
    }

    val lastUpdated =
        _dataStore.data.map { preferences -> preferences[_lastUpdated] ?: return@map null }

    val currentLocation: Flow<Location?> = _dataStore.data.map { preferences ->
        val timezoneValue = preferences[_timezone] ?: return@map null
        val latitudeValue = preferences[_latitude] ?: return@map null
        val longitudeValue = preferences[_longitude] ?: return@map null
        Location(
            timezone = timezoneValue,
            latitude = latitudeValue,
            longitude = longitudeValue
        )
    }
}
