package com.lapcevichme.olympiadfinder

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Основной класс приложения.
 *
 * Аннотация [@HiltAndroidApp] генерирует базовый класс приложения,
 * который используется Hilt для внедрения зависимостей на уровне приложения.
 * Этот класс должен быть указан в манифесте приложения (`android:name`).
 */
@HiltAndroidApp
class MyApplication : Application() {}