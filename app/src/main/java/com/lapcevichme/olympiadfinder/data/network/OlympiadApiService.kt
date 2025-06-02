package com.lapcevichme.olympiadfinder.data.network

import com.lapcevichme.olympiadfinder.data.network.model.NetworkOlympiad
import com.lapcevichme.olympiadfinder.data.network.model.NetworkPaginatedResponse
import com.lapcevichme.olympiadfinder.data.network.model.NetworkSubject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface OlympiadApiService {
    @GET("/api/olympiads")
    suspend fun getAllOlympiads(): Response<List<NetworkOlympiad>>

    @GET("/api/olympiads/{id}")
    suspend fun getOlympiadById(@Path("id") id: Long): Response<NetworkOlympiad>

    @GET("/api/olympiads")
    suspend fun getPaginatedOlympiads(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("query") query: String? = null,
        @Query("grade") grade: List<Int>? = null,
        @Query("subject") subject: List<Long>? = null
    ): Response<NetworkPaginatedResponse<NetworkOlympiad>>

    @GET("/api/subjects")
    suspend fun getSubjects(): Response<List<NetworkSubject>>
}
