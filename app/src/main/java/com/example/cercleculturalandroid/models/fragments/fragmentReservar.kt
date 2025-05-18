package com.example.cercleculturalandroid

import android.os.Bundle
import android.util.Log
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
import com.google.gson.JsonParser
import java.text.SimpleDateFormat
import java.util.*

class fragmentReservar : Fragment(R.layout.fragment_reservar) {

    private var _binding: FragmentReservarBinding? = null
    private val b get() = _binding!!

    private lateinit var event: EventItem
    private var userId: Int = -1
    private var totalCapacity: Int = 0
    private var availableSeats: Int = 0
    private var qty: Int = 1

    companion object {
        private const val ARG_EVENT = "arg_event"
        private const val ARG_USER = "arg_user"

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

        event = requireArguments().getParcelable(ARG_EVENT)!!
        userId = requireArguments().getInt(ARG_USER, -1)
        totalCapacity = event.aforament
        availableSeats = totalCapacity

        setupUi()
    }

    private fun setupUi() {
        b.imgVolver.setOnClickListener { parentFragmentManager.popBackStack() }

        with(b) {
            evName.text = event.nom
            evDesc.text = event.descripcio
            evUbicacion.text = "Ubicación: ${event.ubicacio}"
            evHorario.text = "Horario: ${event.dataInici}"
            evPlacesDisponibles.text = availableSeats.toString()
            txtViewCantidad.text = qty.toString()

            imgMenos.setOnClickListener { adjustQuantity(-1) }
            imgMas.setOnClickListener { adjustQuantity(1) }
            btnReservar.setOnClickListener { doLocalReserve() }
        }
    }

    private fun adjustQuantity(delta: Int) {
        qty = (qty + delta).coerceIn(1, availableSeats)
        b.txtViewCantidad.text = qty.toString()
    }

    private fun doLocalReserve() {
        if (!validateInputs()) return

        availableSeats -= qty
        b.evPlacesDisponibles.text = availableSeats.toString()

        val req = buildReservaRequest()
        sendReservationRequest(req)
    }

    private fun validateInputs(): Boolean {
        return when {
            userId <= 0 -> {
                showToast("Usuario no válido")
                false
            }
            event.espai_id <= 0 -> {
                showToast("Espacio no válido")
                false
            }
            event.dataInici.isNullOrEmpty() -> {
                showToast("Fecha de inicio inválida")
                false
            }
            else -> true
        }
    }

    private fun buildReservaRequest(): ReservaRequest {
        return ReservaRequest(
            usuari_id = userId,
            esdeveniment_id = event.id,
            espai_id = event.espai_id,
            dataReserva = nowIso(),
            estat = "CONFIRMADA",
            tipus = "NORMAL",
            dataInici = formatDateForApi(event.dataInici),
            dataFi = formatDateForApi(event.dataFi ?: event.dataInici),
            numPlaces = qty  // <- Nombre correcto
        )
    }

    private fun sendReservationRequest(req: ReservaRequest) {
        RetrofitClient.getClient()
            .create(ApiService::class.java)
            .postReserva(req)
            .enqueue(object : Callback<Reserva> {
                override fun onResponse(call: Call<Reserva>, resp: Response<Reserva>) {
                    if (!resp.isSuccessful) {
                        handleApiError(resp)
                    } else {
                        showToast("Reserva creada exitosamente")
                    }
                }

                override fun onFailure(call: Call<Reserva>, t: Throwable) {
                    revertUIState()
                    showToast("Error de red: ${t.localizedMessage}")
                }
            })
    }

    private fun handleApiError(resp: Response<Reserva>) {
        revertUIState()
        val errorBody = resp.errorBody()?.string() ?: "Respuesta vacía"
        Log.e("API_ERROR", """
        Código: ${resp.code()}
        Cuerpo: $errorBody
        Headers: ${resp.headers()}
    """.trimIndent())

        val errorMessage = when (resp.code()) {
            400 -> "Solicitud mal formada: ${parseErrorMessage(errorBody)}"
            401 -> "No autorizado - Inicie sesión nuevamente"
            404 -> "Recurso no encontrado"
            500 -> "Error interno del servidor"
            else -> "Error desconocido (${resp.code()})"
        }

        showToast(errorMessage)
    }

    private fun parseErrorMessage(errorBody: String): String {
        return try {
            JsonParser.parseString(errorBody).asJsonObject.let { json ->
                when {
                    json.has("Message") -> json["Message"].asString // Por si el servidor usa PascalCase
                    json.has("errorDescription") -> json["errorDescription"].asString
                    else -> errorBody.take(200) // Muestra fragmento si no es JSON
                }
            }
        } catch (e: Exception) {
            "Detalles técnicos: ${errorBody.take(200)}" // Limita a 200 caracteres
        }
    }

    private fun revertUIState() {
        availableSeats += qty
        b.evPlacesDisponibles.text = availableSeats.toString()
    }

    private fun nowIso(): String {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date())
    }

    private fun formatDateForApi(dateString: String): String {
        return try {
            // El formato en que recibes la fecha (ajústalo si es necesario)
            val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

            // Aquí defines el formato de salida ISO con milisegundos y Z
            val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

            // Parseas y luego formateas
            val date = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            Log.e("DATE_ERROR", "Fecha recibida: $dateString", e)
            // En caso de error, devuelves la fecha actual bien formateada
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                .apply { timeZone = TimeZone.getTimeZone("UTC") }
                .format(Date())
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}