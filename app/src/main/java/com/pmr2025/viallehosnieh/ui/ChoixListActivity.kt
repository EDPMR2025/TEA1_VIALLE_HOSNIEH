package com.pmr2025.viallehosnieh.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pmr2025.viallehosnieh.data.ListAdapter
import com.pmr2025.viallehosnieh.R
import com.pmr2025.viallehosnieh.data.DataProvider
import kotlinx.coroutines.launch

class ChoixListActivity : TemplateActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadView(R.layout.activity_choix_list, "Choix list activity")

        val hash = intent.getStringExtra("hash") ?: ""

        val boutonAjouterListe = findViewById<Button>(R.id.buttonAjouterListe)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewListes)

        val dataProvider = DataProvider(this.application)

        lifecycleScope.launch{
            val result = dataProvider.getLists(hash)
            when(result){
                DataProvider.ResultList.Failure -> {
                    Toast.makeText(this@ChoixListActivity, "Erreur de chargement des listes", Toast.LENGTH_SHORT).show()
                }
                is DataProvider.ResultList.Success -> {
                    val adapter = ListAdapter(
                        result.lists,
                        this@ChoixListActivity,
                        hash,
                        dataProvider)
                    recyclerView.layoutManager = LinearLayoutManager(this@ChoixListActivity)
                    recyclerView.adapter = adapter
                }
            }
        }

        boutonAjouterListe.setOnClickListener {
            val editText = findViewById<EditText>(R.id.newListEditText)
            val nomListe = editText.text.toString().trim()
            if (nomListe.isNotEmpty()) {
                lifecycleScope.launch{
                    val result = dataProvider.addList(hash, nomListe)
                    when(result) {
                        DataProvider.ResultList.Failure -> {
                            Toast.makeText(
                                this@ChoixListActivity,
                                "Erreur d'ajout de la liste",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is DataProvider.ResultList.Success -> {
                            val nouvelleListe = result.lists[0]
                            val adapter = recyclerView.adapter as? ListAdapter
                            adapter?.ajouterListe(nouvelleListe)
                        }
                    }
                }
            }
        }
    }
}