package com.example.cercleculturalandroid

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinner = findViewById<Spinner>(R.id.spinner_opciones)
        spinner.gravity = android.view.Gravity.CENTER

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.opciones_menu,
            R.layout.spinner_item  // Usa el TextView personalizado
                                                     ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)  // Layout para el dropdown
        }
        spinner.adapter = adapter
    }
}