package com.example.bytebuilders.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bytebuilders.application.RoomByteBuilders
import com.example.bytebuilders.model.data.entitys.UserEntity
import com.example.bytebuilders.view.activities.LocationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    // LiveData para todos los usuarios (historial de puntuaciones)
    private val _users = MutableLiveData<List<UserEntity>>()
    val users: LiveData<List<UserEntity>> get() = _users

    // LiveData para las mejores puntuaciones (ranking actual)
    private val _topScores = MutableLiveData<List<UserEntity>>()
    val topScores: LiveData<List<UserEntity>> get() = _topScores

    // LiveData para mensajes de error
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    // Función para insertar un nuevo usuario en la base de datos
    fun insertUser(namePlayer: String, puntuacion: Int, fecha: String, locationData: LocationData) {
        val userEntity = UserEntity(
            namePlayer = namePlayer,
            puntuacion = puntuacion,
            fecha = fecha,
            latitude = locationData.latitude,
            longitude = locationData.longitude,
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                RoomByteBuilders.db.userDao().insertUser(userEntity)
            } catch (e: Exception) {
                // Mostrar un mensaje de error en el Logcat
                Log.e("MainViewModel", "Error al insertar usuario: ${e.message}")
                _errorMessage.postValue("Error al insertar usuario. Intenta nuevamente.")
            }
        }
    }

    // Función para cargar todos los usuarios (historial de puntuaciones)
    fun loadAllUsers() {
        viewModelScope.launch {
            try {
                val userList = withContext(Dispatchers.IO) {
                    RoomByteBuilders.db.userDao().getAllUsers()
                }
                _users.postValue(userList)
            } catch (e: Exception) {
                // Manejo de errores: captura cualquier excepción y establece un mensaje de error
                Log.e("MainViewModel", "Error al cargar usuarios: ${e.message}")
                _errorMessage.postValue("No se pudo cargar la lista de usuarios. Intenta nuevamente.")
            }
        }
    }

    // Función para cargar las mejores puntuaciones (ranking actual)
    fun loadTopScores() {
        viewModelScope.launch {
            try {
                val topScoresList = withContext(Dispatchers.IO) {
                    RoomByteBuilders.db.userDao().getTopScores()
                }
                _topScores.postValue(topScoresList)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error al cargar las mejores puntuaciones: ${e.message}")
                _errorMessage.postValue("No se pudo cargar las mejores puntuaciones. Intenta nuevamente.")
            }
        }
    }
}
