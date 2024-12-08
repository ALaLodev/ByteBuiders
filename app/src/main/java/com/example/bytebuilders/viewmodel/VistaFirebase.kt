package com.example.bytebuilders.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.bytebuilders.model.data.entitys.DatosJugador
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class VistaFirebase:ViewModel() {
    //Base de datos FireStore
    private var db: FirebaseFirestore= Firebase.firestore//Llamamos a fireStore

    private val _jugador= MutableStateFlow<List<DatosJugador>>(emptyList())
    val jugador:StateFlow<List<DatosJugador>> = _jugador

    //Funcion insertar datos Jugador
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
    // Función para obtener los 4 jugadores con mayor puntuación
    fun obtenerTopJugadores(onResult: (List<DatosJugador>) -> Unit, onError: (Exception) -> Unit) {
        db.collection("Jugadores")
            .orderBy("puntuacion", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(4)
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
}