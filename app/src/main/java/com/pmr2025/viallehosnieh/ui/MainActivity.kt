package com.pmr2025.viallehosnieh.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.pmr2025.viallehosnieh.R
import com.pmr2025.viallehosnieh.data.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : TemplateActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadView(R.layout.activity_main, "Main Activity")

        val editTextPseudo = findViewById<EditText>(R.id.editTextPseudo)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonOk = findViewById<Button>(R.id.buttonOk)
        val buttonNewAccount = findViewById<Button>(R.id.buttonNewAccount)

        // Chargement du dernier pseudo enregistré
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val savedPseudo = prefs.getString("pseudo", "")
        editTextPseudo.setText(savedPseudo)

        val dataProvider = DataProvider(this.application)

        lifecycleScope.launch {
            if (canAccessInternet()) {
                buttonNewAccount.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.new_account_background))
                buttonOk.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.ok_background))
            } else {
                Toast.makeText(this@MainActivity, "Pas de connexion internet", Toast.LENGTH_SHORT).show()
            }
        }

        buttonOk.setOnClickListener {
            val user = editTextPseudo.text.toString()
            val password = editTextPassword.text.toString()
            if (user.isBlank() || password.isBlank()){
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val result = dataProvider.authenticate(user, password)
                when(result){
                    DataProvider.ResultUser.Failure -> {
                        Toast.makeText(this@MainActivity, "Erreur d'authentification", Toast.LENGTH_SHORT).show()
                    }
                    is DataProvider.ResultUser.Success -> {
                        prefs.edit() { putString("pseudo", user) } // Sauvegarde le hash dans les SharedPreferences
                        val intent = Intent(this@MainActivity, ChoixListActivity::class.java) // Lance l’activité suivante
                        intent.putExtra("hash", result.hash)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    suspend fun canAccessInternet(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url =
                URL("https://clients3.google.com/generate_204") // Page qui renvoie 204 sans contenu
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 1500
            conn.readTimeout = 1500
            conn.connect()
            conn.responseCode == 204
        } catch (e: Exception) {
            false
        }
    }
}