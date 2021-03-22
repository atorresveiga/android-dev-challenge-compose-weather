package com.example.androiddevchallenge.domain

import com.example.androiddevchallenge.data.LocalForecastRepository
import com.example.androiddevchallenge.di.IoDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

class ClearOldDataUseCase @Inject constructor(
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
    private val localForecastRepository: LocalForecastRepository
) : SuspendUseCase<Long, Unit>(ioDispatcher) {
    override suspend fun execute(parameters: Long) {
        localForecastRepository.clearOldData(parameters)
    }
}
