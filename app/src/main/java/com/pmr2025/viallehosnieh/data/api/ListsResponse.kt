package com.pmr2025.viallehosnieh.data.api

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ListsResponse(
    val lists: List<ListResponse>
)

@JsonClass(generateAdapter = true)
data class AddListResponse(
    val list: ListResponse
)

@JsonClass(generateAdapter = true)
data class ListResponse(
    val id: Int,
    val label: String
)