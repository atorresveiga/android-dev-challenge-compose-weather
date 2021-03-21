package com.example.androiddevchallenge.data

import com.example.androiddevchallenge.model.Forecast
import com.example.androiddevchallenge.model.HourForecast
import com.example.androiddevchallenge.model.Location
import javax.inject.Inject
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant


class OpenWeatherDataSource @Inject constructor(private val api: OpenWeatherAPI) :
    ForecastDataSource {
    override fun getForecast(latitude: Double, longitude: Double) = flow {
        emit(Result.Loading)
        val apiLocationForecast = api.oneCall(latitude, longitude)
        val result = Result.Success(apiLocationForecast.transformToForecast())
        emit(result)
    }
}

fun LocationForecast.transformToForecast(): Forecast {
    return OpenWeatherTransformation.transformToForecast(this)
}

class OpenWeatherTransformation {
    companion object {
        fun transformToForecast(locationForecast: LocationForecast): Forecast {
            val hours = mutableListOf<HourForecast>()
            val days = locationForecast.daily.associateBy { day -> day.datetime.getStringDate() }

            for (hour in locationForecast.hourly) {
                val hourDate = hour.datetime.getStringDate()
                days[hourDate]?.let { day ->
                    val sunPosition =
                        calculateSunPosition(hour.datetime, day.sunrise, day.sunset)
                    val hourForecast = HourForecast(
                        datetime = hour.datetime - locationForecast.offset,  // local representation of datetime
                        temperature = hour.temperature,
                        feelsLike = hour.feelsLike,
                        pressure = hour.pressure,
                        humidity = hour.humidity,
                        uvi = hour.uvi,
                        clouds = hour.clouds,
                        visibility = hour.visibility,
                        windSpeed = hour.windSpeed,
                        windDegrees = hour.windDegrees,
                        weather = hour.weather.joinToString { it.description },
                        pop = hour.pop,
                        sunPosition = sunPosition,
                        rain = hour.rain.lastHour,
                        snow = hour.snow.lastHour
                    )
                    hours.add(hourForecast)
                }
            }

            val location = Location(
                timezone = locationForecast.timezone,
                latitude = locationForecast.latitude,
                longitude = locationForecast.longitude,
                lastUpdated = Clock.System.now().epochSeconds
            )

            return Forecast(
                location = location,
                hourly = hours
            )
        }

        private fun calculateSunPosition(datetime: Long, sunrise: Long, sunset: Long): Int {
            val top = (sunset - sunrise) / 2
            return when {
                datetime < sunrise -> -1
                datetime > sunset -> -1
                datetime < top -> ((datetime - sunrise) * 100 / top).toInt()
                else -> ((sunset - datetime) * 100 / top).toInt()
            }
        }
    }
}

/**
 * A util function to get date string representation from a Unix datetime.
 */
fun Long.getStringDate() = Instant.fromEpochMilliseconds(this).toString().take(10)