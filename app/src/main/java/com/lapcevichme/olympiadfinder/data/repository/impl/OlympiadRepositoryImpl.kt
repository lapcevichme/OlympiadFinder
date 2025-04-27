package com.lapcevichme.olympiadfinder.data.repository.impl

import com.lapcevichme.olympiadfinder.data.network.OlympiadApiService
import com.lapcevichme.olympiadfinder.data.network.model.NetworkOlympiad
import com.lapcevichme.olympiadfinder.data.network.model.NetworkStage
import com.lapcevichme.olympiadfinder.data.network.model.toDomain
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.PaginatedResponse
import com.lapcevichme.olympiadfinder.domain.model.PaginationMetadata
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

    override fun getAllOlympiads(): Flow<List<Olympiad>> = flow {
        val response = olympiadApiService.getAllOlympiads()
        if (response.isSuccessful) {
            val networkOlympiads = response.body() ?: emptyList()
            emit(networkOlympiads.map { it.toDomain() })
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = "Ошибка при загрузке всех олимпиад: ${response.code()} - ${response.message()}"
            println(errorMessage)
            throw HttpException(response)
        }
    }.catch { e ->
        println("OlympiadRepositoryImpl: Exception during getAllOlympiads Flow: ${e.message}")
        emit(emptyList())
        // Можно также перебросить исключение: throw e
    }


    override fun getOlympiads(page: Int, pageSize: Int, query: String?, selectedGrades: List<Int>): Flow<PaginatedResponse<Olympiad>> = flow {
        // Выполняем сетевой запрос
        val response = olympiadApiService.getPaginatedOlympiads(
            page = page,
            pageSize = pageSize,
            query = query,
            grade = if (selectedGrades.isEmpty()) null else selectedGrades
        )

        if (response.isSuccessful) {
            val networkResponse = response.body() // Получаем NetworkPaginatedResponse (теперь с meta)
            // Проверяем, что тело ответа и его содержимое не null
            if (networkResponse?.meta != null) { // <-- Проверяем на null и items, и meta
                // Маппим список NetworkOlympiad -> List<Olympiad>
                val domainItems = networkResponse.items.map { it.toDomain() }

                val domainMeta = networkResponse.meta.toDomain()

                // Эмитируем доменную PaginatedResponse<Olympiad>
                emit(PaginatedResponse(items = domainItems, meta = domainMeta))
            } else {
                println("OlympiadRepositoryImpl: Received successful response but body, items, or meta were null/empty.")
                // Т.к. нет сетевых метаданных, создаем доменные на основе запроса
                emit(PaginatedResponse(emptyList(), PaginationMetadata(0, 1, page, pageSize)))
            }
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = "Ошибка при загрузке олимпиад (пагинация/поиск/фильтры): ${response.code()} - ${response.message()} Error body: $errorBody"
            println(errorMessage)
            throw HttpException(response)
        }
    }.catch { e ->
        println("OlympiadRepositoryImpl: Exception during getOlympiads Flow: ${e.message}")
        // В случае ошибки Flow, эмитируем пустой ответ с метаданными запроса
        emit(PaginatedResponse(emptyList(), PaginationMetadata(0, 1, page, pageSize)))
        // throw e
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
