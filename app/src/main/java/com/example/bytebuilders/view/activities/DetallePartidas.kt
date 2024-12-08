package com.example.bytebuilders.view.activities

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.bytebuilders.R
import com.example.bytebuilders.databinding.DetallePartidasBinding
import com.example.bytebuilders.model.data.entitys.DatosJugador
import com.example.bytebuilders.viewmodel.MainViewModel
import com.example.bytebuilders.viewmodel.VistaFirebase


class DetallePartidas : AppCompatActivity() {

    private lateinit var binding: DetallePartidasBinding
    private val viewModel: MainViewModel by viewModels()
   // private val viewModelFirestore: VistaFirebase by viewModels()
    // Variables para el sonido de clic
    private lateinit var soundPool: SoundPool
    private var soundIdClickNormal: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetallePartidasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar SoundPool y cargar el sonido
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()
        soundIdClickNormal = soundPool.load(this, R.raw.click_normal, 1)

        binding.returnToStart.setOnClickListener {
            // Reproducir sonido de clic normal
            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            finish() // Regresa a la actividad anterior
        }

        // Datos del ViewModel
        /*viewModel.users.observe(this) { users ->
            val userList = users ?: emptyList()
            displayUserDetails(userList)
        }*/
        cargarTodosJugadores()
        // Mensajes de error
        viewModel.errorMessage.observe(this) { errorMsg ->
            if (errorMsg != null) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }

        // Carga todos los usuarios al iniciar
        viewModel.loadAllUsers()
    }
    private fun cargarTodosJugadores() {
        val vistaFirebase = VistaFirebase()

        vistaFirebase.obtenerTopJugadores(
            onResult = { topJugadores ->
                // Llama a displayScores con la lista de jugadores obtenidos
                displayUserDetails(topJugadores)
            },
            onError = { exception ->
                // Manejo de errores
                Log.e("Error", "Error al obtener los jugadores: ${exception.message}")
            }
        )
    }


    private fun displayUserDetails(jugadores: List<DatosJugador>) {
        val container = binding.detailsContainer
        container.removeAllViews() // Limpiar vistas anteriores

        val displayUsers =  jugadores.take(10) // Limitar a 10 usuarios

        for ((index, user) in displayUsers.withIndex()) {
            val detailLayout = layoutInflater.inflate(R.layout.item_user_detail, container, false)

            val positionTextView = detailLayout.findViewById<TextView>(R.id.positionText)
            val scoreTextView = detailLayout.findViewById<TextView>(R.id.scoreText)
            val dateTextView = detailLayout.findViewById<TextView>(R.id.dateText)
            val locationTextView = detailLayout.findViewById<TextView>(R.id.locationText)

            // Establecer datos
            positionTextView.text = "${user.namePlayer}."
            scoreTextView.text = "${user.puntuacion} ${getString(R.string.points)}"
            dateTextView.text = user.fecha
            locationTextView.text = "Lat: ${user.latitude}, Lon: ${user.longitude}"

            container.addView(detailLayout)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
}