package com.example.cercleculturalandroid.models.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.example.cercleculturalandroid.models.clases.core.MyGdxGame

class fragmentGdx : AndroidFragmentApplication(), AndroidFragmentApplication.Callbacks {
    fun createApplicationListener(): ApplicationListener =
        MyGdxGame()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
                             ): View {
        val config = AndroidApplicationConfiguration().apply {
            // desactiva sensores que no uses:
            useAccelerometer = false
            useCompass = false
        }
        return initializeForView(createApplicationListener(), config)
    }

    override fun exit() {
        // si MyGdxGame llama Gdx.app.exit()
    }
}
