package com.example.bytebuilders.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bytebuilders.model.data.entitys.DatosJugador
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.converter.gson.GsonConverterFactory

interface FirebaseApiService {
    @GET("commonReward")
    suspend fun getCommonReward(): Response<PremioComunResponse>
}

data class VictoryImageResponse(val url: String)


//Cambiamos la propiedad a "galahad" con @SerializedName("premio") para mapear la clave "premio" en el JSON al campo 'galahad'

data class PremioComunResponse(
    @SerializedName("premio")
    val galahad: String
)

object RetrofitInstance {
    // Apuntamos la URL de la Firestore
    private const val BASE_URL =
        "https://firestore.googleapis.com/v1/projects/bytebuilders-uoc/databases/(default)/documents/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()) //Uso Gson porque Moshi no me termina de funcionar
        .build()

    val apiService: FirebaseApiService = retrofit.create(FirebaseApiService::class.java)
}

class VistaFirebase : ViewModel() {

    // cargamos la BD FireStore
    private var db: FirebaseFirestore = Firebase.firestore

    private val _jugador = MutableStateFlow<List<DatosJugador>>(emptyList())
    val jugador: StateFlow<List<DatosJugador>> = _jugador

    // Insertamos un jugador en Firestore
    fun agregarJugador(jugador: DatosJugador) {
        val jugadoresCollection = db.collection("Jugadores") // Nombre de la coleccion

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

//    fun obtenerJugadores( // Lo gestionamos todo mediante obtenerTopJugadores()
//        onResult: (List<DatosJugador>) -> Unit,
//        onError: (Exception) -> Unit
//    ) {
//        db.collection("Jugadores")
//            .get()
//            .addOnSuccessListener { docs ->
//                val lista = docs.mapNotNull { doc ->
//                    val namePlayer = doc.getString("namePlayer") ?: ""
//                    val puntuacion = doc.getLong("puntuacion")?.toInt() ?: 0
//                    val fecha = doc.getString("fecha") ?: ""
//                    val latitude = doc.getDouble("latitude") ?: 0.0
//                    val longitude = doc.getDouble("longitude") ?: 0.0
//
//                    DatosJugador(namePlayer, puntuacion, fecha, latitude, longitude)
//                }
//                onResult(lista)
//            }
//            .addOnFailureListener { e ->
//                Log.w("VistaFirebase", "Error al obtener documentos", e)
//                onError(e)
//            }
//    }

    fun obtenerTopJugadores(
        onResult: (List<DatosJugador>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("Jugadores")
            .get()
            .addOnSuccessListener { docs ->
                val topJugadores = mutableListOf<DatosJugador>()
                for (doc in docs) {
                    val namePlayer = doc.getString("namePlayer") ?: ""
                    val puntuacion = doc.getLong("puntuacion")?.toInt() ?: 0
                    val fecha = doc.getString("fecha") ?: ""
                    val latitude = doc.getDouble("latitude") ?: 0.0
                    val longitude = doc.getDouble("longitude") ?: 0.0

                    topJugadores.add(DatosJugador(namePlayer, puntuacion, fecha, latitude, longitude))
                }
                onResult(topJugadores)
            }
            .addOnFailureListener { e ->
                onError(e)
            }
    }

    // Obtiene el premio comun (Retrofit)
    fun obtenerPremioComun(
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val resp = RetrofitInstance.apiService.getCommonReward()
                if (resp.isSuccessful) {
                    val premioObj = resp.body()
                    // "galahad" = la clave mapeada por @SerializedName("premio")
                    val galahadValue = premioObj?.galahad ?: "No se encontró premio"
                    onSuccess(galahadValue)
                } else {
                    Log.d("VistaFirebase", "Response JSON: ${resp.errorBody()?.string()}")
                    onFailure(Exception("Error al obtener el premio común"))
                }
            } catch (ex: Exception) {
                onFailure(ex)
            }
        }
    }

//    fun consumirPremioComun( // Finalmente no es necesario, se gestiona desde la GameaActivity la visibilidad.
//        onSuccess: () -> Unit,
//        onFailure: (Exception) -> Unit
//    ) {
//        // Formato Firestore:
//        //   Collection: "Premios"
//        //   Document: "commonRewardDoc"
//        val docRef = db.collection("Premios").document("commonRewardDoc")
//        docRef.delete()
//            .addOnSuccessListener {
//                Log.d("VistaFirebase", "Premio común consumido y eliminado.")
//                onSuccess()
//            }
//            .addOnFailureListener { e ->
//                Log.w("VistaFirebase", "Error al consumir premio común", e)
//                onFailure(e)
//            }
//    }
}
