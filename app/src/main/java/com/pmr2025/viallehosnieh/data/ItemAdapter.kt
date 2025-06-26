package com.pmr2025.viallehosnieh.data

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.pmr2025.viallehosnieh.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemAdapter(
    private val itemsInitial: List<ItemToDo>,
    private val dataProvider: DataProvider,
    private val context: Context,
    private val hash: String,
    private val listId: Int
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private val items: MutableList<ItemToDo> = itemsInitial.toMutableList()

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
        val iconEdit: ImageView = itemView.findViewById(R.id.iconEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.checkBox.text = item.titre
        holder.checkBox.isChecked = item.checked

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            item.checked = isChecked
            CoroutineScope(Dispatchers.Main).launch{
                val result = dataProvider.updateItemChecked(hash, listId, item.id, if (isChecked) "1" else "0")
                when (result){
                    DataProvider.ResultItem.Failure -> {
                        Toast.makeText(context, "Erreur lors de la mise à jour de la tâche", Toast.LENGTH_SHORT).show()
                    }
                    is DataProvider.ResultItem.Success -> {
                        notifyItemChanged(position)
                    }
                }
            }
        }

        holder.iconEdit.setOnClickListener {
            val editText = EditText(holder.itemView.context)
            editText.setText(item.titre)
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Modifier la tâche")
                .setView(editText)
                .setPositiveButton("Valider") { _, _ ->
                    val nouvelleDescription = editText.text.toString().trim()
                    if (nouvelleDescription.isNotEmpty()) {
                        item.titre = nouvelleDescription
                        CoroutineScope(Dispatchers.Main).launch{
                            val result = dataProvider.updateItemName(hash, listId, item.id, nouvelleDescription)
                            when (result) {
                                DataProvider.ResultItem.Failure -> {
                                    Toast.makeText(
                                        context,
                                        "Erreur lors de la mise à jour de la tâche",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                is DataProvider.ResultItem.Success -> {
                                    notifyItemChanged(position)
                                    Toast.makeText(context, "Item modifié", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                    } else {
                        Toast.makeText(holder.itemView.context, "Description vide", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Annuler", null)
                .show()
        }
    }

    fun ajouterItem(nouvelItem: ItemToDo) {
        items.add(nouvelItem)
        notifyItemInserted(items.size - 1)
    }

    override fun getItemCount(): Int = items.size
}