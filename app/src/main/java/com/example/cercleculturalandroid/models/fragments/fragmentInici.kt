package com.example.cercleculturalandroid.models.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cercleculturalandroid.R
import com.example.cercleculturalandroid.api.ApiService
import com.example.cercleculturalandroid.api.RetrofitClient
import com.example.cercleculturalandroid.databinding.FragmentIniciBinding
import com.example.cercleculturalandroid.fragmentReservar
import com.example.cercleculturalandroid.models.adapters.EventsAdapter
import com.example.cercleculturalandroid.models.clases.Espai
import com.example.cercleculturalandroid.models.clases.EventItem
import com.example.cercleculturalandroid.models.clases.Eventos
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class fragmentInici : Fragment(R.layout.fragment_inici) {
    private var _binding: FragmentIniciBinding? = null
    private val binding get() = _binding!!

    // Lista fusionada de eventos + espacios
    private var allItems: List<EventItem> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentIniciBinding.bind(view)

        // 1) Configura RecyclerView
        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())

        // 2) Configura Spinner de filtros
        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.filtro_menu,
            R.layout.spinner_item
                                                            )
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerFiltro.adapter = spinnerAdapter

        // Selección inicial en “Tots”
        binding.spinnerFiltro.setSelection(0)

        binding.spinnerFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(
                parent: AdapterView<*>, v: View?, pos: Int, id: Long
                                       ) {
                // Aplica filtro en cada selección
                aplicarFiltro()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Si nunca selecciona nada, forzamos “Tots”
                binding.spinnerFiltro.setSelection(0)
            }
        }

        // 3) Carga inicial de datos
        loadEvents()
    }

    private fun loadEvents() {
        val api = RetrofitClient.getClient().create(ApiService::class.java)
        api.getEsdeveniments().enqueue(object : Callback<List<Eventos>> {
            override fun onResponse(call: Call<List<Eventos>>, response: Response<List<Eventos>>) {
                if (!response.isSuccessful) {
                    Log.e("fragmentInici", "Error eventos: ${response.code()}")
                    return
                }
                mergeWithSpaces(response.body().orEmpty())
            }
            override fun onFailure(call: Call<List<Eventos>>, t: Throwable) {
                Log.e("fragmentInici", "Fallo eventos: ${t.message}")
            }
        })
    }

    private fun mergeWithSpaces(events: List<Eventos>) {
        val api = RetrofitClient.getClient().create(ApiService::class.java)
        api.getEspais().enqueue(object : Callback<List<Espai>> {
            override fun onResponse(call: Call<List<Espai>>, response: Response<List<Espai>>) {
                if (!response.isSuccessful) {
                    Log.e("fragmentInici", "Error espais: ${response.code()}")
                    return
                }
                val mapEspais = response.body().orEmpty().associateBy { it.id }
                allItems = events.map { ev ->
                    EventItem(
                        id          = ev.id,
                        nom         = ev.nom,
                        descripcio  = ev.descripcio,
                        dataInici   = ev.dataInici.take(10),
                        dataFi      = ev.dataFi?.take(10) ?: ev.dataInici.take(10),
                        aforament   = ev.aforament,
                        espai_id    = ev.espai_id,
                        ubicacio    = mapEspais[ev.espai_id]?.ubicacio.orEmpty(),
                        imatge      = ev.imatge,
                        perInfants  = ev.per_infants
                             )
                }
                // Mostrar inicialmente filtrado como “Tots”
                aplicarFiltro()
            }
            override fun onFailure(call: Call<List<Espai>>, t: Throwable) {
                Log.e("fragmentInici", "Fallo espais: ${t.message}")
            }
        })
    }

    private fun mostrarListado(list: List<EventItem>) {
        binding.rvEvents.adapter = EventsAdapter(list) { evento ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.flFragment, fragmentReservar.newInstance(evento))
                .addToBackStack(null)
                .commit()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun aplicarFiltro() {
        val selected = binding.spinnerFiltro.selectedItem as String
        val hoy = LocalDate.now()
        val fmt = DateTimeFormatter.ISO_LOCAL_DATE

        val filtered = when (selected) {
            "Tots" -> allItems.filter { esEventoFuturo(it, hoy, fmt) }
            "Pròxims" -> {
                val limite = hoy.plusDays(7)
                allItems.filter { esEventoProximo(it, hoy, limite, fmt) }
            }
            "Per infants" -> allItems.filter { it.perInfants && esEventoFuturo(it, hoy, fmt) }
            "Anteriors" -> allItems.filter { esEventoAnterior(it, hoy, fmt) }
            else -> allItems
        }
        mostrarListado(filtered)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun esEventoFuturo(e: EventItem, hoy: LocalDate, fmt: DateTimeFormatter): Boolean {
        return try {
            val date = LocalDate.parse(e.dataInici, fmt)
            !date.isBefore(hoy)
        } catch (ex: Exception) {
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun esEventoProximo(e: EventItem, hoy: LocalDate, limite: LocalDate, fmt: DateTimeFormatter): Boolean {
        val date = LocalDate.parse(e.dataInici, fmt)
        return !date.isBefore(hoy) && !date.isAfter(limite)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun esEventoAnterior(e: EventItem, hoy: LocalDate, fmt: DateTimeFormatter): Boolean {
        val date = LocalDate.parse(e.dataInici, fmt)
        return date.isBefore(hoy)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
