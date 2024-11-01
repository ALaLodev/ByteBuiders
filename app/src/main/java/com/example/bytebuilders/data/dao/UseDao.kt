package com.example.bytebuilders.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bytebuilders.data.entitys.UserEntity

@Dao
interface UserDao {

    // Consulta suspend para obtener todos los usuarios
    @Query("SELECT * FROM UserEntity")
    suspend fun getAll(): List<UserEntity>

    // Consulta suspend para obtener un grupo de usuarios por sus IDs
    @Query("SELECT * FROM UserEntity WHERE uid IN (:userIds)")
    suspend fun loadAllUser(userIds: IntArray): List<UserEntity>

    // Inserta uno o más usuarios de manera asíncrona
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(vararg users: UserEntity)

    // Inserta una lista de usuarios de manera asíncrona
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(users: List<UserEntity>)

    // Elimina un usuario de manera asíncrona
    @Delete
    suspend fun delete(user: UserEntity)

    // Obtener los 4 mejores puntajes ordenados de mayor a menor
    @Query("SELECT * FROM UserEntity ORDER BY puntuacion DESC LIMIT 4")
    suspend fun getTopScores(): List<UserEntity>

}
