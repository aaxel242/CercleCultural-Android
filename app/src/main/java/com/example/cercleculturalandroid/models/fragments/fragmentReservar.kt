package com.example.cercleculturalandroid

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cercleculturalandroid.api.ApiService
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
import com.example.cercleculturalandroid.models.TotalReservasResponse


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
        fetchTotalReservas {
            setupUi()
        }
    }

    private fun fetchTotalReservas(onDone: () -> Unit) {
        RetrofitClient.getService()
            .getTotalReservasEvento(event.id)
            .enqueue(object : Callback<TotalReservasResponse> {
                override fun onResponse(
                    call: Call<TotalReservasResponse>,
                    resp: Response<TotalReservasResponse>
                                       ) {
                    if (resp.isSuccessful) {
                        val reserved = resp.body()?.total ?: 0
                        availableSeats = event.aforament - reserved
                    } else {
                        availableSeats = 0
                        toast("Error al cargar reservas del evento")
                    }
                    onDone()
                }
                override fun onFailure(call: Call<TotalReservasResponse>, t: Throwable) {
                    availableSeats = 0
                    toast("Fallo de red al cargar reservas")
                    onDone()
                }
            })
    }


    private fun setupUi() {
        with(b) {
            btnReservar.isEnabled = availableSeats > 0
            if (availableSeats == 0) {
                evPlacesDisponibles.text = "Agotadas"
                toast("No quedan plazas para este evento")
            } else {
                evPlacesDisponibles.text = availableSeats.toString()
            }
            txtViewCantidad.text = qty.toString()
            imgMenos.setOnClickListener {
                if (qty > 1) {
                    qty--
                    txtViewCantidad.text = qty.toString()
                }
            }
            imgMas.setOnClickListener {
                if (availableSeats == 0) {
                    toast("No quedan más plazas")
                    return@setOnClickListener
                }
                if (qty < 4 && qty < availableSeats) {
                    qty++
                    txtViewCantidad.text = qty.toString()
                } else if (qty >= 4) {
                    toast("Máximo 4 plazas por reserva")
                } else {
                    toast("No quedan más plazas disponibles")
                }
            }
            imgVolver.setOnClickListener { parentFragmentManager.popBackStack() }
            btnReservar.setOnClickListener { doReserve() }
            evName.text      = event.nom
            evDesc.text      = event.descripcio
            evUbicacion.text = "Ubicación: ${event.ubicacio}"
            evHorario.text   = "Horario: ${event.dataInici}"
        }
    }

    private fun doReserve() {
        if (userId < 1) {
            toast("Usuario no válido"); return
        }
        if (qty > availableSeats) {
            toast("No quedan plazas"); return
        }

        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date())

        availableSeats -= qty
        b.evPlacesDisponibles.text = availableSeats.toString()

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

        Log.d("PAYLOAD", Gson().toJson(req))

        RetrofitClient.getService()
            .postReserva(req)
            .enqueue(object : Callback<Reserva> {
                override fun onResponse(call: Call<Reserva>, resp: Response<Reserva>) {
                    if (resp.isSuccessful) {
                        toast("Reserva creada exitosamente")
                    } else {
                        availableSeats += qty
                        b.evPlacesDisponibles.text = availableSeats.toString()

                        val err = resp.errorBody()?.string() ?: "Sin cuerpo de error"
                        Log.e("API_ERROR_FULL", "Código ${resp.code()}: $err")
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

    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

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
