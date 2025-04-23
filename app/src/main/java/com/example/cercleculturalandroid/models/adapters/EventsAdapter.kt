package com.example.cercleculturalandroid.models.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cercleculturalandroid.R
import com.example.cercleculturalandroid.models.clases.EventItem

class EventsAdapter(
    private val items: List<EventItem>
                   ) : RecyclerView.Adapter<EventsAdapter.EventVH>() {

    inner class EventVH(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombre    = view.findViewById<TextView>(R.id.txtNombreEvento)
        val txtDesc      = view.findViewById<TextView>(R.id.TxtDescripcion)
        val txtUbicacion = view.findViewById<TextView>(R.id.TxtUbicacion)
        val txtFecha     = view.findViewById<TextView>(R.id.txtFechaEvento)
        //val imgEvento    = view.findViewById<ImageView>(R.id.imgProducto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_evento_layout, parent, false)
        return EventVH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: EventVH, position: Int) {
        val ev = items[position]
        holder.txtNombre.text    = ev.nom
        holder.txtDesc.text      = ev.descripcio
        holder.txtUbicacion.text = ev.ubicacio
        // Formateamos la fecha (ejemplo: yyyy-MM-dd)
        holder.txtFecha.text     = ev.dataInici.substringBefore("T")
        // Si usas Glide o Picasso, carga la imagen:
        // Glide.with(holder.imgEvento).load(ev.imatge).into(holder.imgEvento)
    }
}
