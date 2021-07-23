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
import com.example.androiddevchallenge.data.LocalForecastRepository
import com.example.androiddevchallenge.data.UserLocationDataSource
import com.example.androiddevchallenge.domain.FindUserLocationUseCase
import com.example.androiddevchallenge.domain.LocationNotFoundException
import com.example.androiddevchallenge.fake.FakeTestLocalForecastRepository
import com.example.androiddevchallenge.fake.SuccessTestSearchLocation
import com.example.androiddevchallenge.fake.SuccessUserLocationDataSource
import com.example.androiddevchallenge.fake.UserLocationNotFoundDataSource
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import org.junit.Rule
import org.junit.Test

class FindUserLocationUseCaseTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineRule = MainCoroutineRule()

    private lateinit var useCase: FindUserLocationUseCase
    private lateinit var locationDataSource: UserLocationDataSource
    private lateinit var localRepository: LocalForecastRepository
    private val searchLocationDataSource = SuccessTestSearchLocation()

    @Test
    fun `When Location not found THEN throw LocationNotFoundException`() =
        coroutineRule.runBlockingTest {
            // WHEN location datasource can't access user location
            locationDataSource = UserLocationNotFoundDataSource()
            localRepository = FakeTestLocalForecastRepository()
            useCase = FindUserLocationUseCase(
                defaultDispatcher = coroutineRule.testDispatcher,
                userLocationDataSource = locationDataSource,
                localForecastRepository = localRepository,
                searchLocationDataSource = searchLocationDataSource
            )

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
            val location = Pair(0.0, 0.0)
            locationDataSource = SuccessUserLocationDataSource(location)
            localRepository = FakeTestLocalForecastRepository()
            useCase = FindUserLocationUseCase(
                defaultDispatcher = coroutineRule.testDispatcher,
                userLocationDataSource = locationDataSource,
                localForecastRepository = localRepository,
                searchLocationDataSource = searchLocationDataSource
            )
            useCase.execute()
            // THEN saved it locally
            val savedLocation = localRepository.getCurrentLocation().first()
            assertThat(savedLocation).isNotNull()
            assertThat(savedLocation?.latitude).isEqualTo(location.first)
            assertThat(savedLocation?.longitude).isEqualTo(location.second)
        }
}
