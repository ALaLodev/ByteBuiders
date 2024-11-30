package com.example.bytebuilders.view.activities

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.example.bytebuilders.R
import com.example.bytebuilders.databinding.ActivityMainBinding
import com.example.bytebuilders.view.utils.LocalHelper
import com.example.bytebuilders.view.utils.MusicPlayer

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    // Variables para el sonido de clic
    private lateinit var soundPool: SoundPool
    private var soundIdClickNormal: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Solo inicia la música si no esta reproduciendose
        if (!MusicPlayer.isPlaying()) {
            MusicPlayer.start(this, R.raw.solve_the_puzzle)
        }

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

        binding.btnStart.setOnClickListener {
            // Reproducir sonido de clic normal
            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            navigateToSelectPlayers()
        }

        binding.changeToEnglishButton.setOnClickListener{
            // Reproducir sonido de clic normal
            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            LocalHelper.setLocale(this, "en")
            recreate()
        }
        binding.changeToSpanishButton.setOnClickListener{
            // Reproducir sonido de clic normal
            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            LocalHelper.setLocale(this, "es")
            recreate()
        }
        binding.changeToGermanButton.setOnClickListener{
            // Reproducir sonido de clic normal
            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            LocalHelper.setLocale(this, "de")
            recreate()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    private fun navigateToSelectPlayers() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { LocalHelper.applyPreferredLanguage(it) })
    }
}
