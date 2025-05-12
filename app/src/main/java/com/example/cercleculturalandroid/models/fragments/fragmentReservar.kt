package com.example.cercleculturalandroid

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.cercleculturalandroid.databinding.FragmentReservarBinding

class fragmentReservar : Fragment(R.layout.fragment_reservar) {

    // Binding seguro con nullable backing property
    private var _binding: FragmentReservarBinding? = null
    private val binding get() = _binding!!

    private lateinit var gameFragment: fragmentGdx

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Vincula el layout
        _binding = FragmentReservarBinding.bind(view)

        // Inserta el FragmentGdx en el contenedor
        gameFragment = fragmentGdx()
        childFragmentManager.beginTransaction()
            .replace(binding.gdxContainer.id, gameFragment)
            .commitNow()  // asegura que la vista esté creada antes de interactuar

        // Botón para reservar una butaca de ejemplo
        binding.btnConfirm.setOnClickListener {
            gameFragment.reserveSeat(col = 2, row = 1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // evita fugas del view
    }
}
