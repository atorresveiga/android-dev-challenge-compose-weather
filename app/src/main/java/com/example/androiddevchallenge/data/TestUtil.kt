package com.example.androiddevchallenge.data

import androidx.annotation.VisibleForTesting
import com.example.androiddevchallenge.model.HourForecast
import kotlin.random.Random

@VisibleForTesting
object TestUtil {

    fun createHourlyForecast(startEpoch: Long, hours: Int = 48): List<HourForecast> {
        val result = mutableListOf<HourForecast>()
        var datetime = startEpoch
        val weather = listOf("broken clouds", "scattered clouds", "clear sky", "light rain")
        for (hour in 0..hours) {
            val temperature = Random.nextInt(10, 38).toFloat()
            val hourForecast = HourForecast(
                datetime = datetime,
                temperature = temperature,
                feelsLike = temperature,
                pressure = Random.nextInt(1000, 3000),
                humidity = Random.nextInt(40),
                uvi = Random.nextInt(30).toFloat(),
                clouds = Random.nextInt(40),
                visibility = Random.nextLong(1000000),
                windSpeed = Random.nextInt(30).toFloat(),
                windDegrees = Random.nextInt(360).toFloat(),
                weather = weather[Random.nextInt(0, 3)],
                sunPosition = Random.nextInt(-1, 100),
                pop = Random.nextInt(0, 100) / 100f,
                rain = Random.nextInt(0, 1000) / 100f,
                snow = Random.nextInt(0, 1000) / 100f
            )
            datetime += 3600
            result.add(hourForecast)
        }
        return result
    }

}