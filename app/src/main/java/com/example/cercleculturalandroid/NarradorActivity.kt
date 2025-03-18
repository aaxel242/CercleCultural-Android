package com.example.cercleculturalandroid

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.airbnb.lottie.LottieAnimationView

class NarradorActivity: AppCompatActivity() {

    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var imgMicro: ImageView
    private lateinit var imgMicroMute: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.narrador_layout)
        val animDeslizarDedo = findViewById<LottieAnimationView>(R.id.animDeslizarDedo)
        animDeslizarDedo.playAnimation()

        // Referencia a las ImageView
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
            if (e1 == null || e2 == null) return false  // Prevenir crashes

            val diffX = e2.x - e1.x

            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                runOnUiThread {
                    if (diffX > 0) {
                        // Deslizar de izquierda a derecha
                        imgMicroMute.setImageResource(R.drawable.img_micro_mute_rojo)
                        //Ocultar el otro micro
                        imgMicro.visibility = View.INVISIBLE
                        abrirMainActivity()


                    } else {
                        // Deslizar de derecha a izquierda
                        imgMicro.setImageResource(R.drawable.img_micro_verde)
                        //Ocultar el otro micro
                        imgMicroMute.visibility = View.INVISIBLE
                        abrirMainActivity()
                    }
                }
                return true
            }
            return false
        }

        private  fun abrirMainActivity() {
            //Delay medio segundo
            Thread.sleep(500)

            //Ir a MainActivity
            val intent = Intent(this@NarradorActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}