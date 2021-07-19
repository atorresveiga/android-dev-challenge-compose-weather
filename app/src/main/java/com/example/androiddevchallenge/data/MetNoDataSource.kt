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

import com.example.androiddevchallenge.model.DayForecast
import com.example.androiddevchallenge.model.Forecast
import com.example.androiddevchallenge.model.HourForecast
import com.example.androiddevchallenge.model.Location
import com.example.androiddevchallenge.ui.MoonPhaseFormatter
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetAt
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.math.E
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class MetNoDataSource @Inject constructor(private val api: MetNoAPI) : NetworkForecastDataSource {
    override suspend fun getForecast(location: Location): Forecast {

        val apiLocationForecast =
            api.complete(latitude = location.latitude, longitude = location.longitude)

        val timeZone = TimeZone.of(location.timezoneId)
        val instant = Clock.System.now()
        val offset = timeZone.offsetAt(instant).id
        val date = instant.toLocalDateTime(timeZone).date.toString()

        val apiSunriseMoonPhase = api.sunriseMoonPhase(
            latitude = location.latitude,
            longitude = location.longitude,
            offset = offset,
            date = date
        )

        return transformToForecast(
            metNoForecast = apiLocationForecast,
            metNoSunriseMoonPhase = apiSunriseMoonPhase,
            location = location
        )
    }

    private fun encodeWeatherId(code: String): Int {
        return when (code) {
            "clearsky_day",
            "clearsky_night",
            "clearsky_polartwilight",
            "fair_day",
            "fair_night",
            "fair_polartwilight" -> 0
            "lightssnowshowersandthunder_day",
            "lightssnowshowersandthunder_night",
            "lightssnowshowersandthunder_polartwilight" -> 32106
            "lightsnowshowers_day",
            "lightsnowshowers_night",
            "lightsnowshowers_polartwilight" -> 12106
            "heavyrainandthunder" -> 22203
            "heavysnowandthunder" -> 22206
            "rainandthunder" -> 20003
            "heavysleetshowersandthunder_day",
            "heavysleetshowersandthunder_night",
            "heavysleetshowersandthunder_polartwilight" -> 32205
            "heavysnow" -> 2206
            "heavyrainshowers_day",
            "heavyrainshowers_night",
            "heavyrainshowers_polartwilight" -> 12203
            "lightsleet" -> 2105
            "heavyrain" -> 2203
            "lightrainshowers_day",
            "lightrainshowers_night",
            "lightrainshowers_polartwilight" -> 12103
            "heavysleetshowers_day",
            "heavysleetshowers_night",
            "heavysleetshowers_polartwilight" -> 12205
            "lightsleetshowers_day",
            "lightsleetshowers_night",
            "lightsleetshowers_polartwilight" -> 12105
            "snow" -> 6
            "heavyrainshowersandthunder_day",
            "heavyrainshowersandthunder_night",
            "heavyrainshowersandthunder_polartwilight" -> 32203
            "snowshowers_day",
            "snowshowers_night",
            "snowshowers_polartwilight" -> 10006
            "fog" -> 8
            "snowshowersandthunder_day",
            "snowshowersandthunder_night",
            "snowshowersandthunder_polartwilight" -> 30006
            "lightsnowandthunder" -> 22106
            "heavysleetandthunder" -> 22205
            "lightrain" -> 2103
            "rainshowersandthunder_day",
            "rainshowersandthunder_night",
            "rainshowersandthunder_polartwilight" -> 30003
            "rain" -> 3
            "lightsnow" -> 2106
            "lightrainshowersandthunder_day",
            "lightrainshowersandthunder_night",
            "lightrainshowersandthunder_polartwilight" -> 32103
            "heavysleet" -> 2205
            "sleetandthunder" -> 20005
            "lightrainandthunder" -> 22103
            "sleet" -> 5
            "lightssleetshowersandthunder_day",
            "lightssleetshowersandthunder_night",
            "lightssleetshowersandthunder_polartwilight" -> 32105
            "lightsleetandthunder" -> 22105
            "partlycloudy_day",
            "partlycloudy_night",
            "partlycloudy_polartwilight" -> 1101
            "sleetshowersandthunder_day",
            "sleetshowersandthunder_night",
            "sleetshowersandthunder_polartwilight" -> 30005
            "rainshowers_day",
            "rainshowers_night",
            "rainshowers_polartwilight" -> 10003
            "snowandthunder" -> 20006
            "sleetshowers_day",
            "sleetshowers_night",
            "sleetshowers_polartwilight" -> 10005
            "cloudy" -> 1301
            "heavysnowshowersandthunder_day",
            "heavysnowshowersandthunder_night",
            "heavysnowshowersandthunder_polartwilight" -> 32206
            "heavysnowshowers_day",
            "heavysnowshowers_night",
            "heavysnowshowers_polartwilight" -> 12206
            else -> throw IllegalArgumentException("Weather code not found")
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun transformToForecast(
        metNoForecast: MetNoForecast,
        metNoSunriseMoonPhase: MetNoSunriseMoonPhase,
        location: Location
    ): Forecast {
        val hourly = mutableListOf<HourForecast>()
        val daily = mutableListOf<DayForecast>()
        val timeZone = TimeZone.of(location.timezoneId)
        var currentDayDateTime: Long? = null
        val currentDayTimeSeries = mutableListOf<MetNoTimeSeries>()
        val sunriseMoonPhase = metNoSunriseMoonPhase.location.time
            .filter { it.sunrise != null && it.sunset != null }
            .associateBy { it.date }

        var previousMoonPhase = -1

        for (timeSeries in metNoForecast.data.timeSeries) {

            val instant = timeSeries.time.toInstant()
            val date = instant.toLocalDateTime(timeZone = timeZone)

            // Process Hour Forecast only if it has nextHour
            if (timeSeries.data.nextHour != null) {
                val hourForecast = processHourForecast(
                    timeSeries = timeSeries,
                    datetime = instant.epochSeconds
                )
                hourly.add(hourForecast)
            }

            // Process Day Forecast
            val dayDateTime = instant.minus(Duration.hours(date.hour)).epochSeconds
            when {
                currentDayDateTime == null -> {
                    currentDayDateTime = dayDateTime
                }
                currentDayDateTime != dayDateTime -> {
                    val sunriseMoonPhaseInfo = sunriseMoonPhase[date.date.toString()]
                        ?: throw IllegalArgumentException("Missing sun and moon info")
                    val day = processDayForecast(
                        dayTimeSeries = currentDayTimeSeries,
                        sunriseMoonPhaseInfo = sunriseMoonPhaseInfo,
                        datetime = currentDayDateTime,
                        timeZone = timeZone,
                        previousMoonPhase = previousMoonPhase
                    )
                    daily.add(day)
                    previousMoonPhase = fromPhase(sunriseMoonPhaseInfo.moon.phase.toDouble() / 100.0).ordinal
                    currentDayDateTime = dayDateTime
                    currentDayTimeSeries.clear()
                }
                else -> {
                    currentDayTimeSeries.add(timeSeries)
                }
            }
        }

        // Process the remaining day
        if (currentDayDateTime != null && currentDayTimeSeries.size > 0) {

            val instant = currentDayTimeSeries.first().time.toInstant()
            val date = instant.toLocalDateTime(timeZone = timeZone)

            val sunriseMoonPhaseInfo = sunriseMoonPhase[date.date.toString()]
                ?: throw IllegalArgumentException("Missing sun and moon info")

            val day = processDayForecast(
                dayTimeSeries = currentDayTimeSeries,
                sunriseMoonPhaseInfo = sunriseMoonPhaseInfo,
                datetime = currentDayDateTime,
                timeZone = timeZone,
                previousMoonPhase = previousMoonPhase
            )
            daily.add(day)
        }

        return Forecast(
            location = location.copy(lastUpdated = Clock.System.now().epochSeconds),
            hourly = hourly,
            daily = daily
        )
    }

    private fun processDayForecast(
        dayTimeSeries: List<MetNoTimeSeries>,
        sunriseMoonPhaseInfo: MetNoSunriseMoonPhaseDayInfo,
        datetime: Long,
        timeZone: TimeZone,
        previousMoonPhase: Int
    ): DayForecast {

        var pressure: Float = Float.MIN_VALUE
        var humidity: Float = Float.MIN_VALUE
        var uvi: Float = Float.MIN_VALUE
        var clouds: Float = Float.MIN_VALUE
        var windSpeed: Float = Float.MIN_VALUE
        var windDirectionSum = 0f
        var minTemperature: Float = Float.MAX_VALUE
        var maxTemperature: Float = Float.MIN_VALUE
        var precipitation: Float = Float.MIN_VALUE
        var weatherId = 0

        for (timeSeries in dayTimeSeries) {

            val instant = timeSeries.time.toInstant()
            val date = instant.toLocalDateTime(timeZone = timeZone)

            val details = timeSeries.data.instant.details
            pressure = maxOf(pressure, details.pressure)
            humidity = maxOf(humidity, details.humidity)
            uvi = getMaxValue(
                current = uvi,
                default = uvi,
                currentHour = date.hour,
                instant = details.uv,
                nextHour = timeSeries.data.nextHour?.details?.uv,
                next6Hours = timeSeries.data.next6Hour?.details?.uv,
                next12Hours = timeSeries.data.next12Hour?.details?.uv
            )
            clouds = maxOf(clouds, details.clouds)
            windSpeed = maxOf(windSpeed, details.windSpeed)
            windDirectionSum += details.windDirection
            minTemperature = getMinValue(
                current = minTemperature,
                default = minTemperature,
                instant = timeSeries.data.instant.details.temperature,
                currentHour = date.hour,
                nextHour = timeSeries.data.nextHour?.details?.temperatureMin,
                next6Hours = timeSeries.data.next6Hour?.details?.temperatureMin,
                next12Hours = timeSeries.data.next12Hour?.details?.temperatureMin
            )
            maxTemperature = getMaxValue(
                current = maxTemperature,
                default = maxTemperature,
                instant = timeSeries.data.instant.details.temperature,
                currentHour = date.hour,
                nextHour = timeSeries.data.nextHour?.details?.temperatureMax,
                next6Hours = timeSeries.data.next6Hour?.details?.temperatureMax,
                next12Hours = timeSeries.data.next12Hour?.details?.temperatureMax
            )
            precipitation = getMaxValue(
                current = precipitation,
                default = precipitation,
                currentHour = date.hour,
                nextHour = timeSeries.data.nextHour?.details?.precipitation,
                next6Hours = timeSeries.data.next6Hour?.details?.precipitation,
                next12Hours = timeSeries.data.next12Hour?.details?.precipitation
            )
            weatherId =
                getLongerLastingValue(
                    current = weatherId,
                    default = weatherId,
                    currentHour = date.hour,
                    nextHour = encodeWeatherId(
                        timeSeries.data.nextHour?.summary?.code ?: "clearsky_day"
                    ),
                    next6Hours = encodeWeatherId(
                        timeSeries.data.next6Hour?.summary?.code ?: "clearsky_day"
                    ),
                    next12Hours = encodeWeatherId(
                        timeSeries.data.next12Hour?.summary?.code ?: "clearsky_day"
                    )
                )
        }

        val windDegrees = windDirectionSum / dayTimeSeries.size
        val sunrise = sunriseMoonPhaseInfo.sunrise!!.time.toInstant().epochSeconds
        val sunset = sunriseMoonPhaseInfo.sunset!!.time.toInstant().epochSeconds
        val moonPhase = fromPhase(sunriseMoonPhaseInfo.moon.phase.toDouble() / 100.0).ordinal
        val encodedMoonPhase = MoonPhaseFormatter.encode(moonPhase, previousMoonPhase)

        return DayForecast(
            datetime = datetime,
            pressure = pressure,
            humidity = humidity,
            uvi = uvi,
            sunrise = sunrise,
            sunset = sunset,
            clouds = clouds,
            windSpeed = windSpeed,
            windDegrees = windDegrees,
            minTemperature = minTemperature,
            maxTemperature = maxTemperature,
            precipitation = precipitation,
            weatherId = weatherId,
            moonPhase = encodedMoonPhase
        )
    }

    private fun processHourForecast(
        timeSeries: MetNoTimeSeries,
        datetime: Long
    ): HourForecast {
        val details = timeSeries.data.instant.details
        val visibility = 10000 - (10000 * details.fogArea).toLong()
        val feelLike = calculateFeelsLike(
            temperature = details.temperature,
            humidity = details.humidity,
            windSpeed = details.windSpeed
        )

        // hour > 6 hour > 12 hour. Try to find the most accurate value in the timeSeries
        val pop = timeSeries.data.nextHour?.details?.pop
            ?: timeSeries.data.next6Hour?.details?.pop
            ?: timeSeries.data.next12Hour?.details?.pop
            ?: 0f

        val amount = timeSeries.data.nextHour?.details?.precipitation
            ?: timeSeries.data.next6Hour?.details?.precipitation
            ?: timeSeries.data.next12Hour?.details?.precipitation
            ?: 0f

        val code = timeSeries.data.nextHour?.summary?.code
            ?: timeSeries.data.next6Hour?.summary?.code
            ?: timeSeries.data.next12Hour?.summary?.code
            ?: "clearsky_day"

        return HourForecast(
            datetime = datetime, // utc representation of datetime
            temperature = details.temperature,
            feelsLike = feelLike,
            pressure = details.pressure,
            humidity = details.humidity,
            uvi = details.uv,
            clouds = details.clouds,
            visibility = visibility,
            windSpeed = details.windSpeed,
            windDegrees = details.windDirection,
            weatherId = encodeWeatherId(code),
            pop = pop,
            precipitation = amount
        )
    }

    // http://www.bom.gov.au/info/thermal_stress/?cid=003bl08#atapproximation
    private fun calculateFeelsLike(temperature: Float, humidity: Float, windSpeed: Float): Float {
        val ta = temperature.toDouble()
        val ws = windSpeed.toDouble()
        val rh = humidity.toDouble()
        val p = rh / 100 * 6.105 * E.pow(17.27 * ta / (237.7 + ta))
        val feelsLike = ta + 0.33 * p - 0.70 * ws - 4.00
        return feelsLike.toFloat()
    }

    private fun <T : Comparable<T>> getLongerLastingValue(
        current: T,
        default: T,
        currentHour: Int,
        instant: T? = null,
        nextHour: T? = null,
        next6Hours: T? = null,
        next12Hours: T? = null
    ): T {
        val includeNext6Hours = currentHour + 6 < 24
        val includeNext12Hours = currentHour + 12 < 24

        if (includeNext12Hours && next12Hours != null) return next12Hours
        if (includeNext6Hours && next6Hours != null) return next6Hours

        return maxOf(current, instant ?: default, nextHour ?: default)
    }

    private fun <T : Comparable<T>> getMaxValue(
        current: T,
        default: T,
        currentHour: Int,
        instant: T? = null,
        nextHour: T? = null,
        next6Hours: T? = null,
        next12Hours: T? = null
    ): T {
        val includeNext6Hours = currentHour + 6 < 24
        val includeNext12Hours = currentHour + 12 < 24
        return maxOf(
            current,
            instant ?: default,
            nextHour ?: default,
            if (includeNext6Hours) next6Hours ?: default else default,
            if (includeNext12Hours) next12Hours ?: default else default
        )
    }

    private fun <T : Comparable<T>> getMinValue(
        current: T,
        default: T,
        currentHour: Int,
        instant: T? = null,
        nextHour: T? = null,
        next6Hours: T? = null,
        next12Hours: T? = null
    ): T {
        val includeNext6Hours = currentHour + 6 < 24
        val includeNext12Hours = currentHour + 12 < 24
        return minOf(
            current,
            instant ?: default,
            nextHour ?: default,
            if (includeNext6Hours) next6Hours ?: default else default,
            if (includeNext12Hours) next12Hours ?: default else default
        )
    }
}
