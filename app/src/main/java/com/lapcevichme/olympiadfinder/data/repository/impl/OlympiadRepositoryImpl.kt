package com.lapcevichme.olympiadfinder.data.repository.impl

import com.lapcevichme.olympiadfinder.data.network.OlympiadApiService
import com.lapcevichme.olympiadfinder.data.network.model.NetworkOlympiad
import com.lapcevichme.olympiadfinder.data.network.model.NetworkStage
import com.lapcevichme.olympiadfinder.data.network.model.toDomain
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.PaginatedResponse
import com.lapcevichme.olympiadfinder.domain.model.Stage
import com.lapcevichme.olympiadfinder.domain.model.Subject
import com.lapcevichme.olympiadfinder.domain.repository.OlympiadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class OlympiadRepositoryImpl @Inject constructor(
    private val olympiadApiService: OlympiadApiService
) : OlympiadRepository {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun getAllOlympiads(): Flow<Result<List<Olympiad>>> = flow {
        val response = olympiadApiService.getAllOlympiads()
        if (response.isSuccessful) {
            val networkOlympiads = response.body() ?: emptyList()
            // Эмитим Result.success при успешном ответе
            emit(Result.success(networkOlympiads.map { it.toDomain() }))
        } else {
            // Эмитим Result.failure при ошибке HTTP
            emit(Result.failure(HttpException(response)))
        }
    }.catch { e ->
        println("OlympiadRepositoryImpl: Exception during getAllOlympiads Flow: ${e.message}")
        // Эмитим Result.failure при исключении в Flow
        emit(Result.failure(e))
    }


    override fun getOlympiads(page: Int, pageSize: Int, query: String?, selectedGrades: List<Int>): Flow<Result<PaginatedResponse<Olympiad>>> = flow {
        val response = olympiadApiService.getPaginatedOlympiads(
            page = page, pageSize = pageSize, query = query, grade = if (selectedGrades.isEmpty()) null else selectedGrades
        )

        if (response.isSuccessful) {
            val networkResponse = response.body()
            if (networkResponse != null && networkResponse.items != null && networkResponse.meta != null) {
                val domainItems = networkResponse.items.map { it.toDomain() } // Используем маппинг NetworkOlympiad -> Olympiad
                val domainMeta = networkResponse.meta.toDomain() // Используем маппинг метаданных
                // Эмитим Result.success при успешном ответе с корректными данными
                emit(Result.success(PaginatedResponse(items = domainItems, meta = domainMeta)))
            } else {
                // Если ответ успешный, но данные внутри null, эмитим Result.failure
                emit(Result.failure(IllegalStateException("API returned success but data was null or incomplete")))
            }
        } else {
            // Эмитим Result.failure при ошибке HTTP
            emit(Result.failure(HttpException(response)))
        }
    }.catch { e ->
        println("OlympiadRepositoryImpl: Exception during getOlympiads Flow: ${e.message}")
        // Эмитим Result.failure при исключении в Flow (например, IOException)
        emit(Result.failure(e))
    }




    // Extension function для маппинга NetworkOlympiad в Domain Olympiad
    private fun NetworkOlympiad.toDomain(): Olympiad {
        return Olympiad(
            id = id,
            name = name,
            subjects = subjects?.map { Subject(it.name) } ?: emptyList(),
            minGrade = minGrade,
            maxGrade = maxGrade,
            stages = stages?.map { it.toDomain() } ?: emptyList(),
            link = link,
            description = description,
            keywords = keywords
        )
    }

    // Extension function для маппинга NetworkStage в Domain Stage
    private fun NetworkStage.toDomain(): Stage {
        return Stage(
            name = name,
            startDate = startDate?.let { LocalDate.parse(it, dateFormatter) },
            endDate = endDate?.let { LocalDate.parse(it, dateFormatter) }
        )
    }
}
