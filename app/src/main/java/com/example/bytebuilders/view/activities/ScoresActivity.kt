package com.example.bytebuilders.view.activities

import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.bytebuilders.R
import com.example.bytebuilders.databinding.ActivityScoresBinding
import com.example.bytebuilders.model.data.entitys.DatosJugador
import com.example.bytebuilders.model.data.entitys.UserEntity
import com.example.bytebuilders.viewmodel.MainViewModel
import com.example.bytebuilders.viewmodel.VistaFirebase

class ScoresActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScoresBinding
    private val viewModel: MainViewModel by viewModels()
    private val viewModelFirestore:VistaFirebase by viewModels()
    // Variables para el sonido de clic
    private lateinit var soundPool: SoundPool
    private var soundIdClickNormal: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoresBinding.inflate(layoutInflater)
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

        binding.buttonBackToMain.setOnClickListener {

            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Mejores puntuaciones
       // viewModel.topScores.observe(this) { scores ->
         //   displayScores(scores)
        //}
        cargarPuntuaciones()
        // Mensajes de error
        viewModel.errorMessage.observe(this) { errorMsg ->
            if (errorMsg != null) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }

        // Cargar las mejores puntuaciones
        viewModel.loadTopScores()
    }
    private fun cargarPuntuaciones() {
        val vistaFirebase = VistaFirebase()

        vistaFirebase.obtenerTopJugadores(
            onResult = { topJugadores ->
                // Llama a displayScores con la lista de jugadores obtenidos
                displayScores(topJugadores)
            },
            onError = { exception ->
                // Manejo de errores
                Log.e("Error", "Error al obtener los jugadores: ${exception.message}")
            }
        )
    }
    private fun displayScores(scores: List<DatosJugador>) {
        // Asegurarse de tener al menos 4 elementos
        val defaultUser = DatosJugador(
            namePlayer = "N/A",
            puntuacion = 0,
            fecha = "N/A",
            latitude = 0.0,
            longitude = 0.0
        )
        val topScores = scores.take(4).toMutableList()
        while (topScores.size < 4) {
            topScores.add(defaultUser)
        }

        // Actualizar los TextViews con los datos
        binding.player1NameScore.text = "${topScores[0].namePlayer}-${topScores[0].fecha} - ${topScores[0].puntuacion} ${getString(R.string.points)}\nLat: ${topScores[0].latitude}, Lon: ${topScores[0].longitude}"
        binding.player2NameScore.text = "${topScores[1].namePlayer}-${topScores[1].fecha} - ${topScores[1].puntuacion} ${getString(R.string.points)}\nLat: ${topScores[1].latitude}, Lon: ${topScores[1].longitude}"
        binding.player3NameScore.text = "${topScores[2].namePlayer}-${topScores[2].fecha} - ${topScores[2].puntuacion} ${getString(R.string.points)}\nLat: ${topScores[2].latitude}, Lon: ${topScores[2].longitude}"
        binding.player4NameScore.text = "${topScores[3].namePlayer}-${topScores[3].fecha} - ${topScores[3].puntuacion} ${getString(R.string.points)}\nLat: ${topScores[3].latitude}, Lon: ${topScores[3].longitude}"
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
}
