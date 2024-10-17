package com.example.bytebuilders

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class GameActivity : AppCompatActivity() {

    private lateinit var plusButton: FloatingActionButton
    private lateinit var minusButton: FloatingActionButton
    private lateinit var selectedNumber: TextView
    private lateinit var sendButton: Button
    private lateinit var attempsText: TextView
    private lateinit var btnInicio: Button
    private lateinit var cardImageView: ImageView

    private lateinit var feedback: TextView
    private lateinit var roundtext: TextView


    private var randomNumber = 0
    private var selectedNumberValue = 0
    private var attemptsLeft = 4
    private var roundsNumber = 1
    private var points = 0
    private var gameEnded = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.player_activity)

        plusButton = findViewById(R.id.plusButton)
        minusButton = findViewById(R.id.minusButton)
        selectedNumber = findViewById(R.id.selectedNumber)
        sendButton = findViewById(R.id.sendButton)
        feedback = findViewById(R.id.feedback)
        roundtext = findViewById(R.id.roundText)
        attempsText = findViewById(R.id.attemptText)
        btnInicio = findViewById(R.id.player2)
        cardImageView = findViewById(R.id.hiddenCard)

        startNewRound()
        btnInicio.isEnabled = false

        plusButton.setOnClickListener {
            val currentNumber = selectedNumber.text.toString().toInt()
            if (currentNumber < 12) selectedNumber.text = (currentNumber + 1).toString()
        }
        minusButton.setOnClickListener {
            val currentNumber = selectedNumber.text.toString().toInt()
            if (currentNumber > 1) {
                selectedNumber.text = (currentNumber - 1).toString()
            }
        }
        sendButton.setOnClickListener {
            checkAnswer()
        }
        btnInicio.setOnClickListener {
            val intent = Intent(this, SelectPlayersActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startNewRound() {
        randomNumber = (1..12).random()
        selectedNumberValue = 0
        selectedNumber.text = selectedNumberValue.toString()
        attemptsLeft = 4
        roundtext.text = "Ronda $roundsNumber"
        attempsText.text = "Intento: $attemptsLeft/4"
        cardImageView.setImageResource(R.drawable.spr_reverso)
    }

    @SuppressLint("DiscouragedApi")
    private fun checkAnswer() {
        val selectedNumberValue = selectedNumber.text.toString().toInt()

        if (selectedNumberValue == randomNumber && roundsNumber < 3) {
            feedback.text = "Correcto"
            showCardForCorrectAnswer(selectedNumberValue)
            points += attemptsLeft
            nextRoundOrEndGame()
        } else {
            processIncorrectAnswer(selectedNumberValue)
        }
    }

    private fun showCardForCorrectAnswer(number: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            cardImageView.setImageResource(resources.getIdentifier("card_$number", "drawable", packageName))
            delay(2000)

            if (!gameEnded) {
                hideCard()
            }
            feedback.text = ""
        }
    }

    private fun processIncorrectAnswer(selectedNumberValue: Int) {
        if (selectedNumberValue < randomNumber) {
            feedback.text = "El número es mayor"
        } else {
            feedback.text = "El número es menor"
        }

        attemptsLeft--
        attempsText.text = "Intento: $attemptsLeft/4"

        if (attemptsLeft == 0) {
            feedback.text = "Has perdido, el número es $randomNumber"
            showCardForIncorrectAnswer(randomNumber)
            nextRoundOrEndGame()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
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
        if (roundsNumber < 3) {
            roundsNumber++
            startNewRound()
        } else {
            //Fin de juego
            gameEnded = true
            feedback.text = "Fin de juego, tu puntuación es $points puntos"
            sendButton.isEnabled = false
            plusButton.isEnabled = false
            minusButton.isEnabled = false
            btnInicio.isEnabled = true

            // Mostrar la carta final
            cardImageView.setImageResource(resources.getIdentifier("card_$randomNumber", "drawable", packageName))
        }
    }
}
