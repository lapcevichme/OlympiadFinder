package com.lapcevichme.olympiadfinder.data.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lapcevichme.olympiadfinder.BuildConfig
import com.lapcevichme.olympiadfinder.data.network.OlympiadApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
    }

    @Provides
    @Singleton
    fun provideCacheDir(@ApplicationContext context: Context): File {
        // Создаем поддиректорию "okhttp_cache" внутри стандартной директории кэша приложения
        val cacheDir = File(context.cacheDir, "okhttp_cache")
        cacheDir.mkdirs()
        return cacheDir
    }

    @Provides
    @Singleton
    // Принимаем директорию кэша, которую предоставила предыдущая функция
    fun provideCache(cacheDir: File): Cache {
        // Определяем максимальный размер кэша 10 MiB
        val cacheSize = 10 * 1024 * 1024L
        return Cache(cacheDir, cacheSize)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(cache: Cache): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) Level.BASIC else Level.NONE
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            // TODO: Другие интерцепторы (например, для аутентификации, кэширования) здесь же
            .cache(cache)
            .build()
    }

    // emulator : http://10.0.2.2:8080
    // device : http://192.168.3.6:8080

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideOlympiadApiService(retrofit: Retrofit): OlympiadApiService {
        return retrofit.create(OlympiadApiService::class.java)
    }
}