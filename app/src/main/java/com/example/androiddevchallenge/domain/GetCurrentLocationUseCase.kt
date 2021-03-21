package com.example.androiddevchallenge.domain

import com.example.androiddevchallenge.data.DataStoreManager
import com.example.androiddevchallenge.data.Result
import com.example.androiddevchallenge.di.DefaultDispatcher
import com.example.androiddevchallenge.model.Location
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Get user's location saved in local datastore.
 */
class GetCurrentLocationUseCase @Inject constructor(
    @DefaultDispatcher val defaultDispatcher: CoroutineDispatcher,
    private val dataStoreManager: DataStoreManager
) : FlowUseCase<Unit, Location?>(defaultDispatcher) {
    override fun execute(parameters: Unit): Flow<Result<Location?>> {
        return dataStoreManager.currentLocation.map { Result.Success(it) }
    }
}