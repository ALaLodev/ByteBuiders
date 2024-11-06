package com.example.bytebuilders

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SettingsActivity : AppCompatActivity() {

    companion object {
        private const val PICK_AUDIO_REQUEST = 1
    }

    private lateinit var volumeSeekBar: SeekBar
    private lateinit var volumeLabel: TextView
    private lateinit var languageLabel: TextView
    private lateinit var languagePendingMessage: TextView
    private lateinit var exitSettingsButton: Button
    private lateinit var musicFolderButton: ImageButton

    private lateinit var getContentLauncher: ActivityResultLauncher<String>
    private var mediaPlayer: MediaPlayer? = null

    // Variable del volumen con valor predeterminado
    private var volumeLevel: Int = 50
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        // Se inicializa el ActivityResultLauncher
        getContentLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                playAudio(it) // Reproducir la pista seleccionada
            }
        }

        sharedPreferences = getSharedPreferences("GameSettings", Context.MODE_PRIVATE)

        volumeSeekBar = findViewById(R.id.volumeSeekBar)
        volumeLabel = findViewById(R.id.volumeLabel)
        languageLabel = findViewById(R.id.languageLabel)
        languagePendingMessage = findViewById(R.id.languagePendingMessage)
        exitSettingsButton = findViewById(R.id.exitSettingsButton)
        musicFolderButton = findViewById(R.id.music_folder_Button)

        // Obtener el nivel de volumen guardado
        volumeLevel = sharedPreferences.getInt("volumeLevel", 50)
        volumeSeekBar.progress = volumeLevel

        // Configurar SeekBar de volumen
        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                volumeLevel = progress
                sharedPreferences.edit().putInt("volumeLevel", volumeLevel).apply() // Guardar el nuevo nivel de volumen
                MusicPlayer.setVolume(volumeLevel / 100f) // Ajustar el volumen
                volumeLabel.text = "Volumen: $volumeLevel" // Actualizar etiqueta del volumen
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {} // No es necesario implementar
            override fun onStopTrackingTouch(seekBar: SeekBar?) {} // No es necesario implementar
        })

        musicFolderButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                    PICK_AUDIO_REQUEST
                )
            } else {
                openFileChooser()
            }
        }

        // Listener del cambio de idioma (pendiente)
        languageLabel.setOnClickListener {
            // Pendiente de implementación
        }

        // Manejar el clic del botón de salir
        exitSettingsButton.setOnClickListener { finish() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PICK_AUDIO_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileChooser()
            } else {
                Toast.makeText(this, "Permiso denegado para leer archivos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openFileChooser() {
        getContentLauncher.launch("audio/*") // Lanza el ActivityResultLauncher
    }

    private fun playAudio(audioUri: Uri) {
        // Liberar el MediaPlayer anterior
        mediaPlayer?.release()
        mediaPlayer = null

        MusicPlayer.startWithUri(this, audioUri)
    }

}
