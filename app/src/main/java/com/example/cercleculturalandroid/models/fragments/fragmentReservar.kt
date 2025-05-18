package com.example.cercleculturalandroid

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
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
    private var _b: FragmentReservarBinding? = null
    private val b get() = _b!!

    private lateinit var event: EventItem
    private var userId = -1
    private var availableSeats = 0
    private var qty = 1

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
        _b = FragmentReservarBinding.bind(view)
        event = requireArguments().getParcelable(ARG_EVENT)!!
        userId = requireArguments().getInt(ARG_USER, -1)
        availableSeats = event.aforament
        setupUi()
    }

    private fun setupUi() {
        with(b) {
            imgVolver.setOnClickListener { parentFragmentManager.popBackStack() }
            evName.text            = event.nom
            evDesc.text            = event.descripcio
            evUbicacion.text       = "Ubicación: ${event.ubicacio}"
            evHorario.text         = "Horario: ${event.dataInici}"
            evPlacesDisponibles.text = availableSeats.toString()
            txtViewCantidad.text   = qty.toString()

            imgMenos.setOnClickListener {
                if (qty > 1) qty--.also { txtViewCantidad.text = qty.toString() }
            }
            imgMas.setOnClickListener {
                if (qty < availableSeats) qty++.also { txtViewCantidad.text = qty.toString() }
            }
            btnReservar.setOnClickListener { doReserve() }
        }
    }

    private fun doReserve() {
        if (userId < 1) {
            toast("Usuario no válido"); return
        }
        if (qty > availableSeats) {
            toast("No quedan plazas"); return
        }

        // Timestamp en UTC
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date())

        // Actualiza UI inmediatamente
        availableSeats -= qty
        b.evPlacesDisponibles.text = availableSeats.toString()

        // Construye el request
        val req = ReservaRequest(
            usuari_id       = userId,
            dataReserva     = now,
            estat           = "CONFIRMAT",
            tipus           = "ESDEVENIMENT",
            espai_id        = event.espai_id,
            esdeveniment_id = event.id,
            dataInici       = now,
            dataFi          = now,
            numPlaces       = qty
        )

        // 1) Log completo del payload
        Log.d("PAYLOAD", Gson().toJson(req))

        // 2) Envía y captura respuesta
        RetrofitClient.getService()
            .postReserva(req)
            .enqueue(object : Callback<Reserva> {
                override fun onResponse(call: Call<Reserva>, resp: Response<Reserva>) {
                    if (resp.isSuccessful) {
                        toast("Reserva creada exitosamente")
                    } else {
                        // revertir UI en caso de error
                        availableSeats += qty
                        b.evPlacesDisponibles.text = availableSeats.toString()

                        // lee todo el cuerpo de error
                        val err = resp.errorBody()?.string() ?: "Sin cuerpo de error"
                        Log.e("API_ERROR_FULL", "Código ${resp.code()}: $err")
                        // muestra el mensaje completo en Toast (si es corto) o dialogo si es largo
                        if (err.length < 200) {
                            toast("Error ${resp.code()}: $err")
                        } else {
                            showErrorDialog("Error ${resp.code()}", err)
                        }
                    }
                }

                override fun onFailure(call: Call<Reserva>, t: Throwable) {
                    availableSeats += qty
                    b.evPlacesDisponibles.text = availableSeats.toString()
                    toast("Fallo de red: ${t.localizedMessage}")
                }
            })
    }

    // Toast corto
    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    // Para errores muy largos, mostramos diálogo desplazable
    private fun showErrorDialog(title: String, message: String) {
        val tv = TextView(requireContext()).apply {
            text = message
            setPadding(24, 24, 24, 24)
        }
        val scroll = ScrollView(requireContext()).apply {
            addView(tv)
        }
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(scroll)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
