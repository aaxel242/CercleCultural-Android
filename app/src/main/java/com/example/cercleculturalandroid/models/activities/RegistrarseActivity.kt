// app/src/main/java/com/example/cercleculturalandroid/models/activities/RegistrarseActivity.kt
package com.example.cercleculturalandroid.models.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cercleculturalandroid.R
import com.example.cercleculturalandroid.api.RetrofitClient
import com.example.cercleculturalandroid.models.clases.Usuari
import com.example.cercleculturalandroid.models.clases.UsuariRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrarseActivity : AppCompatActivity() {

    private lateinit var etNom: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfPassword: EditText
    private lateinit var btnRegistrarse: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrarse_layout)

        etNom          = findViewById(R.id.editTextNomIniciarSesion)
        etEmail        = findViewById(R.id.editTextCorreoIniciarSesion)
        etPassword     = findViewById(R.id.editTextPasswordIniciarSesion)
        etConfPassword = findViewById(R.id.editTextConfPasswordIniciarSesion)
        btnRegistrarse = findViewById(R.id.btnRegistrarse)

        btnRegistrarse.setOnClickListener {
            attemptRegistration()
        }
    }

    private fun attemptRegistration() {
        val nom  = etNom.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val pass  = etPassword.text.toString()
        val conf  = etConfPassword.text.toString()

        if (nom.isEmpty() || email.isEmpty() || pass.isEmpty() || conf.isEmpty()) {
            toast("Por favor, completa todos los campos")
            return
        }
        if (pass != conf) {
            toast("Las contraseñas no coinciden")
            return
        }
        if (pass.length < 6) {
            toast("La contraseña debe tener al menos 6 caracteres")
            return
        }

        val req = UsuariRequest(nom = nom, email = email, contrasenya = pass)

        RetrofitClient.getService()
            .postUsuari(req)
            .enqueue(object : Callback<Usuari> {
                override fun onResponse(call: Call<Usuari>, response: Response<Usuari>) {
                    if (response.isSuccessful) {
                        val usuari = response.body()!!
                        toast("Usuario registrado correctamente")

                        // Determinamos rol y flag isAdmin
                        val isAdmin = usuari.tipusUsuari == "ORGANITZADOR" || usuari.tipusUsuari == "SUPERUSUARI"

                        // Lanzamos MainActivity con todos los extras
                        Intent(this@RegistrarseActivity, MainActivity::class.java).also { intent ->
                            intent.putExtra("isAdmin", isAdmin)
                            intent.putExtra("userId",   usuari.id)
                            intent.putExtra("userName", usuari.nom)
                            startActivity(intent)
                        }
                        finish()
                    } else {
                        val err = response.errorBody()?.string() ?: "registro fallido"
                        toast("Error ${response.code()}: $err")
                    }
                }
                override fun onFailure(call: Call<Usuari>, t: Throwable) {
                    toast("Fallo de red: ${t.localizedMessage}")
                }
            })
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}
