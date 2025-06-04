package com.pmr2025.viallehosnieh.data

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.pmr2025.viallehosnieh.data.ListeToDo
import com.pmr2025.viallehosnieh.data.ProfilListeToDo
import com.pmr2025.viallehosnieh.R
import com.pmr2025.viallehosnieh.ui.ShowListActivity

class ListAdapter(
    private val listes: MutableList<ListeToDo>,
    private val profil: ProfilListeToDo,
    private val context: Context,
    private val login: String
) : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titreListe: TextView = itemView.findViewById(R.id.textViewTitreListe)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_liste, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val liste = listes[position]
        holder.titreListe.text = liste.titreListeToDo

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ShowListActivity::class.java)
            intent.putExtra("titreListe", liste.titreListeToDo)
            intent.putExtra("pseudo", login)
            context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(context)
                .setTitle("Supprimer la liste ?")
                .setMessage("Souhaitez-vous supprimer '${liste.titreListeToDo}' ?")
                .setPositiveButton("Oui") { _, _ ->
                    listes.removeAt(position)
                    profil.mesListeToDo = listes
                    DataManager.sauvegarderProfil(context, profil)
                    notifyItemRemoved(position)
                    Toast.makeText(context, "Liste supprimée", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Annuler", null)
                .show()
            true
        }
    }

    override fun getItemCount(): Int = listes.size
}