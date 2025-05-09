package com.example.cercleculturalandroid.models.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.example.cercleculturalandroid.models.core.MyGdxGame

class FragmentGdx : AndroidFragmentApplication() { // <-- Herencia correcta
    private var gameInitialized = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
                             ): View {
        val config = AndroidApplicationConfiguration().apply {
            useImmersiveMode = true
        }
        return initializeForView(MyGdxGame(), config)
    }

    // Ciclo de vida automático (NO necesitas métodos resume/pause manuales)
}