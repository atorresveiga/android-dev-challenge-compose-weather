package com.example.androiddevchallenge.domain


import com.example.androiddevchallenge.data.LocalForecastRepository
import com.example.androiddevchallenge.di.DefaultDispatcher
import com.example.androiddevchallenge.model.Location
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UpdateCurrentLocationUseCase @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val localForecastRepository: LocalForecastRepository
) {
    suspend fun execute(location: Location) {
        withContext(defaultDispatcher) {
            localForecastRepository.saveCurrentLocation(location)
        }
    }
}