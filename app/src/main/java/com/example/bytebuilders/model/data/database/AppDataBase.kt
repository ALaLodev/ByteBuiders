package com.example.bytebuilders.model.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bytebuilders.model.data.dao.UserDao
import com.example.bytebuilders.model.data.entitys.UserEntity

/**
 *
 * ClASE ABASTRACTA
 * Version con cada modificacion o insercion aumentará
 * Version 3 añadimos longitud y latitud
 */

@Database(entities = [UserEntity::class],version = 3)
abstract class AppDataBase :RoomDatabase(){
    abstract fun userDao(): UserDao// Nuestra interface
}