package com.example.bytebuilders.view.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.bytebuilders.R
import com.example.bytebuilders.databinding.ActivityGameBinding
import com.example.bytebuilders.viewmodel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone

class GameActivity : BaseActivity() {

    private lateinit var binding: ActivityGameBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var randomNumber = 0
    private var selectedNumberValue = 1
    private var attemptNumber = 1
    private var roundsNumber = 1
    private var points = 0
    private var gameEnded = false
    private val totalRounds = 4
    private val modelo: MainViewModel by viewModels()

    private lateinit var sharedPreferences: SharedPreferences
    private var volumeLevel: Int = 50
    private var mediaPlayer: MediaPlayer? = null
    private val PERMISSION_REQUEST_CODE = 1


    // Lanzador para solicitar permisos de ubicación
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                registerWinner()
            } else {
                Toast.makeText(this, "No entra donde debe ", Toast.LENGTH_SHORT).show()
            }
        }

    private val requestCalendarPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                createCalendarEvent()
            } else {
                Toast.makeText(this, "Permiso de calendario denegado", Toast.LENGTH_SHORT).show()
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("GameSettings", Context.MODE_PRIVATE)
        volumeLevel = sharedPreferences.getInt("volumeLevel", 50)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.attemptText

        startNewRound()
        binding.returnToStart.visibility = View.GONE

        binding.plusButton.setOnClickListener {
            if (selectedNumberValue < 12) {
                selectedNumberValue++
                binding.selectedNumber.text = selectedNumberValue.toString()
            }
        }
        binding.minusButton.setOnClickListener {
            if (selectedNumberValue > 1) {
                selectedNumberValue--
                binding.selectedNumber.text = selectedNumberValue.toString()
            }
        }
        binding.sendButton.setOnClickListener {
            checkAnswer()
        }
        binding.returnToStart.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("Final_Score", points) // Pasa la puntuación final al siguiente activity
            startActivity(intent)
        }

        binding.pauseButton.setOnClickListener {
            val intent = Intent(this, PauseActivity::class.java)
            startActivity(intent)
        }
        // A partir de Android 6.0 (API nivel 23),
        //  solicitar permisos en tiempo de ejecución
        // Solicitar permisos de ubicación
        requestLocationPermission()


        // Solicitar permisos de calendario
        requestCalendarPermission()

    }//fin oncreate

    private fun requestCalendarPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED -> {
                createCalendarEvent() // Si ya se tiene el permiso, crear el evento
            }
            else -> {
                requestCalendarPermissionLauncher.launch(Manifest.permission.WRITE_CALENDAR) // Solicitar permiso
            }
        }
    }

    private fun createCalendarEvent() {
        val contentResolver = contentResolver
        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, System.currentTimeMillis()) // Fecha y hora de inicio del evento
            put(CalendarContract.Events.DTEND, System.currentTimeMillis() + 60 * 60 * 1000) // Duración del evento (1 hora)
            put(CalendarContract.Events.TITLE, "Victoria de ${"Jugador"}")
            put(CalendarContract.Events.DESCRIPTION, "Puntuación final: $points puntos")
            put(CalendarContract.Events.CALENDAR_ID, 1) // Calendario predeterminado
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        // Insertar el evento en el calendario
        val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)

        if (uri != null) {
            Log.d("Calendar", "Evento registrado: $uri")
            Toast.makeText(this, "Evento registrado en el calendario", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No se pudo registrar el evento", Toast.LENGTH_SHORT).show()
        }
    }
    private fun setVolume(volume: Int) {
        val maxVolume = 100
        val volumeAdjustment = (1 - (Math.log((maxVolume - volume).toDouble()) / Math.log(maxVolume.toDouble()))).toFloat()
        mediaPlayer?.setVolume(volumeAdjustment, volumeAdjustment)
    }

    override fun onResume() {
        super.onResume()
        volumeLevel = sharedPreferences.getInt("volumeLevel", 50)
        setVolume(volumeLevel)
        mediaPlayer?.start()
        if (!gameEnded) {
            resumeGame()
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
        pauseGame()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun pauseGame() {
        // Pausar lógica del juego, por ejemplo, detener corrutinas
    }

    private fun resumeGame() {
        // Reanudar lógica del juego
    }

    private fun startNewRound() {
        randomNumber = (1..12).random()
        selectedNumberValue = 1
        binding.selectedNumber.text = selectedNumberValue.toString()
        attemptNumber = 1
        binding.roundText.text = getString(R.string.round_text, roundsNumber)
        binding.attemptText.text = getString(R.string.attempt_text, attemptNumber)
        binding.hiddenCard.setImageResource(R.drawable.spr_reverso)
    }

    @SuppressLint("DiscouragedApi")
    private fun checkAnswer() {
        if (selectedNumberValue == randomNumber) {
            binding.feedback.text = getString(R.string.feedback_correct)
            val pointsEarned = 5 - attemptNumber
            points += pointsEarned

            showCardForCorrectAnswer(selectedNumberValue)
            nextRoundOrEndGame()
        } else {
            processIncorrectAnswer(selectedNumberValue)
        }
    }

    private fun processIncorrectAnswer(selectedNumberValue: Int) {
        if (selectedNumberValue < randomNumber) {
            binding.feedback.text = getString(R.string.feedback_higher)
        } else {
            binding.feedback.text = getString(R.string.feedback_lower)
        }

        attemptNumber++
        binding.attemptText.text = getString(R.string.attempt_text, attemptNumber)

        if (attemptNumber > 4) {
            binding.feedback.text = getString(R.string.feedback_loss, randomNumber)
            showCardForIncorrectAnswer(randomNumber)
            nextRoundOrEndGame()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                if (!gameEnded) { binding.feedback.text = "" }
            }
        }
    }

    private fun showCardForCorrectAnswer(number: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            binding.hiddenCard.setImageResource(resources.getIdentifier("card_$number", "drawable", packageName))
            delay(2000)

            if (!gameEnded) {
                hideCard()
                binding.feedback.text = ""
            }
        }
    }

    private fun showCardForIncorrectAnswer(number: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            binding.hiddenCard.setImageResource(resources.getIdentifier("card_$number", "drawable", packageName))
            delay(2000)

            if (!gameEnded) {
                hideCard()
                binding.feedback.text = ""
            }
        }
    }

    private fun hideCard() {
        binding.hiddenCard.setImageResource(R.drawable.spr_reverso)
    }

    @SuppressLint("DiscouragedApi")
    private fun nextRoundOrEndGame() {
        if (roundsNumber < totalRounds) {
            roundsNumber++
            startNewRound()
        } else {
            // Fin de juego
            gameEnded = true
            binding.feedback.text = getString(R.string.game_over, points)
            binding.sendButton.visibility = View.GONE
            binding.plusButton.visibility = View.GONE
            binding.minusButton.visibility = View.GONE
            binding.selectedNumber.visibility = View.GONE
            binding.returnToStart.visibility = View.VISIBLE

            // Mostrar la carta final
            binding.hiddenCard.setImageResource(resources.getIdentifier("card_$randomNumber", "drawable", packageName))

            // Mostrar el layout de fin de juego
            val endGameLayout = findViewById<LinearLayout>(R.id.endGameLayout)
            endGameLayout.visibility = View.VISIBLE

            // Registrar al ganador
            registerWinner()
        }
    }
    //Llamada a ganador suceden varias cosas
    //Lllamar pantalla detalle y
    //Llamar a latitud longitud
    //Llamar a calendario
    private fun registerWinner() {
        val winnerName = "Jugador"
        val winnerScore = points
        val winnerDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        // Calcular tiempo transcurrido
        val elapsedTime = Duration.between(startTime, LocalDateTime.now()).seconds
        val formattedTime = "${elapsedTime / 60}m ${elapsedTime % 60}s"

        // Obtener la ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val locationData = LocationData(
                        location.latitude,
                        location.longitude
                    )
                    // Llamar al ViewModel para registrar el ganador
                    modelo.insertUser (winnerName, winnerScore, winnerDateTime,locationData)
                    // Mostrar notificación
                    showVictoryNotification(formattedTime)
                    // Mostrar el tiempo de resolución en un Toast
                    showTimeToast(formattedTime)
                    requestCalendarPermission() // Llama a la función para solicitar permiso de calendario
                } else {
                    //  caso en que no se pudo obtener la ubicación
                    Toast.makeText(this, "No se pudo obtener la ubicación. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                //  caso en que hubo un error al obtener la ubicación
                Toast.makeText(this, "Error al obtener la ubicación: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Solicitar permiso de ubicación
            requestLocationPermission()
        }

    }
    //crear notificación
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "game_notifications",
            "Game Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for game events"
        }
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
    //compartir victoria y notificación
    private fun showVictoryNotification(elapsedTime: String) {
        // Cargar el logo de la notificación desde los recursos
        val logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.guesswarslogo) // Asegúrate de que "logo" sea el nombre del archivo sin la extensión

        val notification = NotificationCompat.Builder(this, "game_notifications")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ícono pequeño de la notificación
            .setLargeIcon(logoBitmap) // Ícono grande de la notificación
            .setContentTitle("¡Victoria!")
            .setContentText("La partida ha durado $elapsedTime.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(this)) {
            notify(1001, notification)
        }
    }

//Mostrar el tiempo transcurrido
private fun showTimeToast(elapsedTime: String) {
    // Muestra un Toast con el tiempo de resolución
    Toast.makeText(this, "¡Se acabó! La partida ha durado: $elapsedTime", Toast.LENGTH_LONG).show()
}
    //Parte de registro latitud longitud
    private fun requestLocationPermission() {
        when {
            // Si ya se tiene el permiso
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                // Ya tienes permiso, registra al ganador
                registerWinner()
            }
            // Si no se tiene el permiso, se solicita
            else -> {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
            }
        }
    }
    //PARTE DE LOCALIZACION
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permiso concedido, registra al ganador
                registerWinner()
            } else {
                // Permiso denegado, informa al usuario
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }







}
