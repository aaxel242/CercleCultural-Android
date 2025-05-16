package com.example.cercleculturalandroid.models.clases

import com.google.gson.annotations.SerializedName

    data class Reserva(
        @SerializedName("Esdeveniment") val esdeveniment: Any? = null,
        @SerializedName("Espai") val espai: Any? = null,
        @SerializedName("Usuari") val usuari: Any? = null,
        @SerializedName("Seients") val seients: List<Any> = emptyList(),
        @SerializedName("id") val id: Int,
        @SerializedName("usuari_id") val usuariId: Int,
        @SerializedName("dataReserva") val dataReserva: String,  // Mantenido como String
        @SerializedName("estat") val estat: String,
        @SerializedName("tipus") val tipus: String,
        @SerializedName("espai_id") val espaiId: Int? = null,
        @SerializedName("esdeveniment_id") val esdevenimentId: Int? = null,
        @SerializedName("dataInici") val dataInici: String? = null,  // Cambiado a String
        @SerializedName("dataFi") val dataFi: String? = null,        // Cambiado a String
        @SerializedName("numPlaces") val numPlaces: Int? = null
                  )