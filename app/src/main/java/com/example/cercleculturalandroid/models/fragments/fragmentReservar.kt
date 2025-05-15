package com.example.cercleculturalandroid

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.cercleculturalandroid.databinding.FragmentReservarBinding

 class fragmentReservar : Fragment(R.layout.fragment_reservar) {
        private var _binding: FragmentReservarBinding? = null
        private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReservarBinding.bind(view)

        //LIBGDX
        /*val gdxFrag = fragmentGdx()
        childFragmentManager.beginTransaction()
            .replace(binding.gdxContainer.id, gdxFrag)
            .commit()/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null*/

        //
    }
}
