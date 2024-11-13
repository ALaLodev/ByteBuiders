package com.example.bytebuilders.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.example.bytebuilders.databinding.ActivityPauseBinding
import com.example.bytebuilders.view.utils.MusicPlayer

class PauseActivity : BaseActivity() {

    private lateinit var binding: ActivityPauseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPauseBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.resumeButton.setOnClickListener {
            // Finalizar PauseActivity para volver al GameActivity
            finish()
        }

        binding.mainMenuButton.setOnClickListener {
            // Cerrar GameActivity y volver al MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK // Limpiar el stack
            startActivity(intent)
            finish()
        }

        binding.settingsButton.setOnClickListener {
            // Ir al SettingsActivity
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        binding.muteButton.setOnClickListener {
            if (MusicPlayer.isPlaying()) {
                MusicPlayer.mute()
            } else {
                MusicPlayer.mute()
            }
        }

        binding.exitButton.setOnClickListener {
            // Salir de la app
            finishAffinity()
        }
    }
}