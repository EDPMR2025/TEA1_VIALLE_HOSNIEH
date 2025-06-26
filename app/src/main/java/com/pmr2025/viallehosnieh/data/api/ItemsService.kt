package com.pmr2025.viallehosnieh.data.api

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ItemsService {
    @GET("lists/{id}/items")
    suspend fun getItems(@Header("hash") hash: String, @Path("id") listId: Int): ItemsResponse

    @POST("lists/{id}/items")
    suspend fun addItem(@Header("hash") hash: String, @Path("id") listId: Int, @Query("label") label: String, @Query("url") url : String = ""): AddItemResponse

    @PUT("lists/{listId}/items/{itemId}")
    suspend fun updateItemChecked(@Header("hash") hash: String, @Path("listId") listId: Int, @Path("itemId") itemId: Int, @Query("check") checked: String): AddItemResponse

    @PUT("lists/{listId}/items/{itemId}")
    suspend fun updateItemName(@Header("hash") hash: String, @Path("listId") listId: Int, @Path("itemId") itemId: Int, @Query("label") label: String) : AddItemResponse

    @DELETE("lists/{listId}/items/{itemId}")
    suspend fun deleteItem(@Header("hash") hash: String, @Path("listId") listId: Int, @Path("itemId") itemId: Int): Response<Unit>
}