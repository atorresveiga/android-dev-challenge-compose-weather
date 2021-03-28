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
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.androiddevchallenge.domain.ClearOldDataUseCase
import com.example.androiddevchallenge.domain.RefreshDataUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.datetime.Clock

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val refreshDataUseCase: RefreshDataUseCase,
    private val clearOldDataUseCase: ClearOldDataUseCase
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WorkerId = "SyncWorker"
    }

    override suspend fun doWork(): Result {
        var result = refreshDataUseCase.invoke(Unit)
        if (result is com.example.androiddevchallenge.data.Result.Error) {
            return Result.failure()
        }
        val currentDataTime = Clock.System.now().epochSeconds
        result = clearOldDataUseCase.invoke(currentDataTime - 3600)
        if (result is com.example.androiddevchallenge.data.Result.Error) {
            return Result.failure()
        }
        return Result.success()
    }
}
