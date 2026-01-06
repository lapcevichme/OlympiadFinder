package com.lapcevichme.olympiadfinder.data.di

import javax.inject.Qualifier

/**
 * Qualifier annotation для Hilt, используемая для предоставления моковой реализации
 * [com.lapcevichme.olympiadfinder.domain.repository.OlympiadRepository].
 *
 * Эта аннотация позволяет Hilt различать основную реализацию репозитория
 * ([com.lapcevichme.olympiadfinder.data.repository.impl.OlympiadRepositoryImpl])
 * и моковую реализацию ([com.lapcevichme.olympiadfinder.data.repository.mock.MockOlympiadRepositoryImpl]),
 * позволяя внедрять моковую версию в определенных сценариях, например, для тестов
 * или отладочных сборок.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MockOlympiadRepository