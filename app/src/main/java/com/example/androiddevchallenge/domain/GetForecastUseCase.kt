package com.example.androiddevchallenge.domain

import com.example.androiddevchallenge.data.LocalForecastRepository
import com.example.androiddevchallenge.data.Result
import com.example.androiddevchallenge.di.DefaultDispatcher
import com.example.androiddevchallenge.model.Forecast
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class GetForecastUseCase @Inject constructor(
    @DefaultDispatcher val defaultDispatcher: CoroutineDispatcher,
    private val localForecastRepository: LocalForecastRepository
) : FlowUseCase<Unit, Forecast?>(defaultDispatcher) {
    override fun execute(parameters: Unit): Flow<Result<Forecast?>> =
        localForecastRepository.getForecast()
}