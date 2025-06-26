package com.pmr2025.viallehosnieh.data.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ItemsResponse(
    val items: List<ItemResponse>
)

@JsonClass(generateAdapter = true)
data class AddItemResponse(
    val item: ItemResponse
)

@JsonClass(generateAdapter = true)
data class ItemResponse(
    val id: Int,
    val label: String,
    val url : String = "",
    val checked : String
)