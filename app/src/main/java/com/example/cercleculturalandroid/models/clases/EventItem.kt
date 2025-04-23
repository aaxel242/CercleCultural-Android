package com.example.cercleculturalandroid.models.clases

data class EventItem (
    val id: Int,
    val nom: String,
    val descripcio: String,
    val dataInici: String,
    val espai_id: Int,
    val ubicacio: String,
    //val imatge: String?,
    val perInfants: Boolean
                     )