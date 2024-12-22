package com.example.bytebuilders.view.activities

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.bytebuilders.R
import com.example.bytebuilders.databinding.DetallePartidasBinding
import com.example.bytebuilders.model.data.entitys.DatosJugador
import com.example.bytebuilders.viewmodel.MainViewModel
import com.example.bytebuilders.viewmodel.VistaFirebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DetallePartidas : AppCompatActivity() {

    private lateinit var binding: DetallePartidasBinding
    private val viewModel: MainViewModel by viewModels()
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

        viewModel.errorMessage.observe(this) { errorMsg ->
            if (errorMsg != null) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }

        cargarTodosJugadores()

        // Carga usuarios en la BD local (si lo usas)
        viewModel.loadAllUsers()
    }

    private fun cargarTodosJugadores() {
        val vistaFirebase = VistaFirebase()

        vistaFirebase.obtenerTopJugadores(
            onResult = { jugadores ->
                displayUserDetails(jugadores)
            },
            onError = { exception ->
                Log.e("Error", "Error al obtener los jugadores: ${exception.message}")
            }
        )
    }

    private fun displayUserDetails(jugadores: List<DatosJugador>) {
        val container = binding.detailsContainer
        container.removeAllViews()

        // 1) Encontrar quién jugó más recientemente (fecha más grande).
        //    Asumimos formato "yyyy-MM-dd HH:mm:ss"
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val lastPlayer = jugadores.maxByOrNull { LocalDateTime.parse(it.fecha, formatter) }

        // 2) Ordenar por puntuacion de mayor a menor
        val sortedUsers = jugadores.sortedByDescending { it.puntuacion }

        // 3) Tomamos los primeros 10 si deseas límite
        val displayUsers = sortedUsers.take(10)

        for ((_, user) in displayUsers.withIndex()) {
            val detailLayout = layoutInflater.inflate(R.layout.item_user_detail, container, false)

            val positionTextView = detailLayout.findViewById<TextView>(R.id.positionText)
            val scoreTextView = detailLayout.findViewById<TextView>(R.id.scoreText)
            val dateTextView = detailLayout.findViewById<TextView>(R.id.dateText)
            val locationTextView = detailLayout.findViewById<TextView>(R.id.locationText)
            val trofeoImageView = detailLayout.findViewById<ImageView>(R.id.trofeoImage)

            // Rellenar datos
            positionTextView.text = "${user.namePlayer}-${user.fecha} - ${user.puntuacion} ${getString(R.string.points)}"
            scoreTextView.text = ""
            dateTextView.text = ""
            locationTextView.text = "Lat: ${user.latitude}, Lon: ${user.longitude}"

            // 4) Si este usuario es el último que jugó, mostramos el trofeo a la derecha
            if (user == lastPlayer) {
                trofeoImageView.setImageResource(R.drawable.trofeo)
                trofeoImageView.visibility = android.view.View.VISIBLE
            } else {
                trofeoImageView.visibility = android.view.View.GONE
            }

            container.addView(detailLayout)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
}
