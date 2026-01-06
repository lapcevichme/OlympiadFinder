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

/**
 * Hilt модуль, предоставляющий зависимости, связанные с сетевым взаимодействием.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Предоставляет экземпляр [Gson] для сериализации и десериализации JSON.
     * Устанавливает формат даты по умолчанию.
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
    }

    /**
     * Предоставляет директорию для кэша OkHttp. Кэш хранится в поддиректории "okhttp_cache"
     * внутри стандартной директории кэша приложения.
     *
     * @param context Контекст приложения.
     * @return [File] - директория кэша.
     */
    @Provides
    @Singleton
    fun provideCacheDir(@ApplicationContext context: Context): File {
        val cacheDir = File(context.cacheDir, "okhttp_cache")
        cacheDir.mkdirs()
        return cacheDir
    }

    /**
     * Предоставляет экземпляр [Cache] для использования с OkHttpClient.
     * Устанавливает максимальный размер кэша в 10 MiB.
     *
     * @param cacheDir Директория кэша, предоставленная [provideCacheDir].
     * @return [Cache] - экземпляр кэша OkHttp.
     */
    @Provides
    @Singleton
    fun provideCache(cacheDir: File): Cache {
        val cacheSize = 10 * 1024 * 1024L // 10 MiB
        return Cache(cacheDir, cacheSize)
    }

    /**
     * Предоставляет сконфигурированный экземпляр [OkHttpClient] для выполнения сетевых запросов.
     * Включает логирование запросов в режиме отладки и использует предоставленный [Cache].
     *
     * @param cache Экземпляр [Cache], предоставленный [provideCache].
     * @return [OkHttpClient] - сконфигурированный HTTP клиент.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(cache: Cache): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) Level.BASIC else Level.NONE
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            // TODO: Другие интерцепторы (например, для аутентификации) здесь же
            .cache(cache)
            .build()
    }

    /**
     * Предоставляет экземпляр [Retrofit] для создания API сервисов.
     * Использует базовый URL из [BuildConfig], [Gson] для конвертации JSON и [OkHttpClient] для выполнения запросов.
     *
     * @param gson Экземпляр [Gson], предоставленный [provideGson].
     * @param okHttpClient Экземпляр [OkHttpClient], предоставленный [provideOkHttpClient].
     * @return [Retrofit] - сконфигурированный клиент Retrofit.
     */
    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    /**
     * Предоставляет реализацию интерфейса [OlympiadApiService] для взаимодействия с API олимпиад.
     * Создается с помощью [Retrofit].
     *
     * @param retrofit Экземпляр [Retrofit], предоставленный [provideRetrofit].
     * @return [OlympiadApiService] - API сервис для олимпиад.
     */
    @Provides
    @Singleton
    fun provideOlympiadApiService(retrofit: Retrofit): OlympiadApiService {
        return retrofit.create(OlympiadApiService::class.java)
    }
}