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

class MainViewModel : ViewModel() {

    private val _users = MutableLiveData<List<UserEntity>>()
    val users: LiveData<List<UserEntity>> get() = _users

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
                //  mostrar un mensaje de error en el Logcat
                Log.e("MainViewModel", "Error al insertar usuario: ${e.message}")
            }
        }
    }
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun loadAllUser () {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // obtener la lista de usuarios desde la base de datos
                val userList = RoomByteBuilders.db.userDao().getAll()
                _users.postValue(userList)
            } catch (e: Exception) {
                // Manejo de errores: captura cualquier excepción y establece un mensaje de error
                Log.e("User ViewModel", "Error al cargar usuarios: ${e.message}")
                _errorMessage.postValue("No se pudo cargar la lista de usuarios. Intenta nuevamente.")
            }
        }
    }


}
