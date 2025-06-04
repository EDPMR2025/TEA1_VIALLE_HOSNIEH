package com.pmr2025.viallehosnieh.data

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.pmr2025.viallehosnieh.data.ProfilListeToDo

object DataManager {
    fun sauvegarderProfil(context: Context, profil: ProfilListeToDo) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(profil)
        editor.putString("profil_${profil.login}", json)
        editor.apply()
    }

    fun chargerProfil(context: Context, pseudo: String): ProfilListeToDo? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val gson = Gson()
        val json = prefs.getString("profil_$pseudo", null)
        return if (json != null) gson.fromJson(json, ProfilListeToDo::class.java) else null
    }
}