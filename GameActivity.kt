package com.example.bytebuilders

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.bytebuilders.application.RoomByteBuilders
import com.example.bytebuilders.databinding.SelectPlayersBinding
import com.example.bytebuilders.vistamodelo.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter




class GameActivity : AppCompatActivity() {
    /**
     * INSERTAR DATOS POR EL MAINVIEWMODEL
     * **/
    private val modelo: MainViewModel by viewModels()
    private lateinit var plusButton: FloatingActionButton
    private lateinit var minusButton: FloatingActionButton
    private lateinit var selectedNumber: TextView
    private lateinit var sendButton: Button
    private lateinit var feedback: TextView
    private lateinit var roundText: TextView
    private lateinit var attemptText: TextView

    private var randomNumber = 0
    private var selectedNumberValue = 0
    private var attemptsLeft = 4
    private var roundsNumber = 1
    private var points = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.player_activity)

        // Inicialización de vistas
        initializeViews()

        // Iniciar la primera ronda
        startNewRound()

        // Configurar eventos de clic
        setupClickListeners()
    }

    private fun initializeViews() {
        plusButton = findViewById(R.id.plusButton)
        minusButton = findViewById(R.id.minusButton)
        selectedNumber = findViewById(R.id.selectedNumber)
        sendButton = findViewById(R.id.sendButton)
        feedback = findViewById(R.id.feedback)
        roundText = findViewById(R.id.roundText)
        attemptText = findViewById(R.id.attemptText)
    }

    private fun setupClickListeners() {
        plusButton.setOnClickListener {
            incrementSelectedNumber()
        }

        minusButton.setOnClickListener {
            decrementSelectedNumber()
        }

        sendButton.setOnClickListener {
            checkAnswer()
        }
    }

    private fun incrementSelectedNumber() {
        val currentNumber = selectedNumber.text.toString().toInt()
        if (currentNumber < 12) {
            selectedNumber.text = (currentNumber + 1).toString()
        }
    }

    private fun decrementSelectedNumber() {
        val currentNumber = selectedNumber.text.toString().toInt()
        if (currentNumber > 1) {
            selectedNumber.text = (currentNumber - 1).toString()
        }
    }

    private fun startNewRound() {
        randomNumber = (1..12).random()
        selectedNumberValue = 0
        selectedNumber.text = selectedNumberValue.toString()
        attemptsLeft = 4
        roundText.text = "Ronda $roundsNumber"
    }

    private fun checkAnswer() {
        val selectedNumberValue = selectedNumber.text.toString().toInt()

        if (selectedNumberValue == randomNumber) {
            feedback.text = "Correcto"
            points += attemptsLeft
            nextRoundOrEndGame()
        } else {
            attemptsLeft--
            feedback.text = "Incorrecto. El número es ${if (selectedNumberValue < randomNumber) "mayor" else "menor"}"
            if (attemptsLeft == 0) {
                feedback.text = "Has perdido, el número es $randomNumber"
                nextRoundOrEndGame()
            }
        }
    }

    private fun nextRoundOrEndGame() {
        if (roundsNumber < 3) {
            roundsNumber++
            startNewRound()
        } else {
            feedback.text = "Has ganado, tu puntuación es $points"
            sendButton.isEnabled = false
            // Registrar al ganador
            registerWinner()
        }
    }

    // Método para registrar al ganador
    private fun registerWinner() {
        val winnerName = "jugador4" // Cambia esto por el nombre del jugador real
        val winnerScore = points.toString() // Asegúrate de que 'points' está definido y tiene un valor
        val winnerDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        // Llamar al ViewModel para registrar el ganador
        modelo.insertUser (winnerName, winnerScore, winnerDateTime)
        //LLAMAR NUEVA ACTIVITY GRIDVIEW
        val intent = Intent(this, DetallePartidas::class.java)
        startActivity(intent)


    }


}