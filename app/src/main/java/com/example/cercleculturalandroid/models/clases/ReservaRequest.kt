package com.example.cercleculturalandroid.models.clases

import com.google.gson.annotations.SerializedName

data class ReservaRequest(
    @SerializedName("usuari_id")       val usuari_id      : Int,
    @SerializedName("esdeveniment_id") val esdeveniment_id: Int,
    @SerializedName("espai_id")        val espai_id       : Int,
    @SerializedName("dataReserva")     val dataReserva    : String,
    @SerializedName("estat")           val estat          : String,
    @SerializedName("tipus")           val tipus          : String,
    @SerializedName("dataInici")       val dataInici      : String,
    @SerializedName("dataFi")          val dataFi         : String,
    @SerializedName("numPlaces")       val numPlaces      : Int
                         )

