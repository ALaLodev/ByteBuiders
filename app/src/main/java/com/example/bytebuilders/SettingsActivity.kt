package com.example.bytebuilders

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var volumeSeekBar: SeekBar
    private lateinit var volumeLabel: TextView
    private lateinit var languageLabel: TextView
    private lateinit var languagePendingMessage: TextView
    private lateinit var exitSettingsButton: Button

    // Variable del volumen con valor predeterminado
    private var volumeLevel: Int = 50
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("GameSettings", Context.MODE_PRIVATE)

        volumeSeekBar = findViewById(R.id.volumeSeekBar)
        volumeLabel = findViewById(R.id.volumeLabel)
        languageLabel = findViewById(R.id.languageLabel)
        languagePendingMessage = findViewById(R.id.languagePendingMessage)
        exitSettingsButton = findViewById(R.id.exitSettingsButton)

        // Obtener el nivel de volumen guardado
        volumeLevel = sharedPreferences.getInt("volumeLevel", 50)
        volumeSeekBar.progress = volumeLevel

        // Configurar SeekBar de volumen
        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                volumeLevel = progress
                // Guardar el nuevo nivel de volumen
                sharedPreferences.edit().putInt("volumeLevel", volumeLevel).apply()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // No se necesita implementar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // No se necesita implementar
            }
        })

        // Listener del cambio de idioma (pendiente)
        languageLabel.setOnClickListener {
            // Pendiente de implementación
        }

        // Manejar el clic del botón de salir
        exitSettingsButton.setOnClickListener {
            // Finalizar la actividad para volver a la anterior
            finish()
        }
    }
}
