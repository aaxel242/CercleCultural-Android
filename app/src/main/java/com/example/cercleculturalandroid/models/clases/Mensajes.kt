// Mensajes.kt
package com.example.cercleculturalandroid.models.clases

import java.util.Date

data class Mensajes(
    var id: String = "",
    var usuari_id: Int = 0,
    var nom_usuari: String = "",
    var missatge: String = "",
    var dataEnviament: Date = Date()
                   )
