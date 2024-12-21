package com.example.bytebuilders.application

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.room.Room
import com.example.bytebuilders.model.data.database.AppDataBase
import com.example.bytebuilders.view.utils.AppLifecycleObserver

class RoomByteBuilders : Application() {

    override fun onCreate() {
        super.onCreate()
        // Inicializa la instancia de la base de datos
        db = getDatabase(this)

        // Registra el observador del ciclo de vida global para manejar el estado de la música
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver())
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        // Variable para acceder a la base de datos
        lateinit var db: AppDataBase
            private set

        // Método para obtener la instancia de la base de datos de forma segura
        fun getDatabase(application: Application): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    application.applicationContext,
                    AppDataBase::class.java,
                    "jugador-historico"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
