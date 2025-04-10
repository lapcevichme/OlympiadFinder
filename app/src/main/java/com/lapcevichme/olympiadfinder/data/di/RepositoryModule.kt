package com.lapcevichme.olympiadfinder.data.di

import com.lapcevichme.olympiadfinder.data.repository.impl.OlympiadRepositoryImpl
import com.lapcevichme.olympiadfinder.data.repository.mock.MockOlympiadRepositoryImpl
import com.lapcevichme.olympiadfinder.domain.repository.OlympiadRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

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
}
