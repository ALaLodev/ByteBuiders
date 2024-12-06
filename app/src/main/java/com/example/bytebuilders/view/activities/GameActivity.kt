package com.example.bytebuilders.view.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.location.Location
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
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
import java.io.IOException
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
    private val CALENDAR_PERMISSION_REQUEST_CODE = 3
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 2
    private var startTime: LocalDateTime? = null // Registro del tiempo de inicio del juego

    // Lanzador captura de pantalla
    private lateinit var saveScreenshotLauncher: ActivityResultLauncher<Intent>

    // Variables sonidos de clic
    private lateinit var soundPool: SoundPool
    private var soundIdClickMas: Int = 0
    private var soundIdClickMenos: Int = 0
    private var soundIdClickNormal: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel() // Crear el canal de notificaciones
        binding = ActivityGameBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        startTime = LocalDateTime.now() // Registrar el tiempo de inicio del juego
        sharedPreferences = getSharedPreferences("GameSettings", Context.MODE_PRIVATE)
        volumeLevel = sharedPreferences.getInt("volumeLevel", 50)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.attemptText

        // Configurar SoundPool y cargar los sonidos
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttributes)
            .build()
        soundIdClickMas = soundPool.load(this, R.raw.click_mas, 1)
        soundIdClickMenos = soundPool.load(this, R.raw.click_menos, 1)
        soundIdClickNormal = soundPool.load(this, R.raw.click_normal, 1)

        startNewRound()
        binding.returnToStart.visibility = View.GONE
        binding.saveScreenshotButton.visibility = View.GONE // Ocultar el boton inicialmente

        binding.plusButton.setOnClickListener {
            if (selectedNumberValue < 12) {
                selectedNumberValue++
                binding.selectedNumber.text = selectedNumberValue.toString()
                // Reproducir sonido de clic mas
                soundPool.play(soundIdClickMas, 1f, 1f, 0, 0, 1f)
            }
        }
        binding.minusButton.setOnClickListener {
            if (selectedNumberValue > 1) {
                selectedNumberValue--
                binding.selectedNumber.text = selectedNumberValue.toString()
                // Reproducir sonido de clic menos
                soundPool.play(soundIdClickMenos, 1f, 1f, 0, 0, 1f)
            }
        }
        binding.sendButton.setOnClickListener {
            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            checkAnswer()
        }
        binding.returnToStart.setOnClickListener {
            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("Final_Score", points) // Pasa la puntuacion final al siguiente activity
            startActivity(intent)
        }

        binding.pauseButton.setOnClickListener {
            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            val intent = Intent(this, PauseActivity::class.java)
            startActivity(intent)
        }

        // Ocultar el layout de fin de juego al inicio
        binding.endGameLayout.visibility = View.GONE

        // Inicializar el ActivityResultLauncher para guardar captura
        saveScreenshotLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val uri = result.data?.data
                if (uri != null) {
                    saveScreenshot(uri)
                }
            } else {
                Toast.makeText(
                    this,
                    "No se selecciono ubicacion para guardar la captura.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Listener del boton de guardar captura
        binding.saveScreenshotButton.setOnClickListener {
            soundPool.play(soundIdClickNormal, 1f, 1f, 0, 0, 1f)
            promptForSavingScreenshot()
        }
        requestLocationPermission()

    } // fin onCreate

    private fun setVolume(volume: Int) {
        val maxVolume = 100
        val volumeAdjustment =
            (1 - (Math.log((maxVolume - volume).toDouble()) / Math.log(maxVolume.toDouble()))).toFloat()
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
        soundPool.release() // Liberar SoundPool
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun pauseGame() {
        // Pausar logica del juego
    }

    private fun resumeGame() {
        // Reanudar logica del juego
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
                if (!gameEnded) {
                    binding.feedback.text = ""
                }
            }
        }
    }

    private fun showCardForCorrectAnswer(number: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            binding.hiddenCard.setImageResource(
                resources.getIdentifier(
                    "card_$number",
                    "drawable",
                    packageName
                )
            )
            delay(2000)

            if (!gameEnded) {
                hideCard()
                binding.feedback.text = ""
            }
        }
    }

    private fun showCardForIncorrectAnswer(number: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            binding.hiddenCard.setImageResource(
                resources.getIdentifier(
                    "card_$number",
                    "drawable",
                    packageName
                )
            )
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
            binding.sendButton.visibility = View.GONE
            binding.plusButton.visibility = View.GONE
            binding.minusButton.visibility = View.GONE
            binding.selectedNumber.visibility = View.GONE

            // Mostrar el mensaje de fin de juego
            binding.gameOverMessage.text = getString(R.string.game_over, points)

            // Mostrar el layout de fin de juego
            binding.endGameLayout.visibility = View.VISIBLE

            // Mostrar el boton de guardar captura y volver al inicio
            binding.saveScreenshotButton.visibility = View.VISIBLE
            binding.returnToStart.visibility = View.VISIBLE

            // Ocultar el feedback
            binding.feedback.visibility = View.GONE

            // Mostrar la carta final
            binding.hiddenCard.setImageResource(
                resources.getIdentifier(
                    "card_$randomNumber",
                    "drawable",
                    packageName
                )
            )
            // Registrar al ganador y mostrar la notificacion de victoria
            registerWinner()
        }
    }

    private fun registerWinner() {
        Log.d("GameActivity", "Registrando ganador...")

        val winnerName = "Jugador"
        val winnerScore = points
        val winnerDateTime =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        // Calcular tiempo transcurrido
        val elapsedTime = Duration.between(startTime, LocalDateTime.now()).seconds
        val formattedTime = "${elapsedTime / 60}m ${elapsedTime % 60}s"

        // Obtener ubicacion si se tiene permiso
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                val latitude = location?.latitude ?: 0.0
                val longitude = location?.longitude ?: 0.0

                val locationData = LocationData(
                    latitude,
                    longitude
                )
                // Llamar al ViewModel para registro
                modelo.insertUser(winnerName, winnerScore, winnerDateTime, locationData)
            }.addOnFailureListener { e ->
                // Manejo de errores al obtener la ubicacion
                Log.e("GameActivity", "Error al obtener la ubicacion: ${e.message}")
            }
        } else {
            // No se tiene permiso de ubicacion, registrar con coordenadas por defecto
            val locationData = LocationData(
                0.0, // Latitud por defecto
                0.0  // Longitud por defecto
            )
            modelo.insertUser(winnerName, winnerScore, winnerDateTime, locationData)
        }

        // Mostrar notificacion de victoria
        showVictoryNotification(formattedTime)
        // Mostrar el tiempo de resolucion en un Toast
        showTimeToast(formattedTime)
        // Solicitar permiso de calendario para crear el evento
        requestCalendarPermission()
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // No se tiene permiso, solicitarlo
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun requestCalendarPermission() {
        val permissions = arrayOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                CALENDAR_PERMISSION_REQUEST_CODE
            )
        } else {
            // Ya se tienen los permisos, crear el evento
            createCalendarEvent()
            showCalendarNotification()
        }
    }

    private fun showCalendarNotification() {
        
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si permiso no otorgado, mostrar mensaje o solicitar permiso
            Toast.makeText(
                this,
                "Permiso de notificaciones denegado. No se puede mostrar la notificacion.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val notification = NotificationCompat.Builder(this, "game_notifications")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Evento registrado")
            .setContentText("Se ha registrado correctamente el evento en el calendario.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(this)) {
            notify(1002, notification) // ID único para esta notificacion
        }
    }

    private fun createCalendarEvent() {
        val calendarId = getPrimaryCalendarId()
        if (calendarId == null) {
            Toast.makeText(
                this,
                "No se encontro un calendario para registrar el evento.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, System.currentTimeMillis())
            put(CalendarContract.Events.DTEND, System.currentTimeMillis() + 60 * 60 * 1000)
            put(CalendarContract.Events.TITLE, "Victoria en Guess Wars")
            put(CalendarContract.Events.DESCRIPTION, "Puntuacion final: $points puntos.")
            put(CalendarContract.Events.CALENDAR_ID, calendarId)
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

    private fun getPrimaryCalendarId(): Long? {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.IS_PRIMARY
        )
        val uri = CalendarContract.Calendars.CONTENT_URI
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            while (it.moveToNext()) {
                val id =
                    it.getLong(it.getColumnIndexOrThrow(CalendarContract.Calendars._ID))
                val isPrimary =
                    it.getInt(it.getColumnIndexOrThrow(CalendarContract.Calendars.IS_PRIMARY))
                if (isPrimary == 1) {
                    return id
                }
            }
        }
        return null
    }

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

    private fun showVictoryNotification(elapsedTime: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Si el permiso no está otorgado, solicitar permiso
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
                return
            }
        }
        // Cargar el logo de la notificacion desde los recursos
        val logoBitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.guesswarslogo
        )

        val notification = NotificationCompat.Builder(this, "game_notifications")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ícono pequeño de la notificacion
            .setLargeIcon(logoBitmap) // Ícono grande de la notificacion
            .setContentTitle("¡Victoria!")
            .setContentText("La partida ha durado $elapsedTime.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(this)) {
            notify(1001, notification)
        }
    }

    private fun showTimeToast(elapsedTime: String) {
        // Muestra Toast con el tiempo de resolucion
        Toast.makeText(this, "¡Se acabo! La partida ha durado: $elapsedTime", Toast.LENGTH_LONG)
            .show()
    }

    // Manejar el resultado de la solicitud de permisos
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permiso de ubicacion concedido
                } else {
                    // Permiso de ubicacion denegado
                    Toast.makeText(this, "Permiso de ubicacion denegado.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            CALENDAR_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val perms = permissions.zip(grantResults.toTypedArray()).toMap()
                    val readCalendarGranted =
                        perms[Manifest.permission.READ_CALENDAR] == PackageManager.PERMISSION_GRANTED
                    val writeCalendarGranted =
                        perms[Manifest.permission.WRITE_CALENDAR] == PackageManager.PERMISSION_GRANTED

                    if (readCalendarGranted && writeCalendarGranted) {
                        // Ambos permisos concedidos
                        createCalendarEvent()
                        showCalendarNotification()
                    } else {
                        // Permiso denegado
                        Toast.makeText(this, "Permiso de calendario denegado.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    // Solicitud cancelada
                    Toast.makeText(this, "Permiso de calendario denegado.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            //NOTIFICATION_PERMISSION_REQUEST_CODE -> {
            // Para manejar el permiso de notificaciones, en principio no hace falta
        }
    }

    // Guardar la captura de pantalla
    private fun promptForSavingScreenshot() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/png"
            putExtra(Intent.EXTRA_TITLE, "victory_screenshot.png")
        }
        saveScreenshotLauncher.launch(intent)
    }

    private fun saveScreenshot(uri: Uri) {
        val bitmap = captureScreenshot()

        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                Toast.makeText(this, "Captura guardada correctamente.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar la captura.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun captureScreenshot(): Bitmap {
        val view = window.decorView.rootView
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
}