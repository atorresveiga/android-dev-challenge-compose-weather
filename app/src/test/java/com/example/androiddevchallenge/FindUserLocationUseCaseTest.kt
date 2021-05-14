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
package com.example.androiddevchallenge

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.androiddevchallenge.domain.FindUserLocationUseCase
import com.example.androiddevchallenge.domain.LocationNotFoundException
import com.example.androiddevchallenge.fake.FakeLocalForecastRepository
import com.example.androiddevchallenge.fake.FakeUserLocationDataSource
import com.example.androiddevchallenge.model.Location
import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.TimeZone
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Exception

class FindUserLocationUseCaseTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineRule = MainCoroutineRule()

    private lateinit var useCase: FindUserLocationUseCase
    private val locationDataSource = FakeUserLocationDataSource()
    private val localRepository = FakeLocalForecastRepository()

    @Before
    fun setup() {
        useCase = FindUserLocationUseCase(
            defaultDispatcher = coroutineRule.testDispatcher,
            userLocationDataSource = locationDataSource,
            localForecastRepository = localRepository
        )
    }

    @Test
    fun `When Location not found THEN throw LocationNotFoundException`() =
        coroutineRule.runBlockingTest {
            // WHEN location datasource can't access user location
            locationDataSource.location = null
            try {
                useCase.execute()
            } catch (exception: Exception) {
                // THEN throw a LocationNotFoundException
                assert(exception is LocationNotFoundException)
            }
        }

    @Test
    fun `When found user location THEN saved it locally`() =
        coroutineRule.runBlockingTest {
            // WHEN found user location
            val location = Location(
                timezone = TimeZone.currentSystemDefault().id,
                latitude = 100.0,
                longitude = 100.0
            )
            locationDataSource.location = location
            useCase.execute()
            // THEN saved it locally
            assertThat(localRepository.currentLocation).isEqualTo(location)
        }
}
