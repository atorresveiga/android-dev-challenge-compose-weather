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
package com.example.androiddevchallenge.domain

import com.example.androiddevchallenge.data.LocalForecastRepository
import com.example.androiddevchallenge.data.Result
import com.example.androiddevchallenge.di.DefaultDispatcher
import com.example.androiddevchallenge.model.Location
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Get user's location saved in local datastore.
 */
class GetCurrentLocationUseCase @Inject constructor(
    @DefaultDispatcher val defaultDispatcher: CoroutineDispatcher,
    private val localForecastRepository: LocalForecastRepository
) : FlowUseCase<Unit, Location?>(defaultDispatcher) {
    override fun execute(parameters: Unit): Flow<Result<Location?>> {
        return localForecastRepository.getCurrentLocation().map { Result.Success(it) }
    }
}
