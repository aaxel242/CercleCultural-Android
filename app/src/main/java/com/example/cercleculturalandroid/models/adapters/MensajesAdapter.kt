package com.example.cercleculturalandroid.models.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cercleculturalandroid.R
import com.example.cercleculturalandroid.models.clases.Mensajes
import java.text.SimpleDateFormat
import java.util.*

class MensajesAdapter(private val items: List<Mensajes>) :
    RecyclerView.Adapter<MensajesAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("yyyy‑MM‑dd HH:mm", Locale.getDefault())

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUsuario: TextView = view.findViewById(R.id.tvUsuario)
        val tvMensaje: TextView = view.findViewById(R.id.tvMensaje)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val msg = items[position]
        holder.tvUsuario.text = msg.nom_usuari
        holder.tvMensaje.text = msg.missatge
        // Si dataEnviament es Date
        holder.tvFecha.text = msg.dataEnviament?.let { dateFormat.format(it) } ?: ""
    }

    override fun getItemCount(): Int = items.size
}
