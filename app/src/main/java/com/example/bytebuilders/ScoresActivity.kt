package com.example.bytebuilders

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bytebuilders.application.RoomByteBuilders
import com.example.bytebuilders.data.entitys.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScoresActivity : AppCompatActivity() {

    private lateinit var buttonBackToMain: Button
    private lateinit var currentPlayerScoreTextView: TextView
    private var currentScore: Int = 0

    // TextViews para las posiciones
    private lateinit var player1NameScore: TextView
    private lateinit var player2NameScore: TextView
    private lateinit var player3NameScore: TextView
    private lateinit var player4NameScore: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scores)

        try {
            // Inicializar las vistas
            buttonBackToMain = findViewById(R.id.buttonBackToMain)
            currentPlayerScoreTextView = findViewById(R.id.currentPlayerScore)

            // TextViews para los jugadores
            player1NameScore = findViewById(R.id.player1NameScore)
            player2NameScore = findViewById(R.id.player2NameScore)
            player3NameScore = findViewById(R.id.player3NameScore)
            player4NameScore = findViewById(R.id.player4NameScore)

            // Obtener la puntuación actual desde el Intent
            currentScore = intent.getIntExtra("CURRENT_SCORE", 0)
            currentPlayerScoreTextView.text = "Tu puntuación: $currentScore puntos"

            buttonBackToMain.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            // Cargar y mostrar las puntuaciones
            loadScores()
        } catch (e: Exception) {
            Log.e("ScoresActivity", "Error en onCreate: ${e.message}")
        }
    }

    private fun loadScores() {
        CoroutineScope(Dispatchers.Main).launch {
            val scores = getScoresFromDatabase()
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
        // Asegurarse de que la lista tenga al menos 4 elementos
        // Si no, rellenar con datos predeterminados
        val defaultUser = UserEntity(namePlayer = "N/A", puntuacion = 0, fecha = "")
        val topScores = scores.take(4).toMutableList()
        while (topScores.size < 4) {
            topScores.add(defaultUser)
        }

        // Actualizar los TextViews con los datos de los jugadores
        player1NameScore.text = "${topScores[0].namePlayer} - ${topScores[0].puntuacion} puntos"
        player2NameScore.text = "${topScores[1].namePlayer} - ${topScores[1].puntuacion} puntos"
        player3NameScore.text = "${topScores[2].namePlayer} - ${topScores[2].puntuacion} puntos"
        player4NameScore.text = "${topScores[3].namePlayer} - ${topScores[3].puntuacion} puntos"
    }
}