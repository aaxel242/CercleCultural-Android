package com.example.cercleculturalandroid.models.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.cercleculturalandroid.R
import com.example.cercleculturalandroid.models.clases.EventItem

class fragmentReservar : Fragment() {

    private var evento: EventItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            evento = it.getParcelable("evento")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
                             ): View? =
        inflater.inflate(R.layout.fragment_reservar, container, false)
}

   /* override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        }
    }*/

