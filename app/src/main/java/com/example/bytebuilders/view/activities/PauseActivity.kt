package com.example.bytebuilders.view.activities

import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.example.bytebuilders.R
import com.example.bytebuilders.databinding.ActivityPauseBinding
import com.example.bytebuilders.view.utils.MusicPlayer

class PauseActivity : BaseActivity() {

    private lateinit var binding: ActivityPauseBinding

    // Variables para el sonido de clic
    private lateinit var soundPool: SoundPool
    private var soundIdClickNormal: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPauseBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Configurar SoundPool y cargar el sonido
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()
        soundIdClickNormal = soundPool.load(this, R.raw.click_normal, 1)

        binding.resumeButton.setOnClickListener {

            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            // Finalizar PauseActivity para volver al GameActivity
            finish()
        }

        binding.mainMenuButton.setOnClickListener {

            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            // Cerrar GameActivity y volver al MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK // Limpiar el stack
            startActivity(intent)
            finish()
        }

        binding.settingsButton.setOnClickListener {

            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            // Ir al SettingsActivity
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        binding.muteButton.setOnClickListener {

            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            if (MusicPlayer.isPlaying()) {
                MusicPlayer.mute()
            } else {
                MusicPlayer.mute()
            }
        }

        binding.exitButton.setOnClickListener {

            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            // Salir de la app
            finishAffinity()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
}
