package com.example.cercleculturalandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class fragmentInici : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
                             ): View? {
        // Infla el layout del fragmento que ahora incluye el Spinner
        return inflater.inflate(R.layout.fragment_inici, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar el Spinner
        val spinner = view.findViewById<Spinner>(R.id.spinner_opciones)
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.opciones_menu,
            R.layout.spinner_item
                                                     )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = adapter

        // Configurar el RecyclerView (si lo necesitas)
        val rvEvents = view.findViewById<RecyclerView>(R.id.rvEvents)
        rvEvents.layoutManager = LinearLayoutManager(requireContext())
        // Asigna un adapter al RecyclerView, por ejemplo:
        // rvEvents.adapter = EventosAdapter()
    }
}
