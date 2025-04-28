package com.lapcevichme.olympiadfinder.presentation.viewmodel

sealed class ErrorState {
    // Состояние, когда нет активной ошибки
    data object NoError : ErrorState()

    // Состояние ошибки, связанной с сетью (нет подключения, таймаут и т.д.)
    // Не содержит сообщения, так как сообщение стандартное
    data object NetworkError : ErrorState()

    // Состояние ошибки, связанной с сервером (4xx, 5xx HTTP коды, некорректный ответ и т.д.)
    // Может содержать сообщение с деталями от сервера или исключения
    data class ServerError(val message: String?) : ErrorState()

    // TODO: Добавить другие типы ошибок
}
