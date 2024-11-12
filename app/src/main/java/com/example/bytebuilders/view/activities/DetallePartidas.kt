package com.example.bytebuilders.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridView

import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.bytebuilders.R
import com.example.bytebuilders.viewmodel.MainViewModel
import com.example.bytebuilders.vistamodelo.MyGridAdapter

class DetallePartidas : AppCompatActivity()  {

    private val modelo: MainViewModel by viewModels()
    private lateinit var gridView: GridView
    private lateinit var btnInicio: Button
    private var points = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detalle_partidas)

        gridView = findViewById(R.id.griddetalle)
        btnInicio = findViewById(R.id.scores)
        // Observa los datos del ViewModel
        modelo.users.observe(this) { users ->
            // Maneja el caso en que users puede ser nulo
            val adapter = MyGridAdapter(this, users ?: emptyList()) // Aquí pasamos la lista de usuarios
            gridView.adapter = adapter
        }
        //
        btnInicio.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("Final_Score", points) // Pasa la puntuación final al siguiente activity
            startActivity(intent)
        }
        // Carga todos los usuarios al iniciar
        modelo.loadAllUser ()
        // Mostrar el layout de fin de juego
        val endGameLayout = findViewById<LinearLayout>(R.id.endGameLayout)
        endGameLayout.visibility = View.VISIBLE

    }

 }