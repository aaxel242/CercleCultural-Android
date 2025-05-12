package com.example.cercleculturalandroid.models.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cercleculturalandroid.R
import com.example.cercleculturalandroid.api.ApiService
import com.example.cercleculturalandroid.api.RetrofitClient
import com.example.cercleculturalandroid.fragmentReservar
import com.example.cercleculturalandroid.models.adapters.EventsAdapter
import com.example.cercleculturalandroid.models.clases.Eventos
import com.example.cercleculturalandroid.models.clases.Espai
import com.example.cercleculturalandroid.models.clases.EventItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class fragmentInici : Fragment() {

    private lateinit var rvEvents: RecyclerView
    private lateinit var spinnerFiltro: Spinner
    private var allItems: List<EventItem> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
                             ): View? {
        return inflater.inflate(R.layout.fragment_inici, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinnerFiltro = view.findViewById(R.id.spinner_filtro)
        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.filtro_menu,
            R.layout.spinner_item
                                                            )
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerFiltro.adapter = spinnerAdapter

        // Cambiar eventos segun el filtro
        spinnerFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
                                       ) {
                aplicarFiltro()
            }
            override fun onNothingSelected(parent: AdapterView<*>) { /* no-op */ }
        }

        // Configura la lista de eventos (RecyclerView)
        rvEvents = view.findViewById(R.id.rvEvents)
        rvEvents.layoutManager = LinearLayoutManager(requireContext())

        // 3) Carga inicial
        loadEvents()
    }

    private fun loadEvents() {
        val api = RetrofitClient.getClient().create(ApiService::class.java)
        api.getEsdeveniments().enqueue(object : Callback<List<Eventos>> {
            override fun onResponse(
                call: Call<List<Eventos>>,
                response: Response<List<Eventos>>
                                   ) {
                if (response.isSuccessful) {
                    val events = response.body().orEmpty()
                    loadEspaisAndShow(events)
                } else {
                    Log.e("fragmentInici", "Error al obtener eventos: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Eventos>>, t: Throwable) {
                Log.e("fragmentInici", "Fallo al cargar eventos: ${t.message}")
            }
        })
    }

    private fun loadEspaisAndShow(events: List<Eventos>) {
        val api = RetrofitClient.getClient().create(ApiService::class.java)
        api.getEspais().enqueue(object : Callback<List<Espai>> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<List<Espai>>,
                response: Response<List<Espai>>
                                   ) {
                if (response.isSuccessful) {
                    // Crear mapa id->Espai
                    val espaisMap = response.body().orEmpty().associateBy { it.id }

                    // Fundir datos y crear lista de EventItem
                    val items = events.map { ev ->
                        EventItem(
                            id = ev.id,
                            nom = ev.nom,
                            descripcio = ev.descripcio,
                            dataInici = ev.dataInici.substringBefore("T"),
                            espai_id = ev.espai_id,
                            ubicacio = espaisMap[ev.espai_id]?.ubicacio.orEmpty(),
                            perInfants = ev.per_infants
                                 )
                    }
                    allItems = items
                    aplicarFiltro()
                } else {
                    Log.e("fragmentInici", "Error al obtener espais: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Espai>>, t: Throwable) {
                Log.e("fragmentInici", "Fallo al cargar espais: ${t.message}")
            }


        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun aplicarFiltro() {
        val selected = spinnerFiltro.selectedItem as String
        val hoy = LocalDate.now()
        val fmt = DateTimeFormatter.ISO_LOCAL_DATE

        val filtered = when (selected) {
            "Tots" -> allItems.filter { esEventoFuturo(it, hoy, fmt) }
            "Per infants" -> allItems.filter { it.perInfants && esEventoFuturo(it, hoy, fmt) }
            "Events pròxims" -> {
                val sieteDias = hoy.plusDays(7)
                allItems.filter { esEventoProximo(it, hoy, sieteDias, fmt) }
            }
            "Events Anteriors" -> allItems.filter { esEventoAnterior(it, hoy, fmt) }
            else -> allItems
        }

        rvEvents.adapter = EventsAdapter(filtered) { evento ->
            seleccionarEvento(evento)

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun esEventoFuturo(evento: EventItem, hoy: LocalDate, fmt: DateTimeFormatter): Boolean {
        return try {
            val dateOnly = evento.dataInici.substringBefore(" ").substringBefore("T")
            val fechaEvento = LocalDate.parse(dateOnly, fmt)
            !fechaEvento.isBefore(hoy)
        } catch (e: Exception) {
            Log.e("FILTRO", "Error al parsear fecha: ${evento.dataInici}", e)
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun esEventoProximo(evento: EventItem, hoy: LocalDate, sieteDias: LocalDate, fmt: DateTimeFormatter): Boolean {
        val dateOnly = evento.dataInici.substringBefore(" ").substringBefore("T")
        val fechaEvento = LocalDate.parse(dateOnly, fmt)
        return !fechaEvento.isBefore(hoy) && !fechaEvento.isAfter(sieteDias)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun esEventoAnterior(evento: EventItem, hoy: LocalDate, fmt: DateTimeFormatter): Boolean {
        val dateOnly = evento.dataInici.substringBefore(" ").substringBefore("T")
        return LocalDate.parse(dateOnly, fmt).isBefore(hoy)
    }

    private fun seleccionarEvento(evento: EventItem) {
        val args = Bundle().apply { putParcelable("evento", evento) }
        val fragment = fragmentReservar().apply { arguments = args }

        parentFragmentManager.beginTransaction()
            .replace(R.id.flFragment, fragment)
            .addToBackStack(null)
            .commit()
    }
}
