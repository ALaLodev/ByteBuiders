package com.example.bytebuilders

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GameActivity : AppCompatActivity() {

    private lateinit var plusButton: FloatingActionButton
    private lateinit var minusButton: FloatingActionButton
    private lateinit var selectedNumber: TextView
    private lateinit var sendButton: Button

    private lateinit var feedback: TextView // Se puede cambiar por imageview. Es una ayuda para mayor o menor
    private lateinit var roundtext: TextView //Sirve para ver la ronda en la que estás jugando


    private var randomNumber = 0 //Número que te da la funcion random
    private var selectedNumberValue = 0
    private var attemptsLeft = 4
    private var roundsNumber = 1
    private var points = 0
    //private var roundPoints = 0

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

        startNewRound()


        plusButton.setOnClickListener {
            val currentNumber = selectedNumber.text.toString().toInt()
            if (currentNumber < 12)
                selectedNumber.text = (currentNumber + 1).toString()
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

    }

    private fun startNewRound() {
        randomNumber = (1..12).random()
        selectedNumberValue = 0
        selectedNumber.text = selectedNumberValue.toString()
        attemptsLeft = 4
        roundtext.text = "Ronda $roundsNumber"
    }

    private fun checkAnswer() {
        val selectedNumberValue = selectedNumber.text.toString().toInt()
        if (selectedNumberValue == randomNumber) {
            feedback.text = "Correcto"
            points += attemptsLeft
            //roundsNumber++
            nextRoundOrEndGame()
        } else {
            attemptsLeft--
            feedback.text = "Incorrecto"
            if (selectedNumberValue < randomNumber) {
                feedback.text = "El número es mayor"
            } else {
                feedback.text = "El número es menor"

            }
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
        }

    }


}
