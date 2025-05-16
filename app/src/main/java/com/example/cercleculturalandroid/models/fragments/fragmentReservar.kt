package com.example.cercleculturalandroid

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cercleculturalandroid.api.ApiService
import com.example.cercleculturalandroid.api.RetrofitClient
import com.example.cercleculturalandroid.databinding.FragmentReservarBinding
import com.example.cercleculturalandroid.models.clases.EventItem
import com.example.cercleculturalandroid.models.clases.Reserva
import com.example.cercleculturalandroid.models.clases.ReservaRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class fragmentReservar : Fragment(R.layout.fragment_reservar) {
    private var _binding: FragmentReservarBinding? = null
    private val b get() = _binding!!

    private lateinit var event: EventItem
    private var userId: Int = -1

    // 1) Aforo original e imitable
    private var totalCapacity: Int = 0
    private var availableSeats: Int = 0

    // 2) Cantidad a reservar
    private var qty: Int = 1

    companion object {
        private const val ARG_EVENT = "arg_event"
        private const val ARG_USER  = "arg_user"

        fun newInstance(ev: EventItem, userId: Int) = fragmentReservar().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_EVENT, ev)
                putInt(ARG_USER, userId)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReservarBinding.bind(view)

        // Recuperar argumentos
        event  = requireArguments().getParcelable(ARG_EVENT)!!
        userId = requireArguments().getInt(ARG_USER, -1)

        // Inicializar aforos
        totalCapacity   = event.aforament
        availableSeats  = totalCapacity

        setupUi()
    }

    private fun setupUi() {
        // Botón volver
        b.imgVolver.setOnClickListener { parentFragmentManager.popBackStack() }

        // Mostrar datos del evento
        b.evName.text               = event.nom
        b.evDesc.text               = event.descripcio
        b.evUbicacion.text          = "Ubicación: ${event.ubicacio}"
        b.evHorario.text            = "Horario: ${event.dataInici}"
        b.evPlacesDisponibles.text  = availableSeats.toString()
        b.txtViewCantidad.text      = qty.toString()

        // Ajustar cantidad
        b.imgMenos.setOnClickListener {
            if (qty > 1) {
                qty--
                b.txtViewCantidad.text = qty.toString()
            }
        }
        b.imgMas.setOnClickListener {
            if (qty < availableSeats) {
                qty++
                b.txtViewCantidad.text = qty.toString()
            }
        }

        // Acción reservar
        b.btnReservar.setOnClickListener { doLocalReserve() }
    }

    private fun doLocalReserve() {
        // Validaciones
        if (userId < 0) {
            Toast.makeText(requireContext(), "Usuario no válido", Toast.LENGTH_SHORT).show()
            return
        }
        if (qty > availableSeats) {
            Toast.makeText(requireContext(), "No quedan tantas plazas", Toast.LENGTH_SHORT).show()
            return
        }

        // Reducir solo en UI
        availableSeats -= qty
        b.evPlacesDisponibles.text = availableSeats.toString()

        // Construir objeto Reserva
        val req = ReservaRequest(
            usuari_id       = userId,
            esdeveniment_id = event.id,
            espai_id        = event.espai_id,
            dataReserva     = nowIso(),
            estat           = "CONFIRMADA",
            tipus           = "NORMAL",
            dataInici       = event.dataInici,
            dataFi          = event.dataFi ?: event.dataInici,
            numPlaces       = qty
                                )

        // Envío a la API
        RetrofitClient.getClient()
            .create(ApiService::class.java)
            .postReserva(req)
            .enqueue(object : Callback<Reserva> {
                override fun onResponse(call: Call<Reserva>, resp: Response<Reserva>) {
                    if (resp.isSuccessful) {
                        Toast.makeText(requireContext(), "Reserva creada", Toast.LENGTH_SHORT).show()
                    } else {
                        availableSeats += qty
                        b.evPlacesDisponibles.text = availableSeats.toString()

                        // Leer mensaje de error del cuerpo
                        val errorBody = resp.errorBody()?.string() ?: "Error desconocido"
                        Toast.makeText(requireContext(), "Error ${resp.code()}: $errorBody", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<Reserva>, t: Throwable) {
                    // En fallo de red, revertimos UI
                    availableSeats += qty
                    b.evPlacesDisponibles.text = availableSeats.toString()
                    Toast.makeText(requireContext(), "Fallo de red: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun nowIso(): String {
        // Usa el formato exacto que espera la API (sin milisegundos ni zona horaria)
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC") // Asegúrate de usar UTC
        return sdf.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
