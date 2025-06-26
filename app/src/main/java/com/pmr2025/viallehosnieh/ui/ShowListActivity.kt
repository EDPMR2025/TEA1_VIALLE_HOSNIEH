package com.pmr2025.viallehosnieh.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pmr2025.viallehosnieh.R
import com.pmr2025.viallehosnieh.data.DataProvider
import com.pmr2025.viallehosnieh.data.ItemAdapter
import kotlinx.coroutines.launch

class ShowListActivity : TemplateActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadView(R.layout.activity_show_list, "Show list activity")

        val hash = intent.getStringExtra("hash") ?: ""
        val listId = intent.getIntExtra("idList", -1)


        val textViewTitreListe = findViewById<TextView>(R.id.textViewTitreListe)
        val buttonAddItem = findViewById<Button>(R.id.buttonAddItem)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewItems)

        val dataProvider = DataProvider(this.application)

        lifecycleScope.launch{
            // On récupère les items de la liste
            val result = dataProvider.getItems(hash, listId)
            when (result){
                DataProvider.ResultItem.Failure -> {
                    Toast.makeText(this@ShowListActivity, "Erreur de chargement des items", Toast.LENGTH_SHORT).show()
                }
                is DataProvider.ResultItem.Success -> {
                    val items = result.items
                    val adapter = ItemAdapter(items, dataProvider, this@ShowListActivity, hash, listId)
                    recyclerView.layoutManager = LinearLayoutManager(this@ShowListActivity)
                    recyclerView.adapter = adapter
                }
            }
            // On récupère le nom de la liste pour l'afficher
            val result2 = dataProvider.getListInfos(hash, listId)
            when (result2){
                DataProvider.ResultList.Failure -> {
                    Toast.makeText(this@ShowListActivity, "Erreur de chargement de la liste", Toast.LENGTH_SHORT).show()
                }
                is DataProvider.ResultList.Success -> {
                    val lists = result2.lists
                    textViewTitreListe.text = lists[0].titre
                }
            }

        }

        buttonAddItem.setOnClickListener {
            val editText = findViewById<EditText>(R.id.newItemEditText)
            val itemName = editText.text.toString().trim()
            if (itemName.isNotEmpty()) {
                lifecycleScope.launch {
                    val result = dataProvider.addItem(hash, listId, itemName)
                    when (result){
                        DataProvider.ResultItem.Failure -> {
                            Toast.makeText(this@ShowListActivity, "Erreur d'ajout de l'item", Toast.LENGTH_SHORT).show()
                        }
                        is DataProvider.ResultItem.Success -> {
                            val newItem = result.items[0]
                            val adapter = recyclerView.adapter as? ItemAdapter
                            adapter?.ajouterItem(newItem)
                        }
                    }
                }
            }
        }

    }
}