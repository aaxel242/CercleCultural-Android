package com.example.cercleculturalandroid.models.clases

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Eventos(
    @SerializedName("id")          val id: Int,
    @SerializedName("nom")         val nom: String,
    @SerializedName("descripcio")  val descripcio: String,
    @SerializedName("dataInici")   val dataInici: String,
    @SerializedName("dataFi")      val dataFi: String?,
    @SerializedName("aforament")   val aforament: Int,
    @SerializedName("espai_id")    val espai_id: Int,
    @SerializedName("imatge")      val imatge: String?,
    @SerializedName("per_infants") val per_infants: Boolean
                  ) : Parcelable