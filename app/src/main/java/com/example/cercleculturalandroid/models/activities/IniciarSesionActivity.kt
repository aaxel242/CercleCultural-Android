package com.example.cercleculturalandroid.models.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cercleculturalandroid.R
import com.example.cercleculturalandroid.api.ApiService
import com.example.cercleculturalandroid.api.RetrofitClient
import com.example.cercleculturalandroid.models.clases.Usuari
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest

class IniciarSesionActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etContrasenya: EditText
    private lateinit var btnIniciarSesion: Button
    private lateinit var logoAdmin: ImageView
    private lateinit var txtRegistrarse: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.iniciar_sesion_layout)

        etEmail          = findViewById(R.id.editTextCorreoIniciarSesion)
        etContrasenya    = findViewById(R.id.editTextPasswordIniciarSesion)
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)
        logoAdmin        = findViewById(R.id.logo_admin)
        txtRegistrarse   = findViewById(R.id.txtRegistrarse)

        // Registro
        txtRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegistrarseActivity::class.java))
        }

        // Login normal
        btnIniciarSesion.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass  = etContrasenya.text.toString()
            val encriptado = md5(pass)

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Si us plau, completa tots els camps", Toast.LENGTH_SHORT).show()
            } else {
                val api = RetrofitClient.getClient().create(ApiService::class.java)
                api.getUsuaris().enqueue(object : Callback<List<Usuari>> {
                    override fun    onResponse(
                        call: Call<List<Usuari>>,
                        response: Response<List<Usuari>>
                                           ) {
                        if (response.isSuccessful) {
                            val usuaris = response.body().orEmpty()
                            // Buscamos usuario que coincida email y contraseña
                            val usuari = usuaris.find {
                                it.email.equals(email, ignoreCase = true) &&
                                        it.contrasenya == encriptado
                            }
                            if (usuari != null) {

                                val tipus = usuari.tipusUsuari
                                val isAdmin = tipus == "ORGANITZADOR" || tipus == "SUPERUSUARI"

                                // Lanzar MainActivity con flag
                                val intent = Intent(this@IniciarSesionActivity, MainActivity::class.java)
                                intent.putExtra("isAdmin", isAdmin)
                                intent.putExtra("userId", usuari.id)
                                intent.putExtra("userName", usuari.nom)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@IniciarSesionActivity,
                                    "Email o contrasenya incorrectes",
                                    Toast.LENGTH_SHORT
                                              ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@IniciarSesionActivity,
                                "Error al conectar. Código de respuesta: (codi ${response.code()})",
                                Toast.LENGTH_SHORT
                                          ).show()
                        }
                    }

                    override fun onFailure(call: Call<List<Usuari>>, t: Throwable) {
                        Toast.makeText(
                            this@IniciarSesionActivity,
                            "Error de red: ${t.message}",
                            Toast.LENGTH_SHORT
                                      ).show()
                    }
                })
            }
        }
    }
    fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
