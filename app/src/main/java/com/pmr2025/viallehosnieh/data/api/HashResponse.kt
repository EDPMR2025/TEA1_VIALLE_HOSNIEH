package com.pmr2025.viallehosnieh.data.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HashResponse(
    val hash : String
)