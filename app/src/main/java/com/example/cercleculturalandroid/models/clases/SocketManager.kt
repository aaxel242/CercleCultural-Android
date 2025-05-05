package com.example.cercleculturalandroid.models.clases
import android.os.Handler
import android.os.Looper
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.Date
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class SocketManager(private val messageListener: MessageListener?) {
    private var socket: Socket? = null
    private var out: PrintWriter? = null
    private var `in`: BufferedReader? = null
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    interface MessageListener {
        fun onMessageReceived(mensaje: Mensajes) // Sin nullable
        fun onError(error: String) // Sin nullable
    }

    fun connect() {
        executor.execute {
            try {
                println("DEBUG: Intentando conectar a $SERVER_IP:$SERVER_PORT")
                socket = Socket(SERVER_IP, SERVER_PORT)
                println("DEBUG: Conexión exitosa")
                out = PrintWriter(socket!!.getOutputStream(), true)
                `in` = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                startListening()
            } catch (e: IOException) {
                println("DEBUG: Error de conexión: ${e.message}")
                messageListener?.onError("Error de conexión: " + e.message)
            }
        }
    }

    private fun startListening() {
        Thread {
            try {
                var inputLine: String
                while ((`in`!!.readLine().also { inputLine = it }) != null) {
                    val mensaje = parseMessage(inputLine)
                    if (messageListener != null && mensaje != null) {
                        Handler(Looper.getMainLooper()).post {
                            messageListener.onMessageReceived(
                                mensaje
                                                             )
                        }
                    }
                }
            } catch (e: IOException) {
                messageListener?.onError("Error recibiendo mensaje: " + e.message)
            }
        }.start()
    }

    fun sendMessage(mensaje: Mensajes) {
        executor.execute {
            if (out != null && !socket!!.isClosed) {
                val formattedMsg = formatMessage(mensaje)
                out!!.println(formattedMsg)
            }
        }
    }

    private fun formatMessage(mensaje: Mensajes): String {
        return mensaje.id.toString() + "|" + mensaje.usuari_id + "|" + mensaje.nom_usuari + "|" + mensaje.missatge + "|" + mensaje.dataEnviament.time
    }

    private fun parseMessage(message: String): Mensajes? {
        try {
            val parts = message.split("|") // Cambio aquí: regex simple
            val msg = Mensajes()
            msg.id = parts[0].toInt()
            msg.usuari_id = parts[1].toInt() // Corregido de 'arts' a 'parts'
            msg.nom_usuari = parts[2]
            msg.missatge = parts[3]
            msg.dataEnviament = Date(parts[4].toLong())
            return msg
        } catch (e: Exception) {
            return null
        }
    }

    fun disconnect() {
        try {
            if (socket != null) socket!!.close()
            if (`in` != null) `in`!!.close()
            if (out != null) out!!.close()
            executor.shutdown()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val SERVER_IP = "10.0.0.121"
        private const val SERVER_PORT = 8888
    }
}