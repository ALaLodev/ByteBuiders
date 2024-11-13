package com.example.bytebuilders.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import com.example.bytebuilders.R
import com.example.bytebuilders.databinding.ActivityMenuBinding
import com.example.bytebuilders.view.utils.MusicPlayer

class MenuActivity : BaseActivity() {

    private lateinit var binding: ActivityMenuBinding

    private var points: Int = 0

    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        MusicPlayer.start(this, R.raw.solve_the_puzzle)

        // Configura el texto subrayado en el título del menu
        val textView = findViewById<TextView>(R.id.titlePlayer)
        val content = getString(R.string.menu_title)
        val underlinedText = SpannableString(content)
        underlinedText.setSpan(UnderlineSpan(), 0, content.length, 0)
        textView.text = underlinedText

        points = intent.getIntExtra("Final_Score", 0)

        binding.settingsButtonMenu.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.exitButtonMenu.setOnClickListener {
            // Salir de la app
            finishAffinity()
        }

        binding.playGame.setOnClickListener { navigateToSelectPlayers() }
        binding.btnScores.setOnClickListener { navigateToScores() }

        // Configución botón mute
        binding.muteButtonMenu.setOnClickListener {
            if (MusicPlayer.isPlaying()) {
                MusicPlayer.pause()
            } else {
                MusicPlayer.resume()
            }
        }
        // Configurar el botón de ayuda
        binding.helpButton.setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }
    }

    private fun navigateToSelectPlayers() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToScores() {
        val intent = Intent(this, ScoresActivity::class.java)
        intent.putExtra("CURRENT_SCORE", points)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        MusicPlayer.resume()
    }

    // Libera recursos al salir de la app
    override fun onDestroy() {
        super.onDestroy()
        MusicPlayer.release()
    }
}