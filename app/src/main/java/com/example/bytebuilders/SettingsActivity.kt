package com.example.bytebuilders

import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var volumeSeekBar: SeekBar
    private lateinit var volumeLabel: TextView
    private lateinit var languageLabel: TextView

    // Variable del volumen con valor predeterminado
    private var volumeLevel: Int = 50

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        volumeSeekBar = findViewById(R.id.volumeSeekBar)
        volumeLabel = findViewById(R.id.volumeLabel)
        languageLabel = findViewById(R.id.languageLabel)

        // Configurar SeekBar de volumen
        volumeSeekBar.progress = volumeLevel
        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                volumeLevel = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // No se toca por ahora
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // No se toca por ahora
            }
        })

        // listener del cambio idioma (pendiente)
        languageLabel.setOnClickListener {
                // No se toca por ahora
        }
    }
}
