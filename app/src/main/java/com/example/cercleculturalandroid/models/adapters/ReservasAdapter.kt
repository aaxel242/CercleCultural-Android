package com.example.cercleculturalandroid.models.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.cercleculturalandroid.databinding.ItemReservaBinding
import com.example.cercleculturalandroid.models.clases.Reserva
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ReservasAdapter(private var reservas: List<Reserva>) :
    RecyclerView.Adapter<ReservasAdapter.ReservaViewHolder>() {

    inner class ReservaViewHolder(private val binding: ItemReservaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(reserva: Reserva) {
            with(binding) {
                // Configurar formateadores
                val isoFormatter = DateTimeFormatter.ISO_DATE_TIME
                val displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

                // Tipo de reserva
                tvTipoReserva.text = "Reserva de ${reserva.tipus.lowercase().replaceFirstChar { it.uppercase() }}"

                // Fecha de reserva (String a LocalDateTime)
                val fechaReserva = try {
                    LocalDateTime.parse(reserva.dataReserva, isoFormatter)
                } catch (e: DateTimeParseException) {
                    null
                }
                tvFechaReserva.text = "Reservado el: ${fechaReserva?.format(displayFormatter) ?: "Fecha inválida"}"

                // Estado
                tvEstado.text = "Estado: ${reserva.estat}"
                tvEstado.setTextColor(
                    when (reserva.estat) {
                        "CONFIRMAT" -> root.context.getColor(android.R.color.holo_green_dark)
                        "PENDENT" -> root.context.getColor(android.R.color.holo_orange_dark)
                        else -> root.context.getColor(android.R.color.holo_red_dark)
                    }
                                     )

                // Detalles específicos
                when (reserva.tipus) {
                    "ESPAI" -> {
                        layoutEspacio.visibility = View.VISIBLE
                        // Convertir fechas de String a LocalDateTime
                        val inicio = try {
                            LocalDateTime.parse(reserva.dataInici, isoFormatter)
                        } catch (e: Exception) {
                            null
                        }
                        val fin = try {
                            LocalDateTime.parse(reserva.dataFi, isoFormatter)
                        } catch (e: Exception) {
                            null
                        }

                        tvFechaInicio.text = "Inicio: ${inicio?.format(displayFormatter) ?: "N/A"}"
                        tvFechaFin.text = "Fin: ${fin?.format(displayFormatter) ?: "N/A"}"
                    }
                    "ESDEVENIMENT" -> {
                        layoutEvento.visibility = View.VISIBLE
                        tvPlazas.text = "Plazas: ${reserva.numPlaces ?: 0}"
                    }
                }

                // Botón Cancelar
                btnCancelar.setOnClickListener {
                    // Lógica futura
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val binding = ItemReservaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
                                                )
        return ReservaViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        holder.bind(reservas[position])
    }

    fun updateData(newReservas: List<Reserva>) {
        reservas = newReservas
        notifyDataSetChanged()
    }

    override fun getItemCount() = reservas.size
}