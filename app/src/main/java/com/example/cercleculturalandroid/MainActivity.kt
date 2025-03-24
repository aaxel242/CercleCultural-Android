package com.example.cercleculturalandroid

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinner = findViewById<Spinner>(R.id.spinner_opciones)
        spinner.gravity = android.view.Gravity.CENTER

        val adapter = ArrayAdapter.createFromResource(
            this, R.array.opciones_menu, R.layout.spinner_item
                                                     ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        }
        spinner.adapter = adapter

        val rvEvents = findViewById<RecyclerView>(R.id.rvEvents)
        rvEvents.layoutManager = LinearLayoutManager(this)

        //Adapter de eventos
        val eventsAdapter = EventosAdapter()



    }
}