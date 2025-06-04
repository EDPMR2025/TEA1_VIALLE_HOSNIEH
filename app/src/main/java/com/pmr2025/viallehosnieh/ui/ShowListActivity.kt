package com.pmr2025.viallehosnieh.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pmr2025.viallehosnieh.R
import com.pmr2025.viallehosnieh.data.DataManager
import com.pmr2025.viallehosnieh.data.ItemAdapter
import com.pmr2025.viallehosnieh.data.ItemToDo
import com.pmr2025.viallehosnieh.data.ListeToDo

class ShowListActivity : TemplateActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadView(R.layout.activity_show_list, "Show list activity")

        val titreListe = intent.getStringExtra("titreListe") ?: ""
        val pseudo = intent.getStringExtra("pseudo") ?: ""

        val profil = DataManager.chargerProfil(this, pseudo)
        val liste = profil?.mesListeToDo?.find { it.titreListeToDo == titreListe }

        findViewById<TextView>(R.id.textViewTitreListe).text = titreListe

        if (profil != null) {
            val adapter = ItemAdapter(liste?.lesItems ?: listOf(), profil)
            val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewItems)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        }

        val buttonAddItem = findViewById<Button>(R.id.buttonAddItem)

        buttonAddItem.setOnClickListener {
            val editText = findViewById<EditText>(R.id.newItemEditText)
            val itemName = editText.text.toString().trim()
            if (itemName.isNotEmpty()) {
                val newItem = ItemToDo(description = itemName)
                if (liste != null) {
                    liste.ajouterItem(newItem)
                }
                if (profil != null) {
                    DataManager.sauvegarderProfil(this, profil)
                }

                val restartIntent = Intent(this, ShowListActivity::class.java)
                restartIntent.putExtra("pseudo", pseudo)
                restartIntent.putExtra("titreListe", titreListe)
                startActivity(restartIntent)
            }
        }

    }
}