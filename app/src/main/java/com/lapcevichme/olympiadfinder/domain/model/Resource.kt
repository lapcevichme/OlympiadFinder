package com.lapcevichme.olympiadfinder.domain.model

/**
 * Sealed class, представляющий состояние асинхронной операции.
 * Может быть в состоянии [Loading], [Success] или [Failure].
 *
 * @param T Тип данных, которые ожидаются в случае успеха.
 */
sealed class Resource<out T> {
    /** Состояние загрузки данных. */
    data object Loading : Resource<Nothing>()

    /**
     * Состояние успешного получения данных.
     * @property data Полученные данные.
     */
    data class Success<out T>(val data: T) : Resource<T>()

    /**
     * Состояние ошибки.
     * @property appError Типизированная ошибка [AppError].
     */
    data class Failure(val appError: AppError) : Resource<Nothing>()

    /** Вспомогательные функции для удобства создания экземпляров [Resource]. */
    companion object {
        fun <T> loading(): Resource<T> = Loading
        fun <T> success(data: T): Resource<T> = Success(data)
        fun failure(appError: AppError): Resource<Nothing> = Failure(appError)
    }

    /**
     * Вспомогательная функция для обработки различных состояний [Resource].
     * Позволяет выполнять различные действия в зависимости от текущего состояния.
     *
     * @param onSuccess Лямбда, вызываемая в случае [Success].
     * @param onFailure Лямбда, вызываемая в случае [Failure].
     * @param onLoading Лямбда, вызываемая в случае [Loading].
     * @return Результат выполнения соответствующей лямбды.
     */
    inline fun <R> fold(
        onSuccess: (T) -> R,
        onFailure: (AppError) -> R, // Изменен тип параметра на AppError
        onLoading: () -> R
    ): R {
        return when (this) {
            is Success -> onSuccess(this.data)
            is Failure -> onFailure(this.appError) // Используем appError
            is Loading -> onLoading()
        }
    }
}