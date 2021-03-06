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
package com.atorresveiga.bluecloud.di

import android.content.Context
import androidx.room.Room
import com.atorresveiga.bluecloud.BuildConfig
import com.atorresveiga.bluecloud.data.AppDatabase
import com.atorresveiga.bluecloud.data.DataStoreManager
import com.atorresveiga.bluecloud.data.GeoNamesAPI
import com.atorresveiga.bluecloud.data.LocalForecastRepository
import com.atorresveiga.bluecloud.data.LocalForecastRepositoryDefault
import com.atorresveiga.bluecloud.data.MetNoAPI
import com.atorresveiga.bluecloud.data.MetNoDataSource
import com.atorresveiga.bluecloud.data.NetworkForecastDataSource
import com.atorresveiga.bluecloud.data.NetworkForecastRepository
import com.atorresveiga.bluecloud.data.OpenWeatherAPI
import com.atorresveiga.bluecloud.data.OpenWeatherDataSource
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    fun providesOpenWeatherAPI(): OpenWeatherAPI {

        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url()
            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("appid", BuildConfig.OpenWeatherSecAPIKey)
                .build()
            val requestBuilder = original.newBuilder().url(url)
            val newRequest = requestBuilder.build()
            chain.proceed(newRequest)
        }.build()

        val contentType = MediaType.get("application/json")
        val format = Json { ignoreUnknownKeys = true }

        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.OpenWeatherBaseURL)
            .addConverterFactory(format.asConverterFactory(contentType))
            .build()

        return retrofit.create(OpenWeatherAPI::class.java)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    fun providesGeoNamesAPI(): GeoNamesAPI {

        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url()
            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("username", BuildConfig.GeoNamesUser)
                .build()
            val requestBuilder = original.newBuilder().url(url)
            val newRequest = requestBuilder.build()
            chain.proceed(newRequest)
        }.build()

        val contentType = MediaType.get("application/json")
        val format = Json { ignoreUnknownKeys = true }

        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.GeoNamesBaseURL)
            .addConverterFactory(format.asConverterFactory(contentType))
            .build()

        return retrofit.create(GeoNamesAPI::class.java)
    }

    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "ForecastDatabase"
        ).build()
    }

    @Provides
    fun provideLocalForecastRepository(
        dataStoreManager: DataStoreManager,
        appDatabase: AppDatabase
    ): LocalForecastRepository {
        return LocalForecastRepositoryDefault(
            dataStoreManager, appDatabase
        )
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    fun provideMetNoAPI(): MetNoAPI {
        val appId = "${BuildConfig.APPLICATION_ID}/${BuildConfig.VERSION_NAME}"
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url()
            val url = originalHttpUrl.newBuilder()
                .build()
            val requestBuilder = original.newBuilder()
                .addHeader("User-Agent", appId)
                .url(url)
            val newRequest = requestBuilder.build()
            chain.proceed(newRequest)
        }.build()

        val contentType = MediaType.get("application/json")
        val format = Json { ignoreUnknownKeys = true }
        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.MetNoBaseURL)
            .addConverterFactory(format.asConverterFactory(contentType))
            .build()

        return retrofit.create(MetNoAPI::class.java)
    }

    @Provides
    fun provideMetNoNetworkForecastDataSource(
        dataStoreManager: DataStoreManager,
        openWeatherApi: OpenWeatherAPI,
        metNoApi: MetNoAPI
    ): NetworkForecastDataSource = NetworkForecastRepository(
        dataStoreManager = dataStoreManager,
        openWeatherDataSource = OpenWeatherDataSource(openWeatherApi),
        metNoDataSource = MetNoDataSource(metNoApi)
    )
}
