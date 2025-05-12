package com.example.cercleculturalandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.butacas.core.MyGdxGame

class fragmentGdx : AndroidFragmentApplication() {
    private lateinit var game: MyGdxGame

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
                             ): View {
        // Instancia y configura el juego
        game = MyGdxGame()
        val config = AndroidApplicationConfiguration().apply {
            useAccelerometer = false
            useCompass       = false
        }
        // Devuelve la vista GL embebida
        return initializeForView(game, config)
    }

    /** Expuesto a la UI Android para reservar butacas */
    fun reserveSeat(col: Int, row: Int) {
        game.selectSeat(col, row)
    }
}
