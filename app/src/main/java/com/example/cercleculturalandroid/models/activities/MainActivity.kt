// app/src/main/java/com/example/cercleculturalandroid/MainActivity.kt
package com.example.cercleculturalandroid.models.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.example.cercleculturalandroid.R
import com.example.cercleculturalandroid.databinding.ActivityMainBinding
import com.example.cercleculturalandroid.models.fragments.fragmentChat
import com.example.cercleculturalandroid.models.fragments.fragmentInici
import com.example.cercleculturalandroid.models.fragments.fragmentIniciAdmin
import com.example.cercleculturalandroid.models.fragments.fragmentUsuario
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    private lateinit var binding: ActivityMainBinding
    private var isAdmin = false
    private var userId: Int = -1
    private var userName: String = "Usuari"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflar layout con ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Leer datos pasados en el Intent
        isAdmin = intent.getBooleanExtra("isAdmin", false)
        userId  = intent.getIntExtra("userId", -1)
        userName = intent.getStringExtra("userName") ?: "Usuari"

        // Configurar la barra inferior
        val bottomNav: BottomNavigationView = binding.bottomNavigation
        bottomNav.itemIconTintList = null
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.inici -> {
                    val frag: Fragment = if (isAdmin) {
                        fragmentIniciAdmin()
                    } else {
                        fragmentInici()
                    }
                    replaceFragment(frag)
                    true
                }
                R.id.chat -> {
                    val chatFrag = fragmentChat().apply {
                        arguments = Bundle().apply {
                            putInt("userId", userId)
                            putString("userName", userName)
                        }
                    }
                    replaceFragment(chatFrag)
                    true
                }
                R.id.usuari -> {
                    val userFrag = fragmentUsuario().apply {
                        arguments = Bundle().apply {
                            putInt("userId", userId)
                        }
                    }
                    replaceFragment(userFrag)
                    true
                }
                else -> false
            }
        }

        // Selección inicial
        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.inici
        }
    }

    /** Inserta o reemplaza el fragmento en el container R.id.flFragment */
    private fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = false) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            if (addToBackStack) addToBackStack(null)
            commit()
        }
    }

    /** Método requerido por AndroidFragmentApplication.Callbacks */
    override fun exit() {
        // No es necesario implementar lógica de salida en este embed
    }
}
