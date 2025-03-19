package com.example.cercleculturalandroid

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class IniciarSesionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.iniciar_sesion_layout)

        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)
        val txtRegistrarse = findViewById<TextView>(R.id.txtRegistrarse)

        txtRegistrarse.setOnClickListener {
            intent = Intent(this, RegistrarseActivity::class.java)
            startActivity(intent)
        }

        btnIniciarSesion.setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
    }
}