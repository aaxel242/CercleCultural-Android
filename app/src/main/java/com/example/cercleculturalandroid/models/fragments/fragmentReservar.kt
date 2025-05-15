package com.example.cercleculturalandroid

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cercleculturalandroid.api.ApiService
import com.example.cercleculturalandroid.api.RetrofitClient
import com.example.cercleculturalandroid.databinding.FragmentReservarBinding
import com.example.cercleculturalandroid.models.clases.Reserva
import com.example.cercleculturalandroid.models.clases.EventItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class fragmentReservar : Fragment(R.layout.fragment_reservar) {
    private var _b: FragmentReservarBinding? = null
    private val b get() = _b!!

    private lateinit var event: EventItem
    private var available = 0
    private var qty = 1
    private var userId = -1

    companion object {
        private const val ARG_EVENT = "arg_event"
        fun newInstance(ev: EventItem) = fragmentReservar().apply {
            arguments = Bundle().apply { putParcelable(ARG_EVENT, ev) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _b = FragmentReservarBinding.bind(view)

        event = requireArguments().getParcelable(ARG_EVENT)!!
        available = event.aforament

        // Botón volver
        b.imgVolver.setOnClickListener { parentFragmentManager.popBackStack() }

        // Datos
        b.evName.text = event.nom
        b.evDesc.text = event.descripcio
        b.evUbicacion.text = "Ubicación: ${event.ubicacio}"
        b.evHorario.text = "Horario: ${event.dataInici}"
        b.evPlacesDisponibles.text = "$available"

        // Cantidad
        b.imgMenos.setOnClickListener {
            if (qty > 1) {
                qty--
                b.txtViewCantidad.text = "$qty"
            }
        }
        b.imgMas.setOnClickListener {
            if (qty < available) {
                qty++
                b.txtViewCantidad.text = "$qty"
            }
        }

        // Reservar
        b.btnReservar.setOnClickListener { doLocalReserve() }
    }

    private fun doLocalReserve() {
        if (qty > available) {
            Toast.makeText(requireContext(), "No quedan tantas plazas", Toast.LENGTH_SHORT).show()
            return
        }
        available -= qty
        b.evPlacesDisponibles.text = "$available"

        val newRes = Reserva(
            id             = 0,
            usuariId       = userId,
            esdevenimentId = event.id,
            espaiId        = event.espai_id,
            dataReserva    = nowIso(),
            estat          = "CONFIRMADA",
            tipus          = "NORMAL",
            dataInici      = event.dataInici,
            dataFi         = event.dataFi,
            numPlaces      = qty
                            )

        RetrofitClient.getClient()
            .create(ApiService::class.java)
            .postReserva(newRes)
            .enqueue(object : Callback<Reserva> {
                override fun onResponse(call: Call<Reserva>, resp: Response<Reserva>) {
                    if (!resp.isSuccessful) {
                        // revertir
                        available += qty
                        b.evPlacesDisponibles.text = "$available"
                        Toast.makeText(requireContext(), "Error ${resp.code()}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Reserva OK", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Reserva>, t: Throwable) {
                    available += qty
                    b.evPlacesDisponibles.text = "$available"
                    Toast.makeText(requireContext(), "Fallo: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun nowIso(): String =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
