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
import com.example.cercleculturalandroid.databinding.FragmentIniciAdminBinding
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

class fragmentIniciAdmin : Fragment(R.layout.fragment_inici_admin) {
    private var _binding: FragmentIniciAdminBinding? = null
    private val binding get() = _binding!!

    private var userId: Int = -1
    private var allItems: List<EventItem> = emptyList()

    companion object {
        private const val ARG_USER = "arg_user"
        fun newInstance(userId: Int) = fragmentIniciAdmin().apply {
            arguments = Bundle().apply { putInt(ARG_USER, userId) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = requireArguments().getInt(ARG_USER, -1)
        _binding = FragmentIniciAdminBinding.bind(view)

        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.spinnerFiltro.adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.filtro_menu, R.layout.spinner_item
                                                                       ).also { it.setDropDownViewResource(R.layout.spinner_dropdown_item) }

        binding.spinnerFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(
                parent: AdapterView<*>, v: View?, pos: Int, id: Long
                                       ) = aplicarFiltro()

            override fun onNothingSelected(parent: AdapterView<*>) {
                binding.spinnerFiltro.setSelection(0)
            }
        }

        loadEvents()
    }

    private fun loadEvents() {
        RetrofitClient.getClient()
            .create(ApiService::class.java)
            .getEsdeveniments()
            .enqueue(object : Callback<List<Eventos>> {
                override fun onResponse(
                    call: Call<List<Eventos>>,
                    response: Response<List<Eventos>>
                                       ) {
                    if (response.isSuccessful) mergeWithSpaces(response.body().orEmpty())
                    else Log.e("fragmentIniciAdmin", "Error eventos: ${response.code()}")
                }
                override fun onFailure(call: Call<List<Eventos>>, t: Throwable) {
                    Log.e("fragmentIniciAdmin", "Fallo eventos: ${t.message}")
                }
            })
    }

    private fun mergeWithSpaces(events: List<Eventos>) {
        RetrofitClient.getClient()
            .create(ApiService::class.java)
            .getEspais()
            .enqueue(object : Callback<List<Espai>> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<List<Espai>>, response: Response<List<Espai>>
                                       ) {
                    if (!response.isSuccessful) {
                        Log.e("fragmentIniciAdmin", "Error espais: ${response.code()}")
                        return
                    }
                    val espMap = response.body().orEmpty().associateBy { it.id }
                    allItems = events.map { ev ->
                        EventItem(
                            id          = ev.id,
                            nom         = ev.nom,
                            descripcio  = ev.descripcio,
                            dataInici   = ev.dataInici.take(10),
                            dataFi      = ev.dataFi?.take(10) ?: ev.dataInici.take(10),
                            aforament   = ev.aforament,
                            espai_id    = ev.espai_id,
                            ubicacio    = espMap[ev.espai_id]?.ubicacio.orEmpty(),
                            imatge      = ev.imatge,
                            perInfants  = ev.per_infants
                                 )
                    }
                    aplicarFiltro()
                }
                override fun onFailure(call: Call<List<Espai>>, t: Throwable) {
                    Log.e("fragmentIniciAdmin", "Fallo espais: ${t.message}")
                }
            })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun aplicarFiltro() {
        val sel = binding.spinnerFiltro.selectedItem as String
        val hoy = LocalDate.now()
        val fmt = DateTimeFormatter.ISO_LOCAL_DATE
        val filtered = when (sel) {
            "Tots"       -> allItems.filter { esEventoFuturo(it, hoy, fmt) }
            "Pròxims"    -> allItems.filter {
                val lim = hoy.plusDays(7)
                esEventoProximo(it, hoy, lim, fmt)
            }
            "Per infants"-> allItems.filter { it.perInfants && esEventoFuturo(it, hoy, fmt) }
            "Anteriors"  -> allItems.filter { esEventoAnterior(it, hoy, fmt) }
            else         -> allItems
        }
        binding.rvEvents.adapter = EventsAdapter(filtered) { evento ->
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.flFragment,
                    fragmentReservar.newInstance(evento, userId)
                        )
                .addToBackStack(null)
                .commit()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun esEventoFuturo(e: EventItem, hoy: LocalDate, fmt: DateTimeFormatter) =
        try { !LocalDate.parse(e.dataInici, fmt).isBefore(hoy) } catch (_: Exception) { false }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun esEventoProximo(
        e: EventItem, hoy: LocalDate, lim: LocalDate, fmt: DateTimeFormatter
                               ) = esEventoFuturo(e, hoy, fmt) && !LocalDate.parse(e.dataInici, fmt).isAfter(lim)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun esEventoAnterior(e: EventItem, hoy: LocalDate, fmt: DateTimeFormatter) =
        LocalDate.parse(e.dataInici, fmt).isBefore(hoy)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
