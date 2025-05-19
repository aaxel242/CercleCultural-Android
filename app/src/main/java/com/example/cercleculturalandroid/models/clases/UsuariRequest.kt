package com.example.cercleculturalandroid.models.clases

data class UsuariRequest(
    val nom: String,
    val email: String,
    val contrasenya: String,
    val tipusUsuari: String = "NORMAL",
    val idioma: String = "ca"
                        )
