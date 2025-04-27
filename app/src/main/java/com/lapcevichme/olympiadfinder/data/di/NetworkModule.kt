package com.lapcevichme.olympiadfinder.data.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lapcevichme.olympiadfinder.data.network.OlympiadApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

    // emulator : http://10.0.2.2:8080
    // device : http://192.168.3.6:8080

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideOlympiadApiService(retrofit: Retrofit): OlympiadApiService {
        return retrofit.create(OlympiadApiService::class.java)
    }
}