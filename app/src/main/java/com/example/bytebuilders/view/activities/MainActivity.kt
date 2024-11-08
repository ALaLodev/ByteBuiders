package com.example.bytebuilders.view.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.bytebuilders.R
import com.example.bytebuilders.view.activities.utils.LocalHelper
import com.example.bytebuilders.view.activities.utils.MusicPlayer


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        MusicPlayer.start(this, R.raw.solve_the_puzzle)

        val btnEmpezar = findViewById<Button>(R.id.empezar)
        btnEmpezar.setOnClickListener { navigateToSelectPlayers() }
    }

    private fun navigateToSelectPlayers() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { LocalHelper.applyPreferredLanguage(it) })
    }
    /*fun applyPreferredLanguage(context: Context): Context {
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val languageCode = sharedPreferences.getString("preferred_language", "en") ?: "en"
        return LocalHelper.setLocale(languageCode, context)
    }*/
}