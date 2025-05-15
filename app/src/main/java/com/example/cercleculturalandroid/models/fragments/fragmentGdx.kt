package com.example.cercleculturalandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.butacas.core.MyGdxGame

class fragmentGdx : AndroidFragmentApplication() {
    private val game = MyGdxGame()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
                             ): View {
        val config = AndroidApplicationConfiguration().apply {
            useAccelerometer = false
            useCompass = false
            useGL30 = true // Try newer OpenGL version
            numSamples = 2 // Anti-aliasing
        }
        return initializeForView(game, config)
    }
}

