package com.example.cercleculturalandroid.models.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.cercleculturalandroid.R
import com.example.cercleculturalandroid.models.fragments.fragmentChat
import com.example.cercleculturalandroid.models.fragments.fragmentInici
import com.example.cercleculturalandroid.models.fragments.fragmentIniciAdmin
import com.example.cercleculturalandroid.models.fragments.fragmentUsuario
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private var isAdmin = false
    private var userId: Int = -1
    private var userName: String = "Usuari"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Recupera extra de la condicion de admin
        isAdmin = intent.getBooleanExtra("isAdmin", false)
        userId = intent.getIntExtra("userId", -1)
        userName = intent.getStringExtra("userName").toString()

        // Configurar BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.itemIconTintList = null
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.inici -> {
                    // si es admin o no carga el fragmento correspondiente
                    if (isAdmin) {
                        reemplazarFragmento(fragmentIniciAdmin(), false)
                    } else {
                        reemplazarFragmento(fragmentInici(), false)
                    }
                    true
                }
                R.id.chat -> {
                    val chatFragment = fragmentChat().apply {
                        arguments = Bundle().apply {
                            putInt("userId", userId)
                            putString("userName", userName) // Enviamos el ID al fragmento
                        }
                    }
                    reemplazarFragmento(chatFragment, false)
                    true
                }
                R.id.usuari -> {
                    val userFragment = fragmentUsuario().apply {
                        arguments = Bundle().apply {
                            putInt("userId", userId)
                        }
                    }
                    reemplazarFragmento(userFragment, false)
                    true
                }
                else -> false
            }
        }

        // 4) Selección inicial
        if (savedInstanceState == null) {
            // Esto disparará el listener de arriba con el ID correcto
            bottomNavigationView.selectedItemId = R.id.inici
        }
    }

    private fun reemplazarFragmento(fragment: Fragment, addToBackStack: Boolean) {
        val tx = supportFragmentManager.beginTransaction()
            .replace(R.id.flFragment, fragment)
        if (addToBackStack) tx.addToBackStack(null)
        tx.commit()
    }
}
