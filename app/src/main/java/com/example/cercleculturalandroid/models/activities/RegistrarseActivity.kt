package com.example.cercleculturalandroid.models.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.cercleculturalandroid.R

class RegistrarseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrarse_layout)

        val btnRegistrarse = findViewById<Button>(R.id.btnRegistrarse)
        btnRegistrarse.setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}