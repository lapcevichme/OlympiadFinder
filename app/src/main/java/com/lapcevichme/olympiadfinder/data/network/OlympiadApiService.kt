package com.lapcevichme.olympiadfinder.data.network

import com.lapcevichme.olympiadfinder.data.network.model.NetworkOlympiad
import com.lapcevichme.olympiadfinder.data.network.model.NetworkPaginatedResponse
import com.lapcevichme.olympiadfinder.data.network.model.NetworkSubject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Интерфейс Retrofit для взаимодействия с API олимпиад.
 * Определяет эндпоинты для получения данных об олимпиадах и предметах.
 */
interface OlympiadApiService {

    /**
     * Получает список всех олимпиад.
     *
     * @return [Response] с телом, содержащим список [NetworkOlympiad].
     */
    @GET("/api/olympiads")
    suspend fun getAllOlympiads(): Response<List<NetworkOlympiad>>

    /**
     * Получает олимпиаду по ее уникальному идентификатору.
     *
     * @param id Уникальный идентификатор олимпиады.
     * @return [Response] с телом, содержащим [NetworkOlympiad].
     */
    @GET("/api/olympiads/{id}")
    suspend fun getOlympiadById(@Path("id") id: Long): Response<NetworkOlympiad>

    /**
     * Получает пагинированный список олимпиад с возможностью фильтрации.
     *
     * @param page Номер страницы для запроса.
     * @param pageSize Количество элементов на странице.
     * @param query Поисковый запрос (опционально).
     * @param grade Список классов для фильтрации (опционально).
     * @param subject Список ID предметов для фильтрации (опционально).
     * @return [Response] с телом, содержащим [NetworkPaginatedResponse] с списком [NetworkOlympiad] и метаданными пагинации.
     */
    @GET("/api/olympiads")
    suspend fun getPaginatedOlympiads(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("query") query: String? = null,
        @Query("grade") grade: List<Int>? = null,
        @Query("subject") subject: List<Long>? = null
    ): Response<NetworkPaginatedResponse<NetworkOlympiad>>

    /**
     * Получает список всех доступных предметов.
     *
     * @return [Response] с телом, содержащим список [NetworkSubject].
     */
    @GET("/api/subjects")
    suspend fun getSubjects(): Response<List<NetworkSubject>>
}