package com.example.androiddevchallenge.domain

import com.example.androiddevchallenge.data.LocalForecastRepository
import com.example.androiddevchallenge.data.LocationDataSource
import com.example.androiddevchallenge.di.DefaultDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Find user's location save it in local datastore.
 */
class FindLocationUseCase @Inject constructor(
    @DefaultDispatcher val defaultDispatcher: CoroutineDispatcher,
    private val locationDataSource: LocationDataSource,
    private val localForecastRepository: LocalForecastRepository
) : SuspendUseCase<Unit, Unit>(defaultDispatcher) {
    override suspend fun execute(parameters: Unit) {
        val location = locationDataSource.getLocation()
            ?: throw LocationNotFoundException("User location is null")

        localForecastRepository.saveCurrentLocation(location)
    }
}

class LocationNotFoundException(message: String) : RuntimeException(message)