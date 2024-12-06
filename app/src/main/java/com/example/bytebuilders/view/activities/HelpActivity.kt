package com.example.bytebuilders.view.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bytebuilders.databinding.ActivityHelpBinding
import com.example.bytebuilders.view.utils.LocalHelper

class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.webViewHelp.settings.javaScriptEnabled = true

        // Obtener el código de idioma actual
        val languageCode = LocalHelper.getPreferredLanguage(this)

        // Determinar el nombre del archivo HTML a cargar
        val htmlFileName = when (languageCode) {
            "en" -> "help_en.html"
            "de" -> "help_de.html"
            else -> "help_es.html"  // Por defecto
        }

        // Cargar el archivo HTML correspondiente
        binding.webViewHelp.loadUrl("file:///android_asset/$htmlFileName")
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocalHelper.applyPreferredLanguage(newBase))
    }
}
