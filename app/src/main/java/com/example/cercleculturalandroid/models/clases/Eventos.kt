package com.example.cercleculturalandroid.models.clases

data class Eventos(
    val id: Int,
    val nom: String,
    val descripcio: String,
    val dataInici: String,      // yyyy-MM-dd'T'HH:mm:ss
    val espai_id: Int,
    val imatge: String?,
    val per_infants: Boolean
                       )