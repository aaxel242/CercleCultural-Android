package com.example.cercleculturalandroid.models.clases

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventItem(
    val id: Int,
    val nom: String,
    val descripcio: String,
    val dataInici: String,
    val dataFi: String,
    val aforament: Int,
    val espai_id: Int,
    val ubicacio: String,
    val imatge: String?,
    val perInfants: Boolean
                    ) : Parcelable