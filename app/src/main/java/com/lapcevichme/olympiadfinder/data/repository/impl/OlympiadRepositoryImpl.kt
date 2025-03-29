package com.lapcevichme.olympiadfinder.data.repository.impl

import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.Stage
import com.lapcevichme.olympiadfinder.domain.model.Subject
import com.lapcevichme.olympiadfinder.domain.repository.OlympiadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import javax.inject.Inject

// TODO: Replace with actual implementation
class OlympiadRepositoryImpl @Inject constructor(
    // Data Sources (ApiService, LocalDatabase)
) : OlympiadRepository {
    override fun getAllOlympiads(): Flow<List<Olympiad>> = flow {
        /*
         Заглушка данных
         (gemini <3)
        */
        val olympiads = listOf(
            Olympiad(
                id = 1L,
                name = "Математика Плюс",
                subjects = listOf(Subject("Математика")),
                minGrade = 7,
                maxGrade = 11,
                stages = listOf(
                    Stage("Отборочный", LocalDate.of(2025, 10, 1), LocalDate.of(2025, 11, 15)),
                    Stage("Заключительный", LocalDate.of(2026, 3, 1), null)
                ),
                link = "https://mathplus.ru",
                description = "Олимпиада по математике для школьников 7-11 классов.",
                keywords = "математика, олимпиада, школьники"
            ),
            Olympiad(
                id = 2L,
                name = "Русский Медвежонок",
                subjects = listOf(Subject("Русский язык")),
                minGrade = 1,
                maxGrade = 11,
                stages = listOf(
                    Stage("Основной тур", LocalDate.of(2025, 11, 15), null)
                ),
                link = "https://rm.ru",
                description = "Международная олимпиада по русскому языку.",
                keywords = "русский язык, олимпиада, медвежонок"
            )
        )
        emit(olympiads)
    }
}
