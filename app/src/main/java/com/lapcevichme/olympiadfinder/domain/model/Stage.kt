package com.lapcevichme.olympiadfinder.domain.model

import java.time.LocalDate

data class Stage(
    val name: String,
    val startDate: LocalDate?,
    val endDate: LocalDate?
)
