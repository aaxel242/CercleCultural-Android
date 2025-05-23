package com.example.cercleculturalandroid.models.clases

import com.google.gson.annotations.SerializedName

data class Usuari(
    val id: Int,
    val nom: String,
    val email: String,
    val contrasenya: String,
    val tipusUsuari: String,
    val idioma: String,
    @SerializedName("FotoPerfil")
    val fotoPerfil: String?
                 )
