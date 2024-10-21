package com.example.bytebuilders.vistaModelo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Ensure this import is present
import com.example.bytebuilders.application.RoomByteBuilders
import com.example.bytebuilders.data.entitys.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch // Import launch for coroutine

/**
 * hay que implementar estas rutinas en el build.grandle
 * implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1" // or the latest version
 * implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2" // or the latest version
 *
 * **/
class MainViewModel : ViewModel() {
    /**
     * Prueba para añadir un registro
     * Las consultas estan en USERDAO
     * **/
    /**
     * Método para insertar un registro en la base de datos
     * @param namePlayer Nombre del jugador
     * @param puntuacion Puntuación del jugador
     * @param fecha Fecha del registro
     */


    fun insertUser(namePlayer: String, puntuacion: String, fecha: String) {
        val userEntity = UserEntity(
            namePlayer = namePlayer,
            puntuacion = puntuacion,
            fecha = fecha // Usa el parámetro de fecha pasado
        )
        viewModelScope.launch(Dispatchers.IO) {
            RoomByteBuilders.db.userDao().insertUser(userEntity)
        }
    }

}