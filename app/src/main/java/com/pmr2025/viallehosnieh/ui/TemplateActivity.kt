package com.pmr2025.viallehosnieh.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pmr2025.viallehosnieh.R

open class TemplateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.template_activity)

        setupWindowInsetsListener()

        val button = findViewById<ImageView>(R.id.settingsButton)
        button.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

    }

    fun loadView(view : Int, activityName : String){
        val template = findViewById<LinearLayout>(R.id.layout_content)
        val insertView = layoutInflater.inflate(view, template, false)
        template.addView(insertView)

        val titleTextView = findViewById<TextView>(R.id.TitleView)
        titleTextView.text = activityName
    }

    private fun setupWindowInsetsListener() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.template)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}