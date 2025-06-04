package com.pmr2025.viallehosnieh.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pmr2025.viallehosnieh.data.ItemToDo
import com.pmr2025.viallehosnieh.data.ListAdapter
import com.pmr2025.viallehosnieh.data.ListeToDo
import com.pmr2025.viallehosnieh.data.ProfilListeToDo
import com.pmr2025.viallehosnieh.R
import com.pmr2025.viallehosnieh.data.DataManager

class ChoixListActivity : TemplateActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadView(R.layout.activity_choix_list, "Choix list activity")

        val login = intent.getStringExtra("pseudo") ?: ""
        var profil = DataManager.chargerProfil(this, login)

        if (profil == null) {
            profil = initializeProfil(profil, login)
        }

        val textView = findViewById<TextView>(R.id.textViewBienvenue)
        textView.text = "Bienvenue, $login"

        val boutonAjouterListe = findViewById<Button>(R.id.buttonAjouterListe)

        boutonAjouterListe.setOnClickListener {
            val editText = findViewById<EditText>(R.id.newListEditText)
            val nomListe = editText.text.toString().trim()
            if (nomListe.isNotEmpty()) {
                val nouvelleListe = ListeToDo(titreListeToDo = nomListe)
                profil!!.ajouterListe(nouvelleListe)
                DataManager.sauvegarderProfil(this, profil)

                val restartIntent = Intent(this, ChoixListActivity::class.java)
                restartIntent.putExtra("pseudo", login)
                startActivity(restartIntent)
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewListes)
        profil?.let {
            val adapter = ListAdapter(
                it.mesListeToDo,
                it,
                this,
                login
            )
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        }
    }

    private fun initializeProfil(
        profil: ProfilListeToDo?,
        login: String
    ): ProfilListeToDo? {
        var profil1 = profil
        profil1 = ProfilListeToDo(login)

        val listeCourses = ListeToDo(titreListeToDo = "Courses")
        listeCourses.ajouterItem(ItemToDo("Acheter du pain"))
        listeCourses.ajouterItem(ItemToDo("Prendre des œufs"))
        listeCourses.ajouterItem(ItemToDo("Acheter du lait"))

        val listePMR = ListeToDo(titreListeToDo = "Projet PMR")
        listePMR.ajouterItem(ItemToDo("Finir le diagramme UML"))
        listePMR.ajouterItem(ItemToDo("Envoyer le rapport"))
        listePMR.ajouterItem(ItemToDo("Préparer la démo"))

        profil1.ajouterListe(listeCourses)
        profil1.ajouterListe(listePMR)

        DataManager.sauvegarderProfil(this, profil1)
        Log.d("ChoixList", "Nouveau profil créé et sauvegardé")
        return profil1
    }
}