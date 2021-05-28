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
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import com.example.androiddevchallenge.model.DayForecast
import com.example.androiddevchallenge.model.HourForecast
import com.example.androiddevchallenge.model.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

@Entity(
    tableName = "hour_forecast_table",
    primaryKeys = ["datetime", "latitude", "longitude"]
)
data class HourForecastEntity(
    val datetime: Long,
    val temperature: Float,
    val feelsLike: Float,
    val pressure: Int,
    val humidity: Int,
    val uvi: Float,
    val clouds: Int,
    val visibility: Long,
    val windSpeed: Float,
    val windDegrees: Float,
    val weatherId: Int,
    val pop: Float,
    val rain: Float,
    val snow: Float,
    val latitude: Double,
    val longitude: Double
)

@Entity(
    tableName = "day_forecast_table",
    primaryKeys = ["datetime", "latitude", "longitude"]
)
data class DayForecastEntity(
    val datetime: Long,
    val pressure: Int,
    val humidity: Int,
    val uvi: Float,
    val sunrise: Long,
    val sunset: Long,
    val minTemperature: Float,
    val maxTemperature: Float,
    val rain: Float = 0f,
    val snow: Float = 0f,
    val weatherId: Int,
    val moonPhaseId: Int,
    val latitude: Double,
    val longitude: Double
)

@Entity(
    tableName = "location_table",
    primaryKeys = ["name"]
)
data class LocationEntity(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneId: String,
    val datetime: Long
)

@Dao
interface ForecastDAO {
    @Query("SELECT * from hour_forecast_table WHERE latitude = :latitude AND longitude=:longitude ORDER BY datetime ASC")
    fun getHourlyForecastFrom(latitude: Double, longitude: Double): Flow<List<HourForecastEntity>>

    @Query("SELECT * from day_forecast_table WHERE latitude = :latitude AND longitude=:longitude ORDER BY datetime ASC")
    fun getDailyForecastFrom(latitude: Double, longitude: Double): Flow<List<DayForecastEntity>>

    @Query("SELECT * from location_table ORDER BY datetime DESC LIMIT 5")
    fun getLocations(): Flow<List<LocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveHourlyForecast(hourly: List<HourForecastEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDailyForecast(daily: List<DayForecastEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLocation(location: LocationEntity)

    @Query("DELETE FROM hour_forecast_table WHERE datetime < :datetime")
    suspend fun clearHourlyForecastOlderThan(datetime: Long)

    @Query("DELETE FROM day_forecast_table WHERE datetime < :datetime")
    suspend fun clearDailyForecastOlderThan(datetime: Long)

    @Query("DELETE FROM location_table WHERE name NOT IN (SELECT name from location_table ORDER BY datetime DESC LIMIT 5)")
    suspend fun clearSelectedLocations()

    @Transaction
    suspend fun clearOlderThan(datetime: Long) {
        clearDailyForecastOlderThan(datetime - 3600 * 24)
        clearHourlyForecastOlderThan(datetime - 3600)
        clearSelectedLocations()
    }
}

/**
 * The [Room] database for this app.
 */
@Database(
    entities = [HourForecastEntity::class, DayForecastEntity::class, LocationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun forecastDAO(): ForecastDAO

    companion object {
        private const val databaseName = "besafe-db"

        fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}

fun DayForecastEntity.toDayForecast() = DayForecast(
    datetime = this.datetime,
    pressure = this.pressure,
    humidity = this.humidity,
    uvi = this.uvi,
    sunrise = this.sunrise,
    sunset = this.sunset,
    minTemperature = this.minTemperature,
    maxTemperature = this.maxTemperature,
    rain = this.rain,
    snow = this.snow,
    weatherId = this.weatherId,
    moonPhase = this.moonPhaseId
)

fun DayForecast.toDayForecastEntity(latitude: Double, longitude: Double) = DayForecastEntity(
    datetime = this.datetime,
    pressure = this.pressure,
    humidity = this.humidity,
    uvi = this.uvi,
    sunrise = this.sunrise,
    sunset = this.sunset,
    minTemperature = this.minTemperature,
    maxTemperature = this.maxTemperature,
    rain = this.rain,
    snow = this.snow,
    weatherId = this.weatherId,
    moonPhaseId = this.moonPhase,
    latitude = latitude,
    longitude = longitude
)

fun HourForecastEntity.toHourForecast() = HourForecast(
    datetime = this.datetime,
    temperature = this.temperature,
    feelsLike = this.feelsLike,
    pressure = this.pressure,
    humidity = this.humidity,
    uvi = this.uvi,
    clouds = this.clouds,
    visibility = this.visibility,
    windSpeed = this.windSpeed,
    windDegrees = this.windDegrees,
    weatherId = this.weatherId,
    pop = this.pop,
    rain = this.rain,
    snow = this.snow
)

fun HourForecast.toHourForecastEntity(latitude: Double, longitude: Double) = HourForecastEntity(
    datetime = this.datetime,
    temperature = this.temperature,
    feelsLike = this.feelsLike,
    pressure = this.pressure,
    humidity = this.humidity,
    uvi = this.uvi,
    clouds = this.clouds,
    visibility = this.visibility,
    windSpeed = this.windSpeed,
    windDegrees = this.windDegrees,
    weatherId = this.weatherId,
    pop = this.pop,
    rain = this.rain,
    snow = this.snow,
    latitude = latitude,
    longitude = longitude
)

fun Location.toLocationEntity() =
    LocationEntity(
        name = name,
        latitude = latitude,
        longitude = longitude,
        timezoneId = timezoneId,
        datetime = Clock.System.now().epochSeconds
    )

fun LocationEntity.toLocation() =
    Location(
        name = name,
        latitude = latitude,
        longitude = longitude,
        timezoneId = timezoneId
    )
