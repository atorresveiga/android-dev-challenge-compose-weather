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

import android.annotation.SuppressLint
import android.content.Context
import com.example.androiddevchallenge.model.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.TimeZone

interface LocationDataSource {
    suspend fun getLocation(): Location?
}

class GMSLocationDataSource @Inject constructor(@ApplicationContext appContext: Context) :
    LocationDataSource {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(appContext)

    @SuppressLint("MissingPermission")
    override suspend fun getLocation(): Location? {

        val fusedLocation =
            fusedLocationClient.lastLocation.await() ?: fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_LOW_POWER,
                CancellationTokenSource().token
            ).await()

        return fusedLocation?.let { location ->
            val timezone = TimeZone.currentSystemDefault().id
            return@let Location(
                timezone = timezone,
                latitude = location.latitude,
                longitude = location.longitude
            )
        }

    }
}

/**
 * Util function to transform Task callbacks to suspend function
 */
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> Task<T>.await(): T {
    // fast path
    if (isComplete) {
        val e = exception
        return if (e == null) {
            if (isCanceled) {
                throw CancellationException(
                    "Task $this was cancelled normally."
                )
            } else {
                result
            }
        } else {
            throw e
        }
    }

    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            val e = exception
            if (e == null) {
                if (isCanceled) cont.cancel() else cont.resume(result) {
                    throw CancellationException(
                        "Task $this was cancelled normally."
                    )
                }
            } else {
                cont.resumeWithException(e)
            }
        }
    }
}
