package com.example.bytebuilders.view.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.example.bytebuilders.R
import com.example.bytebuilders.databinding.ActivityMainBinding
import com.example.bytebuilders.view.utils.LocalHelper
import com.example.bytebuilders.view.utils.MusicPlayer


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Solo inicia la música si no está reproduciéndose
        if (!MusicPlayer.isPlaying()) {
            MusicPlayer.start(this, R.raw.solve_the_puzzle)
        }

        binding.btnStart.setOnClickListener { navigateToSelectPlayers() }

        binding.changeToEnglishButton.setOnClickListener{
            LocalHelper.setLocale(this, "en")
            recreate()
        }
        binding.changeToSpanishButton.setOnClickListener{
            LocalHelper.setLocale(this, "es")
            recreate()
        }
        binding.changeToGermanButton.setOnClickListener{
            LocalHelper.setLocale(this, "de")
            recreate()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Si quieres que la música se detenga completamente al salir de MainActivity:
        // MusicPlayer.release()
    }

    private fun navigateToSelectPlayers() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { LocalHelper.applyPreferredLanguage(it) })
    }
}
