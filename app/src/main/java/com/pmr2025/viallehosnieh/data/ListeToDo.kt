package com.pmr2025.viallehosnieh.data

import java.io.Serializable

data class ListeToDo(
    var titreListeToDo: String = ""
) : Serializable {

    var lesItems: MutableList<ItemToDo> = mutableListOf()

    override fun toString(): String = titreListeToDo

    fun ajouterItem(item: ItemToDo) {
        lesItems.add(item)
    }

    fun rechercherItem(description: String): ItemToDo? {
        return lesItems.find { it.description == description }
    }
}