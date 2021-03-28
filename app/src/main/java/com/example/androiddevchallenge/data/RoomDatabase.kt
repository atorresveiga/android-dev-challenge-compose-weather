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
import com.example.androiddevchallenge.model.HourForecast
import kotlinx.coroutines.flow.Flow

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
    val weather: String,
    val sunPosition: Int,
    val pop: Float,
    val rain: Float,
    val snow: Float,
    val latitude: Double,
    val longitude: Double
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
    weather = this.weather,
    sunPosition = this.sunPosition,
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
    weather = this.weather,
    sunPosition = this.sunPosition,
    pop = this.pop,
    rain = this.rain,
    snow = this.snow,
    latitude = latitude,
    longitude = longitude
)

@Dao
interface HourForecastDAO {
    @Query("SELECT * from hour_forecast_table WHERE latitude = :latitude AND longitude=:longitude ORDER BY datetime ASC")
    fun getHourlyForecastFrom(latitude: Double, longitude: Double): Flow<List<HourForecastEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveForecast(forecast: List<HourForecastEntity>)

    @Query("DELETE FROM hour_forecast_table WHERE datetime < :datetime")
    suspend fun clearOlderThan(datetime: Long)
}

/**
 * The [Room] database for this app.
 */
@Database(entities = [HourForecastEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hourForecastDAO(): HourForecastDAO

    companion object {
        private const val databaseName = "besafe-db"

        fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
