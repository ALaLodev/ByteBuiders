package com.example.bytebuilders.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bytebuilders.application.RoomByteBuilders
import com.example.bytebuilders.model.data.entitys.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    fun insertUser(namePlayer: String, puntuacion: Int, fecha: String) {
        val userEntity = UserEntity(
            namePlayer = namePlayer,
            puntuacion = puntuacion,
            fecha = fecha
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                RoomByteBuilders.db.userDao().insertUser(userEntity)
            } catch (e: Exception) {
                // Manejar la excepción (por ejemplo, mostrar un mensaje de error en el Logcat)
                Log.e("MainViewModel", "Error al insertar usuario: ${e.message}")
            }
        }
    }

}
