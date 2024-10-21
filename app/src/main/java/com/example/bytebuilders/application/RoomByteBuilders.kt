package com.example.bytebuilders.application

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.example.bytebuilders.data.database.AppDataBase

class RoomByteBuilders : Application() {

    companion object {
        lateinit var db: AppDataBase
    }

    override fun onCreate() {
        super.onCreate()
        try {
            db = Room.databaseBuilder(
                applicationContext,
                AppDataBase::class.java,
                "jugador-historico"
            ).build()
        } catch (e: Exception) {
            // Maneja la excepción
            Log.e("RoomByteBuilders", "Error al crear la base de datos", e)
        }
    }

    /* override fun onTerminate() {
         super.onTerminate()
         // Cierra la base de datos cuando la aplicación se detenga
         db.close()
     }*/
}