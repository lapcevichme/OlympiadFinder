package com.lapcevichme.olympiadfinder.data.repository.impl

import android.util.Log
import com.lapcevichme.olympiadfinder.data.network.OlympiadApiService
import com.lapcevichme.olympiadfinder.data.network.model.toDomain
import com.lapcevichme.olympiadfinder.domain.model.Olympiad
import com.lapcevichme.olympiadfinder.domain.model.PaginatedResponse
import com.lapcevichme.olympiadfinder.domain.model.Resource
import com.lapcevichme.olympiadfinder.domain.model.Subject
import com.lapcevichme.olympiadfinder.domain.repository.OlympiadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class OlympiadRepositoryImpl @Inject constructor(
    private val olympiadApiService: OlympiadApiService
) : OlympiadRepository {

    companion object {
        private const val TAG = "OlympiadRepositoryImpl"
    }

    override fun getAllOlympiads(): Flow<Result<List<Olympiad>>> = flow {
        Log.d(TAG, "Fetching all olympiads...")
        try {
            val response = olympiadApiService.getAllOlympiads()
            if (response.isSuccessful) {
                val networkOlympiads = response.body() ?: emptyList()
                Log.i(TAG, "Successfully fetched ${networkOlympiads.size} all olympiads.")
                emit(Result.success(networkOlympiads.map { it.toDomain() }))
            } else {
                Log.e(
                    TAG,
                    "Failed to fetch all olympiads: HTTP ${response.code()} - ${response.message()}"
                )
                emit(Result.failure(HttpException(response)))
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error during getAllOlympiads: ${e.message}", e)
            emit(Result.failure(e))
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error during getAllOlympiads: ${e.message}", e)
            emit(Result.failure(e))
        }
    }.catch { e ->
        Log.e(TAG, "Exception during getAllOlympiads Flow: ${e.message}", e)
        emit(Result.failure(e))
    }

    override suspend fun getOlympiadById(id: Long): Resource<Olympiad> {
        Log.d(TAG, "Fetching olympiad by ID: $id")
        return try {
            val response = olympiadApiService.getOlympiadById(id)
            if (response.isSuccessful) {
                val olympiadDto = response.body()
                if (olympiadDto != null) {
                    Log.i(TAG, "Successfully fetched olympiad with ID: $id")
                    Resource.success(olympiadDto.toDomain())
                } else {
                    val errorMessage = "API returned success but olympiad data was null for ID: $id"
                    Log.e(TAG, errorMessage)
                    Resource.failure(IllegalStateException(errorMessage))
                }
            } else {
                Log.e(
                    TAG,
                    "Failed to fetch olympiad by ID $id: HTTP ${response.code()} - ${response.message()}"
                )
                Resource.failure(HttpException(response))
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error during getOlympiadById for ID $id: ${e.message}", e)
            Resource.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error during getOlympiadById for ID $id: ${e.message}", e)
            Resource.failure(e)
        }
    }


    override fun getPaginatedOlympiads(
        page: Int,
        pageSize: Int,
        query: String?,
        selectedGrades: List<Int>,
        selectedSubjects: List<Long>
    ): Flow<Resource<PaginatedResponse<Olympiad>>> = flow {
        Log.d(
            TAG,
            "Fetching paginated olympiads: page=$page, pageSize=$pageSize, query='$query', grades=$selectedGrades, subjects=$selectedSubjects"
        )
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
                    val domainItems = networkResponse.items.map { it.toDomain() }
                    val domainMeta = networkResponse.meta.toDomain()
                    Log.i(
                        TAG,
                        "Successfully fetched paginated olympiads. Page: ${domainMeta.currentPage}, Items: ${domainItems.size}"
                    )
                    emit(
                        Resource.success(
                            PaginatedResponse(
                                items = domainItems, meta = domainMeta
                            )
                        )
                    )
                } else {
                    val errorMessage =
                        "API returned success but data was null or incomplete for paginated olympiads."
                    Log.e(TAG, errorMessage)
                    emit(Resource.failure(IllegalStateException(errorMessage)))
                }
            } else {
                Log.e(
                    TAG,
                    "Failed to fetch paginated olympiads: HTTP ${response.code()} - ${response.message()}"
                )
                emit(Resource.failure(HttpException(response)))
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error during getPaginatedOlympiads: ${e.message}", e)
            emit(Resource.failure(e))
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error during getPaginatedOlympiads: ${e.message}", e)
            emit(Resource.failure(e))
        }
    }

    override suspend fun getAvailableSubjects(): Resource<List<Subject>> {
        Log.d(TAG, "Fetching available subjects...")
        return try {
            val response = olympiadApiService.getSubjects()
            if (response.isSuccessful) {
                val subjectsDto = response.body()
                if (subjectsDto != null) {
                    Log.i(TAG, "Successfully fetched ${subjectsDto.size} available subjects.")
                    Resource.success(subjectsDto.map { it.toDomain() })
                } else {
                    val errorMessage = "API returned success but subjects data was null."
                    Log.e(TAG, errorMessage)
                    Resource.failure(IllegalStateException(errorMessage))
                }
            } else {
                Log.e(
                    TAG,
                    "Failed to fetch available subjects: HTTP ${response.code()} - ${response.message()}"
                )
                Resource.failure(HttpException(response))
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error during getAvailableSubjects: ${e.message}", e)
            Resource.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error during getAvailableSubjects: ${e.message}", e)
            Resource.failure(e)
        }
    }
}
