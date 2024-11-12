package com.example.bytebuilders.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.GridView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.bytebuilders.R
import com.example.bytebuilders.databinding.ActivityGameBinding
import com.example.bytebuilders.databinding.DetallePartidasBinding
import com.example.bytebuilders.viewmodel.MainViewModel
import com.example.bytebuilders.vistamodelo.MyGridAdapter

class DetallePartidas : BaseActivity()  {
    private lateinit var binding: DetallePartidasBinding

    private val modelo: MainViewModel by viewModels()
    private lateinit var gridView: GridView
    private var points = 10


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detalle_partidas)
        binding = DetallePartidasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //gridView = findViewById(R.id.griddetalle)

        binding.griddetalle


        binding.returnToStart.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("Final_Score", points) // Pasa la puntuación final al siguiente activity
            startActivity(intent)
        }



        // Observa los datos del ViewModel
        modelo.users.observe(this) { users ->
            // Maneja el caso en que users puede ser nulo
            val adapter = MyGridAdapter(this, users ?: emptyList()) // Aquí pasamos la lista de usuarios
            binding.griddetalle.adapter = adapter
        }

        // Carga todos los usuarios al iniciar
        modelo.loadAllUser ()

        //val endGameLayout = findViewById<LinearLayout>(R.id.endGameLayout)

        binding.endGameLayout.visibility = View.VISIBLE


    }
 }