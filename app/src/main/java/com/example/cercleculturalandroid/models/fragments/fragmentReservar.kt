package com.example.cercleculturalandroid.models.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.example.cercleculturalandroid.R
import com.example.cercleculturalandroid.models.clases.EventItem
import com.example.cercleculturalandroid.models.clases.core.MyGdxGame

// 1. En fragmentReservar.kt:
class fragmentReservar : Fragment() {
    // Declara la variable para mantener la referencia
    private var gdxFragment: fragmentGdx? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Crea y guarda la instancia
        gdxFragment = fragmentGdx()
        childFragmentManager.beginTransaction().replace(R.id.gdx_container, gdxFragment!!).commit()
    }


    // 2. En fragmentGdx.kt (corrección de hideStatusBar):
    class fragmentGdx : AndroidFragmentApplication() {
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
                                 ): View {
            val config = AndroidApplicationConfiguration().apply {
                useImmersiveMode = false // <- Corregido aquí
                useAccelerometer = false
                useCompass = false
            }

            return initializeForView(MyGdxGame(), config).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                                                     )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Gdx.app.postRunnable {
            if (Gdx.graphics != null) {
                Gdx.app.log("LIFECYCLE", "OpenGL Context Active: ${Gdx.gl != null}")
            }
        }
    }
}
