package com.example.cercleculturalandroid.models.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cercleculturalandroid.R
import com.example.cercleculturalandroid.models.clases.Mensajes
import java.text.SimpleDateFormat
import java.util.Locale

class ChatAdapter(
    private val mensajes: MutableList<Mensajes>,
    private val currentUserId: Int
                 ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
        private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    }

    override fun getItemViewType(position: Int): Int {
        return if (mensajes[position].usuari_id == currentUserId) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENT -> SentMessageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_sent, parent, false)
                                                   )
            else -> ReceivedMessageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_received, parent, false)
                                             )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mensaje = mensajes[position]
        when (holder) {
            is SentMessageViewHolder -> bindSentMessage(holder, mensaje)
            is ReceivedMessageViewHolder -> bindReceivedMessage(holder, mensaje)
        }
    }

    private fun bindSentMessage(holder: SentMessageViewHolder, mensaje: Mensajes) {
        holder.mensajeTextView.text = mensaje.missatge
        holder.fechaEnvioTextView.text = dateFormat.format(mensaje.dataEnviament)
    }

    private fun bindReceivedMessage(holder: ReceivedMessageViewHolder, mensaje: Mensajes) {
        holder.nombreTextView.text = mensaje.nom_usuari
        holder.mensajeTextView.text = mensaje.missatge
        holder.fechaEnvioTextView.text = dateFormat.format(mensaje.dataEnviament)
    }

    override fun getItemCount() = mensajes.size

    fun addMessage(message: Mensajes) {
        mensajes.add(message)
        notifyItemInserted(mensajes.size - 1)
    }

    fun updateData(newMensajes: List<Mensajes>) {
        mensajes.clear()
        mensajes.addAll(newMensajes)
        notifyDataSetChanged()
    }

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mensajeTextView: TextView = itemView.findViewById(R.id.mensajeTextView)
        val fechaEnvioTextView: TextView = itemView.findViewById(R.id.fechaEnvioTextView)
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.nombreTextView)
        val mensajeTextView: TextView = itemView.findViewById(R.id.mensajeTextView)
        val fechaEnvioTextView: TextView = itemView.findViewById(R.id.fechaEnvioTextView)
    }
}