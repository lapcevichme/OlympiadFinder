package com.lapcevichme.olympiadfinder.data.repository.impl

import com.lapcevichme.olympiadfinder.data.network.OlympiadApiService
import com.lapcevichme.olympiadfinder.data.network.model.NetworkOlympiad
import com.lapcevichme.olympiadfinder.data.network.model.NetworkStage
import com.lapcevichme.olympiadfinder.data.network.model.NetworkSubject
import com.lapcevichme.olympiadfinder.data.network.model.toDomain
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.PaginatedResponse
import com.lapcevichme.olympiadfinder.domain.model.Resource
import com.lapcevichme.olympiadfinder.domain.model.Stage
import com.lapcevichme.olympiadfinder.domain.model.Subject
import com.lapcevichme.olympiadfinder.domain.repository.OlympiadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class OlympiadRepositoryImpl @Inject constructor(
    private val olympiadApiService: OlympiadApiService
) : OlympiadRepository {

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


    override fun getOlympiads(
        page: Int,
        pageSize: Int,
        query: String?,
        selectedGrades: List<Int>,
        selectedSubjects: List<Long>
    ): Flow<Resource<PaginatedResponse<Olympiad>>> = flow {
        try {
            val response = olympiadApiService.getPaginatedOlympiads(
                page = page,
                pageSize = pageSize,
                query = query,
                grade = if (selectedGrades.isEmpty()) null else selectedGrades,
                subject = if (selectedSubjects.isEmpty()) null else selectedSubjects
            )

            if (response.isSuccessful) {
                val networkResponse = response.body()
                if (networkResponse != null && networkResponse.items != null && networkResponse.meta != null) {
                    val domainItems =
                        networkResponse.items.map { it.toDomain() }

                    val domainMeta =
                        networkResponse.meta.toDomain()

                    emit(
                        Resource.success(
                            PaginatedResponse(
                                items = domainItems,
                                meta = domainMeta
                            )
                        )
                    )
                } else {
                    // Если ответ успешный, но данные внутри null, эмитим Resource.failure
                    emit(Resource.failure(IllegalStateException("API returned success but data was null or incomplete")))
                }
            } else {
                // Эмитим Resource.failure при ошибке HTTP
                emit(Resource.failure(HttpException(response)))
            }
        } catch (e: IOException) {
            println("OlympiadRepositoryImpl: IOException during getPaginatedOlympiads Flow: ${e.message}")
            // Эмитим Resource.failure при исключении в Flow (например, IOException)
            emit(Resource.failure(e))
        } catch (e: Exception) { // Ловим другие исключения
            println("OlympiadRepositoryImpl: Exception during getPaginatedOlympiads Flow: ${e.message}")
            emit(Resource.failure(e)) // Неизвестная ошибка
        }
    }

    override suspend fun getAvailableSubjects(): Resource<List<Subject>> {
        return try {
            val response = olympiadApiService.getSubjects() // Response (suspend вызов)
            if (response.isSuccessful) {
                val subjectsDto = response.body() // Тело ответа (список SubjectDto)
                if (subjectsDto != null) {
                    val domainSubjects =
                        subjectsDto.map { it.toDomain() }
                    Resource.success(domainSubjects) // Успех, возвращаем Result.success
                } else {
                    // Обработка случая, когда тело ответа null при успешном статусе
                    Resource.failure(IllegalStateException("API returned success but subjects data was null")) // Возвращаем Result.failure
                }
            } else {
                // Обработка ошибок HTTP (не 2xx статус коды)
                Resource.failure(HttpException(response)) // Возвращаем Result.failure с HttpException
            }
        } catch (e: IOException) {
            println("OlympiadRepositoryImpl: IOException during getAvailableSubjects: ${e.message}")
            Resource.failure(e) // Ошибка сети, возвращаем Result.failure
        } catch (e: Exception) { // Ловим другие исключения
            println("OlympiadRepositoryImpl: Exception during getAvailableSubjects: ${e.message}")
            Resource.failure(e) // Неизвестная ошибка, возвращаем Result.failure
        }
    }
}
