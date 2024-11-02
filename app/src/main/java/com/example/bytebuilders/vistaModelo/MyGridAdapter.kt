package com.example.bytebuilders.vistamodelo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.bytebuilders.R
import com.example.bytebuilders.data.entitys.UserEntity

class MyGridAdapter(private val context: Context, private val users: List<UserEntity?>) : BaseAdapter() {

    override fun getCount(): Int {
        return users.size
    }

    override fun getItem(position: Int): UserEntity? {
        return users[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.grid_item_layout, parent, false)

        val textViewNamePlayer: TextView = view.findViewById(R.id.textViewNamePlayer)
        val textViewPuntuacion: TextView = view.findViewById(R.id.textViewPuntuacion)
        val textViewFecha: TextView = view.findViewById(R.id.textViewFecha)

        val user = users[position]
        textViewNamePlayer.text = user?.namePlayer ?: "Nombre desconocido"
        textViewPuntuacion.text = user?.puntuacion?.toString() ?: "Puntuación desconocida"
        textViewFecha.text = user?.fecha ?: "Fecha desconocida"

        return view
    }
}