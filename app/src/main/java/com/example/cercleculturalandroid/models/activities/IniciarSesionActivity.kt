package com.example.cercleculturalandroid.models.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.cercleculturalandroid.R

class IniciarSesionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.iniciar_sesion_layout)

        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)
        val txtRegistrarse = findViewById<TextView>(R.id.txtRegistrarse)

        txtRegistrarse.setOnClickListener {
            val intent = Intent(this, RegistrarseActivity::class.java)
            startActivity(intent)
        }

        btnIniciarSesion.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  // Cierra IniciarSesionActivity para no volver atráswrgwrgr
        }
    }
}