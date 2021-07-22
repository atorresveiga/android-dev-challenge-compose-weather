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

import com.example.androiddevchallenge.FIRST_QUARTER
import com.example.androiddevchallenge.FULL_MOON
import com.example.androiddevchallenge.NEW_MOON
import com.example.androiddevchallenge.THIRD_QUARTER
import com.example.androiddevchallenge.WANING_CRESCENT
import com.example.androiddevchallenge.WANING_GIBBOUS
import com.example.androiddevchallenge.WAXING_CRESCENT
import com.example.androiddevchallenge.WAXING_GIBBOUS
import kotlinx.datetime.LocalDate
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * 1980 January 0.0 in JDN
 */
const val EPOCH = 2444238.5

/**
 * Ecliptic longitude of the Sun at epoch 1980.0
 */
const val ECLIPTIC_LONGITUDE_EPOCH = 278.833540

/**
 * Ecliptic longitude of the Sun at perigee
 */
const val ECLIPTIC_LONGITUDE_PERIGEE = 282.596403

/**
 * Eccentricity of Earth's orbit
 */
const val ECCENTRICITY = 0.016718

// Elements of the Moon's orbit, epoch 1980.0
/**
 * Moon's mean longitude at the epoch
 */
const val MOON_MEAN_LONGITUDE_EPOCH = 64.975464

/**
 * Mean longitude of the perigee at the epoch
 */
const val MOON_MEAN_PERIGEE_EPOCH = 349.383063

/**
 * Convert date to julian date
 * https://en.wikipedia.org/wiki/Julian_day#Converting_Gregorian_calendar_date_to_Julian_Day_Number
 */
fun LocalDate.toJDN() =
    (1461 * (this.year + 4800 + (this.monthNumber - 14) / 12)) / 4 + (367 * (this.monthNumber - 2 - 12 * ((this.monthNumber - 14) / 12))) / 12 - (3 * ((this.year + 4900 + (this.monthNumber - 14) / 12) / 100)) / 4 + this.dayOfMonth - 32075

fun fixAngle(angle: Double) = angle - 360.0 * floor(angle / 360.0)

fun toRad(deg: Double) = deg * PI / 180.0

fun toDeg(rad: Double) = rad * 180.0 / PI

fun kepler(m: Double, ecc: Double): Double {
    val epsilon = 1e-6
    val mRad = toRad(m)
    var e = m
    do {
        val delta = e - ecc * sin(e) - mRad
        e -= delta / (1.0 - ecc * cos(e))
    } while (abs(delta) > epsilon)

    return e
}

/**
 * Calculate moon phase based on
 * http://bazaar.launchpad.net/~keturn/py-moon-phase/trunk/annotate/head:/moon.py
 * The result is the moon phase position in MoonPhase enum
 */
fun getMoonPhase(date: LocalDate): Int {
    val day = date.toJDN() - EPOCH
    val n = fixAngle((360 / 365.2422) * day)
    val m = fixAngle(n + ECLIPTIC_LONGITUDE_EPOCH - ECLIPTIC_LONGITUDE_PERIGEE)

    var ec = kepler(m, ECCENTRICITY)
    ec = sqrt((1 + ECCENTRICITY) / (1 - ECCENTRICITY)) * tan(ec / 2.0)
    ec = 2 * toDeg(atan(ec))

    val lambdaSun = fixAngle(ec + ECLIPTIC_LONGITUDE_PERIGEE)

    val moonLongitude = fixAngle(13.1763966 * day + MOON_MEAN_LONGITUDE_EPOCH)
    val mm = fixAngle(moonLongitude - 0.1114041 * day - MOON_MEAN_PERIGEE_EPOCH)

    val evection = 1.2739 * sin(toRad(2 * (moonLongitude - lambdaSun) - mm))
    val annualEq = 0.1858 * sin(toRad(m))

    val a3 = 0.37 * sin(toRad(m))
    val mmp = mm + evection - annualEq - a3

    val mec = 6.2886 * sin(toRad(mmp))
    val a4 = 0.214 * sin(toRad(2 * mmp))

    val lp = moonLongitude + evection + mec - annualEq + a4
    val variation = 0.6583 * sin(toRad(2 * (lp - lambdaSun)))

    val lpp = lp + variation

    val moonAge = lpp - lambdaSun
    return fromPhase(fixAngle(moonAge) / 360.0)
}

fun fromPhase(phase: Double): Int {
    return when (phase) {
        in 0.0625..0.1876 -> WAXING_CRESCENT
        in 0.1876..0.3126 -> FIRST_QUARTER
        in 0.3126..0.4376 -> WAXING_GIBBOUS
        in 0.4376..0.5626 -> FULL_MOON
        in 0.5626..0.6876 -> WANING_GIBBOUS
        in 0.6876..0.8126 -> THIRD_QUARTER
        in 0.8126..0.9376 -> WANING_CRESCENT
        else -> NEW_MOON
    }
}
