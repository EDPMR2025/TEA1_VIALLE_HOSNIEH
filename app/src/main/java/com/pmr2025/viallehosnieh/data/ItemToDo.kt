package com.pmr2025.viallehosnieh.data

import java.io.Serializable

data class ItemToDo(
    var description: String = "",
    var fait: Boolean = false
) : Serializable {
    override fun toString(): String {
        return "$description (${if (fait) "fait" else "à faire"})"
    }
}