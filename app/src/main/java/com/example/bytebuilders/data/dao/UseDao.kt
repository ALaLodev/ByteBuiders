package com.example.bytebuilders.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bytebuilders.data.entitys.UserEntity


@Dao
interface UserDao {

    /**
     * AQUÍ PONEMOS LAS CONSULTAS
     * **/
    @Query("SELECT * FROM UserEntity")
    fun getAll(): List<UserEntity>

    @Query("SELECT * FROM UserEntity WHERE uid IN (:userIds)")
    fun loadAllUser(userIds: IntArray): List<UserEntity>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUser(vararg users: UserEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(users: List<UserEntity>)
    @Delete
    fun delete(user: UserEntity)
}