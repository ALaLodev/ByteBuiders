package com.example.bytebuilders.model.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bytebuilders.model.data.dao.UserDao
import com.example.bytebuilders.model.data.entitys.UserEntity

/**
 *
 * ClASE ABASTRACTA
 * Version con cada modificacion o insercion aumentará
 */

@Database(entities = [UserEntity::class],version = 2)
abstract class AppDataBase :RoomDatabase(){
    abstract fun userDao(): UserDao// Nuestra interface
}