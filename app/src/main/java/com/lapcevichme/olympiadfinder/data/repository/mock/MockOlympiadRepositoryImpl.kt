package com.lapcevichme.olympiadfinder.data.repository.mock

import com.lapcevichme.olympiadfinder.data.di.MockOlympiadRepository
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.Stage
import com.lapcevichme.olympiadfinder.domain.model.Subject
import com.lapcevichme.olympiadfinder.domain.repository.OlympiadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import javax.inject.Inject

@MockOlympiadRepository
class MockOlympiadRepositoryImpl @Inject constructor() : OlympiadRepository {
    override fun getAllOlympiads(): Flow<List<Olympiad>> = flow {
        val mockOlympiads = listOf(
            Olympiad(
                id = 1L,
                name = "Фейковая Олимпиада 1",
                subjects = listOf(Subject("Математика")),
                minGrade = 7,
                maxGrade = 11,
                stages = listOf(Stage("Фейковый Этап 1", LocalDate.now(), null)),
                link = "fake://olympiad1",
                description = "Это фейковая олимпиада 1.",
                keywords = "фейк, математика"
            ),
            Olympiad(
                id = 2L,
                name = "Фейковая Олимпиада 2",
                subjects = listOf(Subject("Русский язык")),
                minGrade = 1,
                maxGrade = 5,
                stages = listOf(Stage("Фейковый Этап 2", LocalDate.now().plusDays(7), null)),
                link = "fake://olympiad2",
                description = "Это фейковая олимпиада 2.",
                keywords = "фейк, русский"
            )
        )
        emit(mockOlympiads)
    }
}