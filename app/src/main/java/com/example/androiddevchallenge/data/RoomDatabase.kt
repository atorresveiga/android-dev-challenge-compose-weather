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
import com.example.androiddevchallenge.model.SECONDS_IN_AN_HOUR
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

@Entity(
    tableName = "hour_forecast_table",
    primaryKeys = ["datetime", "latitude", "longitude", "dataSource"]
)
data class HourForecastEntity(
    val datetime: Long,
    val temperature: Float,
    val feelsLike: Float,
    val pressure: Float,
    val humidity: Float,
    val uvi: Float,
    val clouds: Float,
    val visibility: Long,
    val windSpeed: Float,
    val windDegrees: Float,
    val weatherId: Int,
    val pop: Float,
    val precipitation: Float,
    val latitude: Double,
    val longitude: Double,
    val dataSource: Int
)

@Entity(
    tableName = "day_forecast_table",
    primaryKeys = ["datetime", "latitude", "longitude", "dataSource"]
)
data class DayForecastEntity(
    val datetime: Long,
    val pressure: Float,
    val humidity: Float,
    val uvi: Float,
    val sunrise: Long,
    val sunset: Long,
    val clouds: Float,
    val windSpeed: Float,
    val windDegrees: Float,
    val minTemperature: Float,
    val maxTemperature: Float,
    val precipitation: Float = 0f,
    val weatherId: Int,
    val moonPhaseId: Int,
    val latitude: Double,
    val longitude: Double,
    val dataSource: Int
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
    val lastUpdated: Long,
    val datetime: Long
)

@Dao
interface ForecastDAO {
    @Query("SELECT * from hour_forecast_table WHERE dataSource= :dataSource AND latitude = :latitude AND longitude=:longitude AND datetime >= :startTime ORDER BY datetime ASC")
    fun getHourlyForecastFrom(
        latitude: Double,
        longitude: Double,
        startTime: Long,
        dataSource: Int
    ): Flow<List<HourForecastEntity>>

    @Query("SELECT * from day_forecast_table WHERE dataSource= :dataSource AND latitude = :latitude AND longitude=:longitude AND datetime>= :startTime ORDER BY datetime ASC")
    fun getDailyForecastFrom(
        latitude: Double,
        longitude: Double,
        startTime: Long,
        dataSource: Int
    ): Flow<List<DayForecastEntity>>

    @Query("SELECT * from location_table ORDER BY datetime DESC")
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
        clearDailyForecastOlderThan(datetime - SECONDS_IN_AN_HOUR * 24)
        clearHourlyForecastOlderThan(datetime - SECONDS_IN_AN_HOUR)
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
    clouds = clouds,
    windSpeed = windSpeed,
    windDegrees = windDegrees,
    minTemperature = this.minTemperature,
    maxTemperature = this.maxTemperature,
    precipitation = this.precipitation,
    weatherId = this.weatherId,
    moonPhase = this.moonPhaseId
)

fun DayForecast.toDayForecastEntity(latitude: Double, longitude: Double, dataSource: Int) = DayForecastEntity(
    datetime = this.datetime,
    pressure = this.pressure,
    humidity = this.humidity,
    uvi = this.uvi,
    sunrise = this.sunrise,
    sunset = this.sunset,
    clouds = clouds,
    windSpeed = windSpeed,
    windDegrees = windDegrees,
    minTemperature = this.minTemperature,
    maxTemperature = this.maxTemperature,
    precipitation = this.precipitation,
    weatherId = this.weatherId,
    moonPhaseId = this.moonPhase,
    latitude = latitude,
    longitude = longitude,
    dataSource = dataSource
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
    precipitation = this.precipitation
)

fun HourForecast.toHourForecastEntity(latitude: Double, longitude: Double, dataSource: Int) = HourForecastEntity(
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
    precipitation = this.precipitation,
    latitude = latitude,
    longitude = longitude,
    dataSource = dataSource
)

fun Location.toLocationEntity() =
    LocationEntity(
        name = name,
        latitude = latitude,
        longitude = longitude,
        timezoneId = timezoneId,
        lastUpdated = lastUpdated,
        datetime = Clock.System.now().epochSeconds
    )

fun LocationEntity.toLocation() =
    Location(
        name = name,
        latitude = latitude,
        longitude = longitude,
        timezoneId = timezoneId,
        lastUpdated = lastUpdated
    )
