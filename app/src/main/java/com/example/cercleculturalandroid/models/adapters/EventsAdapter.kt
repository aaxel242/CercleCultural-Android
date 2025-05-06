package com.example.cercleculturalandroid.models.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cercleculturalandroid.R
import com.example.cercleculturalandroid.models.clases.EventItem

class EventsAdapter(
    private val items: List<EventItem>,
    private val onClick: (EventItem) -> Unit
                   ) : RecyclerView.Adapter<EventsAdapter.EventVH>() {

    inner class EventVH(view: View) : RecyclerView.ViewHolder(view) {
        private val txtNombre    = view.findViewById<TextView>(R.id.txtNombreEvento)
        private val txtDesc      = view.findViewById<TextView>(R.id.TxtDescripcion)
        private val txtUbicacion = view.findViewById<TextView>(R.id.TxtUbicacion)
        private val txtFecha     = view.findViewById<TextView>(R.id.txtFechaEvento)

        fun bind(ev: EventItem) {
            txtNombre.text    = ev.nom
            txtDesc.text      = ev.descripcio
            txtUbicacion.text = ev.ubicacio
            txtFecha.text     = ev.dataInici

            itemView.setOnClickListener {
                onClick(ev)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_evento_layout, parent, false)
        return EventVH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: EventVH, position: Int) {
        holder.bind(items[position])
    }
}
