package com.example.bytebuilders.model.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bytebuilders.model.data.entitys.UserEntity

@Dao
interface UserDao {

    // Consulta suspend para obtener todos los usuarios
    @Query("SELECT * FROM UserEntity")
    suspend fun getAll(): List<UserEntity>

    // Consulta suspend para obtener un grupo de usuarios por sus IDs
    @Query("SELECT * FROM UserEntity WHERE uid IN (:userIds)")
    suspend fun loadAllUser(userIds: IntArray): List<UserEntity>

    // Inserta uno o más usuarios de manera asíncrona
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // Inserta una lista de usuarios de manera asíncrona
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(users: List<UserEntity>)

    // Elimina un usuario de manera asíncrona
    @Delete
    suspend fun delete(user: UserEntity)

    @Query("SELECT * FROM userentity ORDER BY puntuacion DESC, fecha ASC LIMIT 10")
    fun getTopScores(): List<UserEntity>

    @Query("SELECT * FROM userentity ORDER BY fecha DESC")
    fun getAllUsers(): List<UserEntity>
}
