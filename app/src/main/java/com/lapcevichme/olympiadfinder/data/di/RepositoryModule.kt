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

/**
 * Hilt модуль, связывающий интерфейсы репозиториев с их конкретными реализациями.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Связывает интерфейс [OlympiadRepository] с его основной реализацией [OlympiadRepositoryImpl].
     * Когда кто-то запрашивает [OlympiadRepository] без квалификатора, будет предоставлен [OlympiadRepositoryImpl].
     *
     * @param olympiadRepositoryImpl Реализация репозитория для работы с данными об олимпиадах из сети.
     * @return [OlympiadRepository] - экземпляр основной реализации репозитория олимпиад.
     */
    @Binds
    abstract fun bindOlympiadRepository(
        olympiadRepositoryImpl: OlympiadRepositoryImpl
    ): OlympiadRepository

    /**
     * Связывает интерфейс [OlympiadRepository] с моковой реализацией [MockOlympiadRepositoryImpl]
     * при использовании квалификатора [@MockOlympiadRepository].
     *
     * @param mockOlympiadRepositoryImpl Моковая реализация репозитория олимпиад для тестирования и разработки.
     * @return [OlympiadRepository] - экземпляр моковой реализации репозитория олимпиад.
     */
    @Binds
    @MockOlympiadRepository
    abstract fun bindMockOlympiadRepositoryImpl(
        mockOlympiadRepositoryImpl: MockOlympiadRepositoryImpl
    ): OlympiadRepository

    /**
     * Связывает интерфейс [SettingsRepository] с его реализацией [SettingsRepositoryImpl] и делает его Singleton.
     *
     * @param settingsRepositoryImpl Реализация репозитория для управления настройками приложения.
     * @return [SettingsRepository] - Singleton экземпляр репозитория настроек.
     */
    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository
}