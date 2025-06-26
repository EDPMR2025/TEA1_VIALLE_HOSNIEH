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
import com.pmr2025.viallehosnieh.R
import com.pmr2025.viallehosnieh.ui.ShowListActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListAdapter(
    private val listesInitiales: List<ListeToDo>,
    private val context: Context,
    private val hash: String,
    private val dataProvider: DataProvider,
) : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    // Va permettre de mettre à jour dynamiquement la vue
    private val listes: MutableList<ListeToDo> = listesInitiales.toMutableList()

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
        holder.titreListe.text = liste.titre

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ShowListActivity::class.java)
            intent.putExtra("idList", liste.id)
            intent.putExtra("hash", hash)
            context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(context)
                .setTitle("Supprimer la liste ?")
                .setMessage("Souhaitez-vous supprimer '${liste.titre}' ?")
                .setPositiveButton("Oui") { _, _ ->
                    CoroutineScope(Dispatchers.Main).launch{
                        val result = dataProvider.deleteList(hash, liste.id)
                        when (result){
                            DataProvider.ResultList.Failure -> {
                                Toast.makeText(context, "Erreur de suppression de la liste", Toast.LENGTH_SHORT).show()
                            }
                            is DataProvider.ResultList.Success -> {
                                listes.removeAt(position)
                                notifyItemRemoved(position)
                                Toast.makeText(context, "Liste supprimée", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .setNegativeButton("Annuler", null)
                .show()
            true
        }
    }

    fun ajouterListe(nouvelleListe: ListeToDo) {
        listes.add(nouvelleListe)
        notifyItemInserted(listes.size - 1)
    }

    override fun getItemCount(): Int = listes.size
}