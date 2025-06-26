package com.pmr2025.viallehosnieh.data

data class ItemToDo(
    var titre: String = "",
    var checked: Boolean = false,
    val id : Int = 0,
    val url: String ?= ""
)