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

    companion object{
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