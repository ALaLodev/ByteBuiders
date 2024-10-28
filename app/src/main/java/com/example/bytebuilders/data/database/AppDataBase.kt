package com.example.bytebuilders.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bytebuilders.data.dao.UserDao
import com.example.bytebuilders.data.entitys.UserEntity

/**
 *
 * ClASE ABASTRACTA
 * Version con cada modificacion o insercion aumentará
 */

@Database(entities = [UserEntity::class],version = 1)
abstract class AppDataBase :RoomDatabase(){
    abstract fun userDao():UserDao// Nuestra interface
}