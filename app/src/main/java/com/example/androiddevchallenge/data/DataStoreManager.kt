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

    private val dataStore = appContext.dataStore

    private val timezone = stringPreferencesKey("timezone")
    private val latitude = doublePreferencesKey("latitude")
    private val longitude = doublePreferencesKey("longitude")
    private val lastUpdated = longPreferencesKey("last_updated")

    suspend fun saveCurrentLocation(location: Location) {
        dataStore.edit { forecast ->
            forecast[timezone] = location.timezone
            forecast[latitude] = location.latitude
            forecast[longitude] = location.longitude
            forecast[lastUpdated] = location.lastUpdated
        }
    }

    val currentLocation: Flow<Location?> = dataStore.data.map { preferences ->
        val timezoneValue = preferences[timezone] ?: return@map null
        val latitudeValue = preferences[latitude] ?: return@map null
        val longitudeValue = preferences[longitude] ?: return@map null
        val lastUpdatedValue = preferences[lastUpdated] ?: return@map null
        Location(
            timezone = timezoneValue,
            latitude = latitudeValue,
            longitude = longitudeValue,
            lastUpdated = lastUpdatedValue
        )
    }
}
