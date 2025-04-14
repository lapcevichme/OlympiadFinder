package com.lapcevichme.olympiadfinder.data.di

import com.lapcevichme.olympiadfinder.data.repository.impl.OlympiadRepositoryImpl
import com.lapcevichme.olympiadfinder.data.repository.impl.SettingsRepositoryImpl
import com.lapcevichme.olympiadfinder.data.repository.mock.MockOlympiadRepositoryImpl
import com.lapcevichme.olympiadfinder.domain.repository.OlympiadRepository
import com.lapcevichme.olympiadfinder.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindOlympiadRepository(
        olympiadRepositoryImpl: OlympiadRepositoryImpl
    ): OlympiadRepository

    @Binds
    @MockOlympiadRepository
    abstract fun bindMockOlympiadRepositoryImpl(
        mockOlympiadRepositoryImpl: MockOlympiadRepositoryImpl
    ): OlympiadRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository
}
