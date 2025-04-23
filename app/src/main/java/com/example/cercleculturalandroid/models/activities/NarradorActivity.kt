package com.example.cercleculturalandroid.models.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.airbnb.lottie.LottieAnimationView
import com.example.cercleculturalandroid.R

class NarradorActivity : AppCompatActivity() {

    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var imgMicro: ImageView
    private lateinit var imgMicroMute: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.narrador_layout)

        // Inicializar animación Lottie
        val animDeslizarDedo = findViewById<LottieAnimationView>(R.id.animDeslizarDedo)
        animDeslizarDedo.playAnimation()

        // Referencias a las ImageView
        imgMicro = findViewById(R.id.imgMicro)
        imgMicroMute = findViewById(R.id.imgMicroMute)

        // Inicializar GestureDetector
        gestureDetector = GestureDetectorCompat(this, MyGestureListener())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {

        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
                            ): Boolean {
            if (e1 == null || e2 == null) return false

            val diffX = e2.x - e1.x

            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                runOnUiThread {
                    if (diffX > 0) {
                        // Deslizar de izquierda a derecha
                        imgMicroMute.setImageResource(R.drawable.img_micro_mute_rojo)
                        imgMicro.visibility = View.INVISIBLE
                    } else {
                        // Deslizar de derecha a izquierda
                        imgMicro.setImageResource(R.drawable.img_micro_verde)
                        imgMicroMute.visibility = View.INVISIBLE
                    }

                    // Retrasar la navegación sin bloquear la UI
                    Handler(Looper.getMainLooper()).postDelayed({
                                                                    abrirIniciarSesionActivity()
                                                                }, 500)
                }
                return true
            }
            return false
        }
    }

    private fun abrirIniciarSesionActivity() {
        val intent = Intent(this, IniciarSesionActivity::class.java)
        startActivity(intent)
        finish()
    }
}