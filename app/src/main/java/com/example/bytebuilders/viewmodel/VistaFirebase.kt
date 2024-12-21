package com.example.bytebuilders.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bytebuilders.model.data.entitys.DatosJugador
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import com.squareup.retrofit2.Retrofit
import com.squareup.retrofit2.converter.moshi.MoshiConverterFactory
import kotlinx.coroutines.launch
import retrofit2.Response

// Retrofit interface
interface FirebaseApiService {
    @GET("victoryImage") // Suponiendo que este es el endpoint de tu API
    suspend fun getVictoryImage(): Response<VictoryImageResponse>
}

// Retrofit response data class
data class VictoryImageResponse(
    val url: String // La URL de la imagen de victoria
)

object RetrofitInstance {
    private const val BASE_URL = "https://your-api-url.com/" // URL de tu API

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create()) // Usamos Moshi como convertidor JSON
        .build()

    val apiService: FirebaseApiService = retrofit.create(FirebaseApiService::class.java)
}

class VistaFirebase : ViewModel() {
    //Base de datos FireStore
    private var db: FirebaseFirestore = Firebase.firestore // Llamamos a fireStore

    private val _jugador = MutableStateFlow<List<DatosJugador>>(emptyList())
    val jugador: StateFlow<List<DatosJugador>> = _jugador

    // Función insertar datos Jugador
    fun agregarJugador(jugador: DatosJugador) {
        val jugadoresCollection = db.collection("Jugadores") // Nombre de la colección

        jugadoresCollection.add(jugador)
            .addOnSuccessListener { documentReference ->
                Log.d("VistaFirebase", "Jugador agregado con ID: ${documentReference.id}")
                // Actualiza el estado de _jugador si es necesario
                _jugador.value = _jugador.value + jugador
            }
            .addOnFailureListener { e ->
                Log.w("VistaFirebase", "Error al agregar jugador", e)
            }
    }

    // Función para obtener todos los jugadores
    fun obtenerJugadores(onResult: (List<DatosJugador>) -> Unit, onError: (Exception) -> Unit) {
        db.collection("Jugadores")
            .get()
            .addOnSuccessListener { documents ->
                val topJugadores = mutableListOf<DatosJugador>()
                for (document in documents) {
                    val namePlayer = document.getString("namePlayer") ?: ""
                    val puntuacion = document.getLong("puntuacion")?.toInt() ?: 0
                    val fecha = document.getString("fecha") ?: ""
                    val latitude = document.getDouble("latitude") ?: 0.0
                    val longitude = document.getDouble("longitude") ?: 0.0

                    val jugador = DatosJugador(namePlayer, puntuacion, fecha, latitude, longitude)
                    topJugadores.add(jugador)
                }
                onResult(topJugadores) // Llama al callback con la lista de jugadores
            }
            .addOnFailureListener { exception ->
                Log.w("VistaFirebase", "Error getting documents: ", exception)
                onError(exception) // Llama al callback de error
            }
    }

    // Función para obtener la URL de la imagen de victoria usando Retrofit
    fun obtenerImagenVictoriaDesdeApi(onResult: (String?) -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiService.getVictoryImage()
                if (response.isSuccessful) {
                    val imageUrl = response.body()?.url // Extraemos la URL de la imagen desde la respuesta
                    onResult(imageUrl)
                } else {
                    onError(Exception("Error en la respuesta de la API"))
                }
            } catch (exception: Exception) {
                onError(exception)
            }
        }
    }
}
