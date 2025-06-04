package com.pmr2025.viallehosnieh.data

import java.io.Serializable

data class ProfilListeToDo(
    val login: String,  // identifiant unique de l’utilisateur
    var mesListeToDo: MutableList<ListeToDo> = mutableListOf()
) : Serializable {

    override fun toString(): String = login

    fun ajouterListe(liste: ListeToDo) {
        mesListeToDo.add(liste)
    }
}