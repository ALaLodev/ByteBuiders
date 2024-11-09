package com.example.bytebuilders.view.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.bytebuilders.R
import com.example.bytebuilders.viewmodel.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GameActivity : BaseActivity() {

    private lateinit var plusButton: FloatingActionButton
    private lateinit var minusButton: FloatingActionButton
    private lateinit var selectedNumber: TextView
    private lateinit var sendButton: Button
    private lateinit var attempsText: TextView
    private lateinit var btnInicio: Button
    private lateinit var cardImageView: ImageView
    private lateinit var pauseButton: ImageButton

    private lateinit var feedback: TextView
    private lateinit var roundtext: TextView

    private var randomNumber = 0
    private var selectedNumberValue = 1
    private var attemptNumber = 1
    private var roundsNumber = 1
    private var points = 0
    private var gameEnded = false
    private val totalRounds = 4
    private val modelo: MainViewModel by viewModels()

    private lateinit var sharedPreferences: SharedPreferences
    private var volumeLevel: Int = 50
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)

        sharedPreferences = getSharedPreferences("GameSettings", Context.MODE_PRIVATE)
        volumeLevel = sharedPreferences.getInt("volumeLevel", 50)
        plusButton = findViewById(R.id.plusButton)
        minusButton = findViewById(R.id.minusButton)
        selectedNumber = findViewById(R.id.selectedNumber)
        sendButton = findViewById(R.id.sendButton)
        feedback = findViewById(R.id.feedback)
        roundtext = findViewById(R.id.roundText)
        attempsText = findViewById(R.id.attemptText)
        btnInicio = findViewById(R.id.scores)
        cardImageView = findViewById(R.id.hiddenCard)
        pauseButton = findViewById(R.id.pauseButton)

        startNewRound()
        btnInicio.visibility = View.GONE

        plusButton.setOnClickListener {
            if (selectedNumberValue < 12) {
                selectedNumberValue++
                selectedNumber.text = selectedNumberValue.toString()
            }
        }
        minusButton.setOnClickListener {
            if (selectedNumberValue > 1) {
                selectedNumberValue--
                selectedNumber.text = selectedNumberValue.toString()
            }
        }
        sendButton.setOnClickListener {
            checkAnswer()
        }
        btnInicio.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("Final_Score", points) // Pasa la puntuación final al siguiente activity
            startActivity(intent)
        }

        pauseButton.setOnClickListener {
            val intent = Intent(this, PauseActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setVolume(volume: Int) {
        val maxVolume = 100
        val volumeAdjustment = (1 - (Math.log((maxVolume - volume).toDouble()) / Math.log(maxVolume.toDouble()))).toFloat()
        mediaPlayer?.setVolume(volumeAdjustment, volumeAdjustment)
    }

    override fun onResume() {
        super.onResume()
        volumeLevel = sharedPreferences.getInt("volumeLevel", 50)
        setVolume(volumeLevel)
        mediaPlayer?.start()
        if (!gameEnded) {
            resumeGame()
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
        pauseGame()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun pauseGame() {
        // Pausar lógica del juego, por ejemplo, detener corrutinas
    }

    private fun resumeGame() {
        // Reanudar lógica del juego
    }

    private fun startNewRound() {
        randomNumber = (1..12).random()
        selectedNumberValue = 1
        selectedNumber.text = selectedNumberValue.toString()
        attemptNumber = 1
        roundtext.text = getString(R.string.round_text, roundsNumber)
        attempsText.text = getString(R.string.attempt_text, attemptNumber)
        cardImageView.setImageResource(R.drawable.spr_reverso)
    }

    @SuppressLint("DiscouragedApi")
    private fun checkAnswer() {
        if (selectedNumberValue == randomNumber) {
            feedback.text = getString(R.string.feedback_correct)
            val pointsEarned = 5 - attemptNumber
            points += pointsEarned

            showCardForCorrectAnswer(selectedNumberValue)
            nextRoundOrEndGame()
        } else {
            processIncorrectAnswer(selectedNumberValue)
        }
    }

    private fun processIncorrectAnswer(selectedNumberValue: Int) {
        if (selectedNumberValue < randomNumber) {
            feedback.text = getString(R.string.feedback_higher)
        } else {
            feedback.text = getString(R.string.feedback_lower)
        }

        attemptNumber++
        attempsText.text = getString(R.string.attempt_text, attemptNumber)

        if (attemptNumber > 4) {
            feedback.text = getString(R.string.feedback_loss, randomNumber)
            showCardForIncorrectAnswer(randomNumber)
            nextRoundOrEndGame()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                if (!gameEnded) { feedback.text = "" }
            }
        }
    }

    private fun showCardForCorrectAnswer(number: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            cardImageView.setImageResource(resources.getIdentifier("card_$number", "drawable", packageName))
            delay(2000)

            if (!gameEnded) {
                hideCard()
                feedback.text = ""
            }
        }
    }

    private fun showCardForIncorrectAnswer(number: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            cardImageView.setImageResource(resources.getIdentifier("card_$number", "drawable", packageName))
            delay(2000)

            if (!gameEnded) {
                hideCard()
                feedback.text = ""
            }
        }
    }

    private fun hideCard() {
        cardImageView.setImageResource(R.drawable.spr_reverso)
    }

    @SuppressLint("DiscouragedApi")
    private fun nextRoundOrEndGame() {
        if (roundsNumber < totalRounds) {
            roundsNumber++
            startNewRound()
        } else {
            // Fin de juego
            gameEnded = true
            feedback.text = getString(R.string.game_over, points)
            sendButton.visibility = View.GONE
            plusButton.visibility = View.GONE
            minusButton.visibility = View.GONE
            selectedNumber.visibility = View.GONE
            btnInicio.visibility = View.VISIBLE

            // Mostrar la carta final
            cardImageView.setImageResource(resources.getIdentifier("card_$randomNumber", "drawable", packageName))

            // Mostrar el layout de fin de juego
            val endGameLayout = findViewById<LinearLayout>(R.id.endGameLayout)
            endGameLayout.visibility = View.VISIBLE

            // Registrar al ganador
            registerWinner()
        }
    }

    private fun registerWinner() {
        val winnerName = "Jugador"
        val winnerScore = points
        val winnerDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        // Llamar al ViewModel para registrar el ganador
        modelo.insertUser(winnerName, winnerScore, winnerDateTime)
    }
}
