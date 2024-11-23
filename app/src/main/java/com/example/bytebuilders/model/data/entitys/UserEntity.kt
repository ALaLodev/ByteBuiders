package com.example.bytebuilders.model.data.entitys

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bytebuilders.view.activities.LocationData

@Entity
data class UserEntity (
    /**
     *Las variables de la tabla, data class entre paréntesis()
     * Id
     * nombreJugador
     * puntuacion
     * fechaP
     * localizacion
     */
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "nombrejugador") val namePlayer: String?,
    @ColumnInfo(name = "puntuacion") val puntuacion: Int,
    @ColumnInfo(name = "fecha") val fecha: String?,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double
)