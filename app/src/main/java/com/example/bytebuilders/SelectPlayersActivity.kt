package com.example.bytebuilders

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SelectPlayersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.select_players)

        val textView = findViewById<TextView>(R.id.titlePlayer)
        val content = getString(R.string.Players_title)
        val underlinedText = SpannableString(content)
        underlinedText.setSpan(UnderlineSpan(), 0, content.length, 0)
        textView.text = underlinedText


    }


}