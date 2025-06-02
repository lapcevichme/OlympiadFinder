package com.lapcevichme.olympiadfinder.domain.model

sealed class Resource<out T> {
    // Состояние загрузки
    object Loading : Resource<Nothing>()

    // Состояние успешного получения данных
    data class Success<out T>(val data: T) : Resource<T>()

    // Состояние ошибки
    data class Failure(val exception: Throwable? = null) : Resource<Nothing>()

    // Вспомогательные функции для удобства создания экземпляров
    companion object {
        fun <T> loading(): Resource<T> = Loading
        fun <T> success(data: T): Resource<T> = Success(data)
        fun failure(exception: Throwable? = null): Resource<Nothing> = Failure(exception)
    }

    // Вспомогательная функция для обработки разных состояний
    inline fun <R> fold(
        onSuccess: (T) -> R,
        onFailure: (Throwable?) -> R,
        onLoading: () -> R // Добавляем обработку состояния Loading
    ): R {
        return when (this) {
            is Success -> onSuccess(this.data)
            is Failure -> onFailure(this.exception)
            is Loading -> onLoading()
        }
    }
}

