package com.example.bytebuilders

import android.os.Bundle
import android.widget.GridView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.bytebuilders.vistamodelo.MainViewModel
import androidx.lifecycle.Observer
import com.example.bytebuilders.vistamodelo.MyGridAdapter

class DetallePartidas : AppCompatActivity()  {

    private val modelo: MainViewModel by viewModels()
    private lateinit var gridView: GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detalle_partidas)

        gridView = findViewById(R.id.griddetalle)

        // Observa los datos del ViewModel
        modelo.users.observe(this) { users ->
            // Maneja el caso en que users puede ser nulo
            val adapter = MyGridAdapter(this, users ?: emptyList()) // Aquí pasamos la lista de usuarios
            gridView.adapter = adapter
        }

        // Carga todos los usuarios al iniciar
        modelo.loadAllUser ()
    }
 }