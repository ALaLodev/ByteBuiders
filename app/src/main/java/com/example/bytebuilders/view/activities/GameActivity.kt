package com.example.bytebuilders.view.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import android.content.pm.PackageManager
import android.health.connect.datatypes.ExerciseRoute
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.bytebuilders.R
import com.example.bytebuilders.databinding.ActivityGameBinding
import com.example.bytebuilders.viewmodel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.Manifest
import android.location.Location
import android.widget.Toast

class GameActivity : BaseActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    //private lateinit var plusButton: FloatingActionButton
    //private lateinit var minusButton: FloatingActionButton
   // private lateinit var selectedNumber: TextView
    //private lateinit var sendButton: Button
    //private lateinit var attempsText: TextView
    //private lateinit var btnInicio: Button
    //private lateinit var cardImageView: ImageView
    //private lateinit var pauseButton: ImageButton

   // private lateinit var feedback: TextView
    //private lateinit var roundtext: TextView

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


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                 registerWinner()
            } else {
                // mostrar un mensaje al usuario
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("GameSettings", Context.MODE_PRIVATE)
        volumeLevel = sharedPreferences.getInt("volumeLevel", 50)
        // Inicializar FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //plusButton = findViewById(R.id.plusButton)
        //minusButton = findViewById(R.id.minusButton)
        //selectedNumber = findViewById(R.id.selectedNumber)
        //sendButton = findViewById(R.id.sendButton)
        //feedback = findViewById(R.id.feedback)
        //roundtext = findViewById(R.id.roundText)
        binding.attemptText
        //btnInicio = findViewById(R.id.scores)
        //binding.hiddenCard = findViewById(R.id.hiddenCard)

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
            //val endGameLayout = findViewById<LinearLayout>(R.id.endGameLayout)
             //endGameLayout.visibility = View.VISIBLE

            // Registrar al ganador
            registerWinner()
        }
    }
    //Llamada a ganador suceden varias cosas
    //Lllamar pantalla detalle y
    //Llamar a latitud longitud
    private fun registerWinner() {
        val winnerName = "Jugador"
        val winnerScore = points
        val winnerDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        // Obtener la ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val locationData = LocationData(location.latitude, location.longitude)
                    // Llamar al ViewModel para registrar el ganador
                    modelo.insertUser (winnerName, winnerScore, winnerDateTime,locationData)
                    val intent = Intent(this, DetallePartidas::class.java)
                    startActivity(intent)
                } else {
                    // Manejar el caso en que no se pudo obtener la ubicación
                    Toast.makeText(this, "No se pudo obtener la ubicación. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                // Manejar el caso en que hubo un error al obtener la ubicación
                Toast.makeText(this, "Error al obtener la ubicación: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Solicitar permiso de ubicación
            requestLocationPermission()
        }

    }

    //Parte de registro latitud longitud
    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}
