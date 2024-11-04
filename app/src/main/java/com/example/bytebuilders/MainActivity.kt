package com.example.bytebuilders

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val btnEmpezar = findViewById<Button>(R.id.empezar)

        btnEmpezar.setOnClickListener { navigateToSelectPlayers() }
    }

    private fun navigateToSelectPlayers() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }
}