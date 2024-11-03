package com.example.bytebuilders

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class PauseActivity : AppCompatActivity() {

    private lateinit var resumeButton: Button
    private lateinit var mainMenuButton: Button
    private lateinit var settingsButton: ImageButton
    private lateinit var exitButton: ImageButton
    private lateinit var pauseTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pause)

        pauseTitle = findViewById(R.id.pauseTitle)
        resumeButton = findViewById(R.id.resumeButton)
        mainMenuButton = findViewById(R.id.mainMenuButton)
        settingsButton = findViewById(R.id.settingsButton)
        exitButton = findViewById(R.id.exitButton)

        resumeButton.setOnClickListener {
            // Finalizar PauseActivity para volver al GameActivity
            finish()
        }

        mainMenuButton.setOnClickListener {
            // Cerrar GameActivity y volver al MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK // Limpiar el stack
            startActivity(intent)
            finish()
        }

        settingsButton.setOnClickListener {
            // Ir al SettingsActivity
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        exitButton.setOnClickListener {
            // Salir de la app
            finishAffinity()
        }
    }
}