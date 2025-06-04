package com.pmr2025.viallehosnieh.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.pmr2025.viallehosnieh.ui.ChoixListActivity
import com.pmr2025.viallehosnieh.R

class MainActivity : TemplateActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadView(R.layout.activity_main, "Main Activity")

        //setupWindowInsetsListener()

        val editTextPseudo = findViewById<EditText>(R.id.editTextPseudo)
        val buttonOk = findViewById<Button>(R.id.buttonOk)

        // Chargement du dernier pseudo enregistré
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val savedPseudo = prefs.getString("pseudo", "")
        editTextPseudo.setText(savedPseudo)

        buttonOk.setOnClickListener {
            val pseudo = editTextPseudo.text.toString()

            // Sauvegarde le pseudo dans les SharedPreferences
            prefs.edit() { putString("pseudo", pseudo) }

            // Lancer l’activité suivante
            val intent = Intent(this, ChoixListActivity::class.java)
            intent.putExtra("pseudo", pseudo)
            startActivity(intent)
        }
    }
    /*
    private fun setupWindowInsetsListener() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    */
}