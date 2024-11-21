package com.example.bytebuilders.view.activities

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.bytebuilders.R
import com.example.bytebuilders.databinding.ActivityGameBinding
import com.example.bytebuilders.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GameActivity : BaseActivity() {

    private lateinit var binding: ActivityGameBinding

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
    private var startTime: LocalDateTime? = null // Registro del tiempo de inicio del juego

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel() // Crear el canal de notificaciones
        binding = ActivityGameBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        startTime = LocalDateTime.now() // Registrar el tiempo de inicio del juego

        sharedPreferences = getSharedPreferences("GameSettings", Context.MODE_PRIVATE)
        volumeLevel = sharedPreferences.getInt("volumeLevel", 50)

        startNewRound()
        binding.returnToStart.visibility = View.GONE

        binding.plusButton.setOnClickListener {
            if (selectedNumberValue < 12) {
                selectedNumberValue++
                binding.selectedNumber.text = selectedNumberValue.toString()
            }
        }
        binding.minusButton.setOnClickListener {
            if (selectedNumberValue > 1) {
                selectedNumberValue--
                binding.selectedNumber.text = selectedNumberValue.toString()
            }
        }
        binding.sendButton.setOnClickListener {
            checkAnswer()
        }
        binding.returnToStart.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("Final_Score", points) // Pasa la puntuación final al siguiente activity
            startActivity(intent)
        }

        binding.pauseButton.setOnClickListener {
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
        binding.selectedNumber.text = selectedNumberValue.toString()
        attemptNumber = 1
        binding.roundText.text = getString(R.string.round_text, roundsNumber)
        binding.attemptText.text = getString(R.string.attempt_text, attemptNumber)
        binding.hiddenCard.setImageResource(R.drawable.spr_reverso)
    }

    @SuppressLint("DiscouragedApi")
    private fun checkAnswer() {
        if (selectedNumberValue == randomNumber) {
            binding.feedback.text = getString(R.string.feedback_correct)
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
            binding.feedback.text = getString(R.string.feedback_higher)
        } else {
            binding.feedback.text = getString(R.string.feedback_lower)
        }

        attemptNumber++
        binding.attemptText.text = getString(R.string.attempt_text, attemptNumber)

        if (attemptNumber > 4) {
            binding.feedback.text = getString(R.string.feedback_loss, randomNumber)
            showCardForIncorrectAnswer(randomNumber)
            nextRoundOrEndGame()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                if (!gameEnded) { binding.feedback.text = "" }
            }
        }
    }

    private fun showCardForCorrectAnswer(number: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            binding.hiddenCard.setImageResource(resources.getIdentifier("card_$number", "drawable", packageName))
            delay(2000)

            if (!gameEnded) {
                hideCard()
                binding.feedback.text = ""
            }
        }
    }

    private fun showCardForIncorrectAnswer(number: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            binding.hiddenCard.setImageResource(resources.getIdentifier("card_$number", "drawable", packageName))
            delay(2000)

            if (!gameEnded) {
                hideCard()
                binding.feedback.text = ""
            }
        }
    }

    private fun hideCard() {
        binding.hiddenCard.setImageResource(R.drawable.spr_reverso)
    }

    @SuppressLint("DiscouragedApi")
    private fun nextRoundOrEndGame() {
        if (roundsNumber < totalRounds) {
            roundsNumber++
            startNewRound()
        } else {
            // Fin de juego
            gameEnded = true
            binding.feedback.text = getString(R.string.game_over, points)
            binding.sendButton.visibility = View.GONE
            binding.plusButton.visibility = View.GONE
            binding.minusButton.visibility = View.GONE
            binding.selectedNumber.visibility = View.GONE
            binding.returnToStart.visibility = View.VISIBLE

            // Mostrar la carta final
            binding.hiddenCard.setImageResource(resources.getIdentifier("card_$randomNumber", "drawable", packageName))

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

        // Calcular tiempo transcurrido
        val elapsedTime = Duration.between(startTime, LocalDateTime.now()).seconds
        val formattedTime = "${elapsedTime / 60}m ${elapsedTime % 60}s"

        // Registrar datos en el ViewModel
        modelo.insertUser(winnerName, winnerScore, winnerDateTime)

        // Mostrar notificación
        showVictoryNotification(formattedTime)

        // Mostrar el tiempo de resolución en un Toast
        showTimeToast(formattedTime)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "game_notifications",
            "Game Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for game events"
        }
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun showVictoryNotification(elapsedTime: String) {
        // Cargar el logo de la notificación desde los recursos
        val logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.guesswarslogo) // Asegúrate de que "logo" sea el nombre del archivo sin la extensión

        val notification = NotificationCompat.Builder(this, "game_notifications")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ícono pequeño de la notificación
            .setLargeIcon(logoBitmap) // Ícono grande de la notificación
            .setContentTitle("¡Victoria!")
            .setContentText("La partida ha durado $elapsedTime.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(this)) {
            notify(1001, notification)
        }
    }

    private fun showTimeToast(elapsedTime: String) {
        // Muestra un Toast con el tiempo de resolución
        Toast.makeText(this, "¡Se acabó! La partida ha durado: $elapsedTime", Toast.LENGTH_LONG).show()
    }
}
