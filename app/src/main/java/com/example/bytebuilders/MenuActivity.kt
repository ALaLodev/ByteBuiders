package com.example.bytebuilders

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    private lateinit var exitButtonMenu: ImageButton
    private lateinit var settingsButtonMenu: ImageButton

    private var points: Int = 0

    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        settingsButtonMenu = findViewById(R.id.settingsButtonMenu)
        exitButtonMenu = findViewById(R.id.exitButtonMenu)

        points = intent.getIntExtra("Final_Score", 0)

        settingsButtonMenu.setOnClickListener {
            // Ir al SettingsActivity
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        exitButtonMenu.setOnClickListener {
            // Salir de la app
            finishAffinity()
        }

        val textView = findViewById<TextView>(R.id.titlePlayer)
        val content = getString(R.string.Menu_title)
        val underlinedText = SpannableString(content)
        underlinedText.setSpan(UnderlineSpan(), 0, content.length, 0)
        textView.text = underlinedText


        val btnPlayGame = findViewById<ImageButton>(R.id.playGame)
        val btnScores = findViewById<Button>(R.id.scores)

        btnPlayGame.setOnClickListener { navigateToSelectPlayers() }
        btnScores.setOnClickListener { navigateToScores() }

    }

    private fun navigateToSelectPlayers() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToScores() {
        val intent = Intent(this, ScoresActivity::class.java)
        intent.putExtra("CURRENT_SCORE", points)
        startActivity(intent)
    }


}