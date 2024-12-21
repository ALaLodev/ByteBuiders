package com.example.bytebuilders.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import com.example.bytebuilders.R
import com.example.bytebuilders.databinding.ActivityMenuBinding
import com.example.bytebuilders.view.utils.MusicPlayer

class MenuActivity : BaseActivity() {

    private lateinit var binding: ActivityMenuBinding

    private var points: Int = 0

    // Variables para el sonido de clic
    private lateinit var soundPool: SoundPool
    private var soundIdClickNormal: Int = 0

    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        MusicPlayer.start(this, R.raw.solve_the_puzzle)

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

        // Configura el texto subrayado en el título del menu
        val textView = findViewById<TextView>(R.id.titlePlayer)
        val content = getString(R.string.menu_title)
        val underlinedText = SpannableString(content)
        underlinedText.setSpan(UnderlineSpan(), 0, content.length, 0)
        textView.text = underlinedText

        points = intent.getIntExtra("Final_Score", 0)

        binding.settingsButtonMenu.setOnClickListener {

            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.exitButtonMenu.setOnClickListener {

            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            // Salir de la app
            finishAffinity()
        }

        binding.playGame.setOnClickListener {

            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            navigateToSelectPlayers()
        }
        binding.btnScores.setOnClickListener {

            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            navigateToScores()
        }
        // Llamada Detalle partida
        binding.btnDetalle.setOnClickListener{

            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            navigateDetailGame()
        }
        // Configuracion boton mute
        binding.muteButtonMenu.setOnClickListener {

            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            if (MusicPlayer.isPlaying()) {
                MusicPlayer.pause()
            } else {
                MusicPlayer.resume()
            }
        }
        // Configurar el boton de ayuda
        binding.helpButton.setOnClickListener {

            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
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
    // Boton Detalle Partida
    private fun navigateDetailGame(){
        val intent = Intent(this, DetallePartidas::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
        MusicPlayer.release()
    }

    override fun onResume() {
        super.onResume()
        MusicPlayer.resume()
    }
}
