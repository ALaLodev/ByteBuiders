package com.example.bytebuilders.model.data.entitys

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

class DatosJugador (

    /**
     *Las variables de la tabla, data class entre paréntesis()
     * Id
     * nombreJugador
     * puntuacion
     * fechaP
     * localizacion
     */
     //val uid: Int = 0,
     val namePlayer: String? = "",
     val puntuacion: Int = 0,
     val fecha: String? = "",
     val latitude: Double = 0.0,
     val longitude: Double = 0.0

)