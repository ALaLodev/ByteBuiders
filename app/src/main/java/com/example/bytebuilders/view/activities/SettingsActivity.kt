package com.example.bytebuilders.view.activities

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bytebuilders.databinding.ActivitySettingsBinding
import com.example.bytebuilders.view.utils.MusicPlayer

class SettingsActivity : BaseActivity() {

    private lateinit var binding : ActivitySettingsBinding
    private lateinit var getContentLauncher: ActivityResultLauncher<String>

    companion object {
        private const val PICK_AUDIO_REQUEST = 1
    }

    // Variable del volumen con valor predeterminado
    private var volumeLevel: Int = 50
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Se inicializa el ActivityResultLauncher
        getContentLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? -> uri?.let { playAudio(it) } }

        sharedPreferences = getSharedPreferences("GameSettings", Context.MODE_PRIVATE)

        // Obtener el nivel de volumen guardado
        volumeLevel = sharedPreferences.getInt("volumeLevel", 50)
        binding.volumeSeekBar.progress = volumeLevel

        // Configurar SeekBar de volumen
        binding.volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                volumeLevel = progress
                sharedPreferences.edit().putInt("volumeLevel", volumeLevel).apply() // Guardar el nuevo nivel de volumen
                MusicPlayer.setVolume(volumeLevel / 100f) // Ajustar el volumen
                binding.volumeLabel.text = "Volumen: $volumeLevel" // Actualizar etiqueta del volumen
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {} // No es necesario implementar
            override fun onStopTrackingTouch(seekBar: SeekBar?) {} // No es necesario implementar
        })

        binding.musicFolderButton.setOnClickListener {
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

        // Manejar el clic del botón de salir
        binding.exitSettingsButton.setOnClickListener { finish() }
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
        MusicPlayer.startWithUri(this, audioUri)
    }
}
