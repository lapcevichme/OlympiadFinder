package com.lapcevichme.olympiadfinder.data.network

import com.lapcevichme.olympiadfinder.data.network.model.NetworkOlympiad
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface OlympiadApiService {
    @GET("/api/olympiads")
    suspend fun getAllOlympiads(): Response<List<NetworkOlympiad>>

    @GET("/api/olympiads/{id}")
    suspend fun getOlympiadById(@Path("id") id: Long): Response<NetworkOlympiad>
}
