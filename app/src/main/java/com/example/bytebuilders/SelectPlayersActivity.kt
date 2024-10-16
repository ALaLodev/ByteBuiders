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

class SelectPlayersActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.select_players)

        val textView = findViewById<TextView>(R.id.titlePlayer)
        val content = getString(R.string.Players_title)
        val underlinedText = SpannableString(content)
        underlinedText.setSpan(UnderlineSpan(), 0, content.length, 0)
        textView.text = underlinedText


        val btnPlayer1 = findViewById<ImageButton>(R.id.player1)

        btnPlayer1.setOnClickListener { navigateToSelectPlayers() }

    }

    private fun navigateToSelectPlayers() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }


}