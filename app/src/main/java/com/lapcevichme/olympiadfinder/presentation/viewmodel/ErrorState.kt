package com.lapcevichme.olympiadfinder.presentation.viewmodel

/**
 * Sealed class для представления различных состояний ошибок,
 * которые могут быть отображены в UI.
 */
sealed class ErrorState {
    /** Состояние, когда нет активной ошибки. */
    data object NoError : ErrorState()

    /** Состояние ошибки, связанной с сетью (нет подключения, таймаут и т.д.). */
    data object NetworkError : ErrorState()

    /**
     * Состояние ошибки, связанной с сервером (4xx, 5xx HTTP коды, некорректный ответ и т.д.).
     * @property message Сообщение об ошибке, если доступно.
     */
    data class ServerError(val message: String?) : ErrorState()

    // TODO: Добавить другие типы ошибок
}