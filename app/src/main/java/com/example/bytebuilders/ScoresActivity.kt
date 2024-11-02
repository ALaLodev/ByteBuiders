package com.example.bytebuilders

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bytebuilders.application.RoomByteBuilders
import com.example.bytebuilders.data.entitys.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScoresActivity : AppCompatActivity() {

    private lateinit var rankingsContainer: LinearLayout
    private lateinit var buttonBackToMain: Button
    private lateinit var currentPlayerScoreTextView: TextView
    private var currentScore: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scores)

        rankingsContainer = findViewById(R.id.rankingsContainer)
        buttonBackToMain = findViewById(R.id.buttonBackToMain)
        currentPlayerScoreTextView = findViewById(R.id.currentPlayerScore)

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
    }

    private fun loadScores() {
        CoroutineScope(Dispatchers.Main).launch {
            val scores = getScoresFromDatabase()
            displayScores(scores)
        }
    }

    private suspend fun getScoresFromDatabase(): List<UserEntity> {
        return RoomByteBuilders.db.userDao().getTopScores()
    }

    private fun displayScores(scores: List<UserEntity>) {
        rankingsContainer.removeAllViews()

        for ((index, user) in scores.withIndex()) {
            val textView = TextView(this)
            textView.text = "${index + 1}. ${user.namePlayer} - ${user.puntuacion} puntos - ${user.fecha}"
            textView.textSize = 20f
            textView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            textView.setPadding(0, 8, 0, 8)
            rankingsContainer.addView(textView)
        }
    }
}
