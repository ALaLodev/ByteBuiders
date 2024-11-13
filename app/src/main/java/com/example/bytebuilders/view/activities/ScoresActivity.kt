package com.example.bytebuilders.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bytebuilders.R
import com.example.bytebuilders.application.RoomByteBuilders
import com.example.bytebuilders.databinding.ActivityScoresBinding
import com.example.bytebuilders.model.data.entitys.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScoresActivity : BaseActivity() {

    private lateinit var binding: ActivityScoresBinding

    private var currentScore: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoresBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        try {

            // Sacar la score actual desde el Intent
            currentScore = intent.getIntExtra("CURRENT_SCORE", 0)

            binding.buttonBackToMain.setOnClickListener {
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                finish()
            }

            // Cargar y mostrar las scores
            loadScores()
        } catch (e: Exception) {
            Log.e("ScoresActivity", "Error en onCreate: ${e.message}")
        }
    }

    private fun loadScores() {
        CoroutineScope(Dispatchers.Main).launch {
            val scores = withContext(Dispatchers.IO) { getScoresFromDatabase() } // con Dispatchers.IO aseguramos que se realice en un hilo secundario de E/S de datos
            displayScores(scores)
        }
    }

    private suspend fun getScoresFromDatabase(): List<UserEntity> {
        return try {
            RoomByteBuilders.db.userDao().getTopScores()
        } catch (e: Exception) {
            Log.e("ScoresActivity", "Error al obtener puntuaciones: ${e.message}")
            emptyList()
        }
    }

    private fun displayScores(scores: List<UserEntity>) {
        // Nos aseguramos que la lista tenga al menos 4 elementos
        // Si no, rellenar con datos predeterminados
        val defaultUser = UserEntity(namePlayer = "N/A", puntuacion = 0, fecha = "",latitude = 0.0, longitude = 0.0)
        val topScores = scores.take(4).toMutableList()
        while (topScores.size < 4) {
            topScores.add(defaultUser)
        }

        // Actualizar los TextViews con los datos de los jugadores. Cambiar usuario por fecha?
        binding.player1NameScore.text = "${topScores[0].fecha} - ${topScores[0].puntuacion} puntos"
        binding.player2NameScore.text = "${topScores[1].fecha} - ${topScores[1].puntuacion} puntos"
        binding.player3NameScore.text = "${topScores[2].fecha} - ${topScores[2].puntuacion} puntos"
        binding.player4NameScore.text = "${topScores[3].fecha} - ${topScores[3].puntuacion} puntos"
    }
}