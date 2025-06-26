package com.pmr2025.viallehosnieh.data.api

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ListsService {
    @GET("lists")
    suspend fun getLists(@Header("hash") hash : String) : ListsResponse

    @GET("lists/{id}")
    suspend fun getListInfos(@Header("hash") hash : String, @Path("id") id : Int) : AddListResponse

    @POST("lists")
    suspend fun addList(@Header("hash") hash : String, @Query("label") label : String) : AddListResponse

    @DELETE("lists/{id}")
    suspend fun deleteList(@Header("hash") hash : String, @Path("id") id : Int) : Response<Unit>
}