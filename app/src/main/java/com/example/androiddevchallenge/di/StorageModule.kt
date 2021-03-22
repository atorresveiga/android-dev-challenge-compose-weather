package com.example.androiddevchallenge.di

import android.content.Context
import androidx.room.Room
import com.example.androiddevchallenge.BuildConfig
import com.example.androiddevchallenge.data.AppDatabase
import com.example.androiddevchallenge.data.DataStoreManager
import com.example.androiddevchallenge.data.LocalForecastRepository
import com.example.androiddevchallenge.data.LocalForecastRepositoryImplementation
import com.example.androiddevchallenge.data.OpenWeatherAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import okhttp3.OkHttpClient
import retrofit2.Retrofit


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NetworkDataSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalDataSource

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

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

        return Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.OpenWeatherBaseURL)
            .build()
            .create(OpenWeatherAPI::class.java)
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
        return LocalForecastRepositoryImplementation(
            dataStoreManager, appDatabase
        )
    }

}