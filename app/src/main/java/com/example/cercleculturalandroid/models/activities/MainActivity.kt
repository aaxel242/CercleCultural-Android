package com.example.cercleculturalandroid.models.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.cercleculturalandroid.R
import com.example.cercleculturalandroid.databinding.ActivityMainBinding
import com.example.cercleculturalandroid.models.fragments.fragmentInici
import com.example.cercleculturalandroid.models.fragments.fragmentIniciAdmin
import com.example.cercleculturalandroid.models.fragments.fragmentChat
import com.example.cercleculturalandroid.models.fragments.fragmentUsuario
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isAdmin = false
    private var userId: Int = -1
    private var userName: String = "Usuari"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recogemos los datos del Intent
        isAdmin = intent.getBooleanExtra("isAdmin", false)
        userId  = intent.getIntExtra("userId", -1)
        userName = intent.getStringExtra("userName") ?: "Usuari"

        val bottomNav: BottomNavigationView = binding.bottomNavigation
        bottomNav.itemIconTintList = null
        bottomNav.setOnItemSelectedListener { item ->
            val frag: Fragment = when (item.itemId) {
                R.id.inici ->
                    if (isAdmin) fragmentIniciAdmin.newInstance(userId)
                    else        fragmentInici.newInstance(userId)
                R.id.chat -> fragmentChat.newInstance(userId, userName)
                R.id.usuari -> fragmentUsuario.newInstance(userId)
                else -> return@setOnItemSelectedListener false
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.flFragment, frag)
                .commit()
            true
        }

        if (savedInstanceState == null)
            bottomNav.selectedItemId = R.id.inici
    }
}
