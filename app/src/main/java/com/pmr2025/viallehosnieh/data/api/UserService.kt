package com.pmr2025.viallehosnieh.data.api

import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {
    @POST("authenticate")
    suspend fun authenticate(@Query("user") user: String,
                             @Query("password") password: String): HashResponse

    @POST("users")
    suspend fun createUser(@Query("pseudo") pseudo: String,
                           @Query("pass") password: String,
                           @Header("hash") hash: String): Void
}