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
                // excepción (por ejemplo, mostrar un mensaje de error en el Logcat)
                Log.e("MainViewModel", "Error al insertar usuario: ${e.message}")
            }
        }
    }
    fun loadAllUser() {

        viewModelScope.launch(Dispatchers.IO) {
            val userList = RoomByteBuilders.db.userDao().getAll()
            _users.postValue(userList)
        }
    }

}
