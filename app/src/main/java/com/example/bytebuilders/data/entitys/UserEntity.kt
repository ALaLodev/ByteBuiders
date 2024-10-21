package com.example.bytebuilders.data.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity (
    /**
     *Las variables de la tabla, data class entre paréntesis()
     * Id
     * nombreJugador
     * puntuacion
     * fechaP
     *
     */
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "nombrejugador") val namePlayer: String?,
    @ColumnInfo(name = "puntuacion") val puntuacion: String?,
    @ColumnInfo(name = "fecha") val fecha: String?
)