package com.pmr2025.viallehosnieh.data

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.pmr2025.viallehosnieh.data.ItemToDo
import com.pmr2025.viallehosnieh.data.ProfilListeToDo
import com.pmr2025.viallehosnieh.R

class ItemAdapter(
    private val items: List<ItemToDo>,
    private val profilActuel: ProfilListeToDo
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

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
        holder.checkBox.text = item.description
        holder.checkBox.isChecked = item.fait

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            item.fait = isChecked
            DataManager.sauvegarderProfil(holder.checkBox.context, profilActuel)
        }

        holder.iconEdit.setOnClickListener {
            val editText = EditText(holder.itemView.context)
            editText.setText(item.description)

            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Modifier la tâche")
                .setView(editText)
                .setPositiveButton("Valider") { _, _ ->
                    val nouvelleDescription = editText.text.toString().trim()
                    if (nouvelleDescription.isNotEmpty()) {
                        item.description = nouvelleDescription
                        notifyItemChanged(position)
                        DataManager.sauvegarderProfil(holder.itemView.context, profilActuel)
                    } else {
                        Toast.makeText(holder.itemView.context, "Description vide", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Annuler", null)
                .show()
        }
    }

    override fun getItemCount(): Int = items.size
}