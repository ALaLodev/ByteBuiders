package com.example.bytebuilders.view.activities

import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.bytebuilders.R
import com.example.bytebuilders.databinding.ActivityScoresBinding
import com.example.bytebuilders.model.data.entitys.DatosJugador
import com.example.bytebuilders.viewmodel.MainViewModel
import com.example.bytebuilders.viewmodel.VistaFirebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScoresActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScoresBinding
    private val viewModel: MainViewModel by viewModels()
    private val viewModelFirestore: VistaFirebase by viewModels()
    // Variables para el sonido de clic
    private lateinit var soundPool: SoundPool
    private var soundIdClickNormal: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoresBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()
        soundIdClickNormal = soundPool.load(this, R.raw.click_normal, 1)

        binding.buttonBackToMain.setOnClickListener {
            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        viewModel.errorMessage.observe(this) { errorMsg ->
            if (errorMsg != null) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }

        cargarPuntuaciones()
        viewModel.loadTopScores()
    }

    private fun cargarPuntuaciones() {
        val vistaFirebase = VistaFirebase()
        vistaFirebase.obtenerTopJugadores(
            onResult = { topJugadores ->
                displayScores(topJugadores)
            },
            onError = { exception ->
                Log.e("Error", "Error al obtener los jugadores: ${exception.message}")
            }
        )
    }

    private fun displayScores(scores: List<DatosJugador>) {
        // 1) Encontrar el último que jugó
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val lastPlayer = scores.maxByOrNull { LocalDateTime.parse(it.fecha, formatter) }

        // 2) Ordenar por puntuacion (desc)
        val sortedByPoints = scores.sortedByDescending { it.puntuacion }

        // 3) Asegurarse de tener al menos 4 elementos
        val defaultUser = DatosJugador("N/A", 0, "N/A", 0.0, 0.0)
        val topScores = sortedByPoints.take(4).toMutableList()
        while (topScores.size < 4) {
            topScores.add(defaultUser)
        }

        // 4) Actualizar los TextViews + trofeo si es el último
        binding.player1NameScore.text = formatScoreText(topScores[0], 1, lastPlayer)
        binding.player2NameScore.text = formatScoreText(topScores[1], 2, lastPlayer)
        binding.player3NameScore.text = formatScoreText(topScores[2], 3, lastPlayer)
        binding.player4NameScore.text = formatScoreText(topScores[3], 4, lastPlayer)
    }

    private fun formatScoreText(user: DatosJugador, pos: Int, lastPlayer: DatosJugador?): String {
        val trofeo = if (user == lastPlayer) " 🏆" else ""
        return "$pos. ${user.namePlayer}-${user.fecha} - ${user.puntuacion} ${getString(R.string.points)}$trofeo\n" +
                "Lat: ${user.latitude}, Lon: ${user.longitude}"
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
}
