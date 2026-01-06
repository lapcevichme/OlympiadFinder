package com.lapcevichme.olympiadfinder.data.network.model

import com.lapcevichme.olympiadfinder.domain.model.Subject

/**
 * Data-класс, представляющий сетевой ответ для сущности [Subject].
 * Используется для десериализации данных о предметах из сетевых запросов.
 */
data class NetworkSubject(
    val id: Long,
    val name: String
)

/**
 * Расширяющая функция для преобразования [NetworkSubject] в доменную модель [Subject].
 *
 * @return Доменная модель [Subject], представляющая данный сетевой предмет.
 */
fun NetworkSubject.toDomain(): Subject {
    return Subject(
        id = this.id,
        name = this.name
    )
}