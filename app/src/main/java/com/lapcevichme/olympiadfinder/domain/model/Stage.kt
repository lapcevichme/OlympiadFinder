package com.lapcevichme.olympiadfinder.domain.model

import java.time.LocalDate

/**
 * Data-класс, представляющий доменную модель для этапа олимпиады.
 * Используется в бизнес-логике приложения.
 */
data class Stage(
    val name: String,
    val startDate: LocalDate?,
    val endDate: LocalDate?
)
