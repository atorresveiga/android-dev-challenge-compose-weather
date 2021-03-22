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