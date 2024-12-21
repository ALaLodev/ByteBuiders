package com.example.bytebuilders.vistamodelo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Ensure this import is present
import com.example.bytebuilders.application.RoomByteBuilders
import com.example.bytebuilders.data.entitys.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch // Import launch for coroutine
import java.time.LocalDate

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
    private val _users = MutableLiveData<List<UserEntity>>()
    val users: LiveData<List<UserEntity>> get() = _users

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
    fun loadAllUser() {

        viewModelScope.launch(Dispatchers.IO) {
            val userList = RoomByteBuilders.db.userDao().getAll()
             _users.postValue(userList)
        }
    }
    /**
     * Prueba para añadir registros masivos
     * Las consultas estan en USERDAO
     * **/
    /**fun insertUserList() {
        val users = listOf(
            UserEntity(namePlayer = "jugador1", puntuacion = "324", fecha = "2024-10-26 14:30:00"),
            UserEntity(namePlayer = "jugador2", puntuacion = "145", fecha = "2024-01-26 14:30:00"),
            UserEntity(namePlayer = "jugador4", puntuacion = "240", fecha = "2024-05-26 14:30:00"),
            UserEntity(namePlayer = "jugador1", puntuacion = "58", fecha = "2024-08-26 14:30:00"),
            UserEntity(namePlayer = "jugador3", puntuacion = "21", fecha = "2024-10-26 14:30:00")
        )
        viewModelScope.launch(Dispatchers.IO) {
            RoomByteBuilders.db.userDao().insertAll(users)
        }
    }**/
}