package com.lapcevichme.olympiadfinder.domain.model

import retrofit2.HttpException
import java.io.IOException

/**
 * Герметичный класс, представляющий различные типы ошибок,
 * которые могут возникнуть в приложении.
 * Это доменная модель ошибки, которая делает обработку ошибок более явной и типизированной.
 */
sealed class AppError {
    /** Ошибка, связанная с отсутствием сетевого подключения или проблемами с сетью. */
    object NetworkError : AppError()

    /** Ошибка, связанная с ответом сервера (например, 4xx, 5xx коды HTTP). */
    data class ServerError(val message: String? = null) : AppError()

    /** Ошибка, связанная с некорректными или отсутствующими данными. */
    data class DataError(val message: String? = null) : AppError()

    /** Ошибка, когда запрашиваемый ресурс не найден (например, 404). */
    object NotFoundError : AppError()

    /** Неизвестная или непредвиденная ошибка. */
    data class UnknownError(val message: String? = null) : AppError()

    companion object {
        /**
         * Фабричная функция для создания экземпляра [AppError] из [Throwable].
         * Используется для маппинга низкоуровневых исключений в доменные ошибки.
         * @param throwable Исключение, которое нужно смаппить.
         * @return Соответствующий объект [AppError].
         */
        fun fromThrowable(throwable: Throwable?): AppError {
            return when (throwable) {
                is IOException -> NetworkError // Сетевые ошибки
                is HttpException -> ServerError(throwable.message()) // HTTP ошибки
                is IllegalStateException -> DataError(throwable.message) // Ошибки, связанные с данными
                else -> UnknownError(throwable?.message) // Все остальные ошибки
            }
        }
    }
}
