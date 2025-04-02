package com.example.cercleculturalandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar el BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.itemIconTintList = null

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.inici -> {
                    reemplazarFragmento(fragmentInici(), false)
                    true
                }
                R.id.chat -> {
                    // Reemplaza con otro fragmento si lo tienes, por ejemplo: fragmentChat()
                    reemplazarFragmento(fragmentChat(), false)
                    true
                }
                R.id.usuari -> {
                    reemplazarFragmento(fragmentUsuario(), false)
                    true
                }
                else -> false
            }
        }

        // Mostrar el fragmento "Inici" por defecto
        if (savedInstanceState == null) {
            reemplazarFragmento(fragmentInici(), false)
        }
    }

    private fun reemplazarFragmento(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.flFragment, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}
