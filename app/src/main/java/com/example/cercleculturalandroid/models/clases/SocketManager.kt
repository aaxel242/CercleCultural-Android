package com.example.cercleculturalandroid.models.clases

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.Date
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class SocketManager(private var messageListener: MessageListener?) {

    interface MessageListener {
        fun onMessageReceived(mensaje: Mensajes)
        fun onConnectionStatusChanged(connected: Boolean)
        fun onError(error: String)
    }

    companion object {
        private const val TAG = "SocketManager"
        private const val SERVER_IP = "10.0.0.121"
        private const val SERVER_PORT = 8888
        private const val RECONNECT_DELAY = 3000L
    }

    private var socket: Socket? = null
    private var out: PrintWriter? = null
    private var input: BufferedReader? = null
    private val executor: ExecutorService = Executors.newFixedThreadPool(2)
    private val isConnected = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun connect() {
        executor.execute {
            try {
                if (isConnected.get()) return@execute

                Log.d(TAG, "Connecting...")
                socket = Socket(SERVER_IP, SERVER_PORT).apply {
                    keepAlive = true
                    soTimeout = 15000
                }
                out = PrintWriter(socket!!.getOutputStream(), true)
                input = BufferedReader(InputStreamReader(socket!!.inputStream))
                isConnected.set(true)
                Log.d(TAG, "Connected!")
                handler.post { messageListener?.onConnectionStatusChanged(true) }
                startListening()
            } catch (e: Exception) {
                Log.e(TAG, "Connection failed: ${e.message}")
                handler.post {
                    messageListener?.onError("Connection error: ${e.message}")
                    messageListener?.onConnectionStatusChanged(false)
                }
                scheduleReconnect()
            }
        }
    }

    private fun startListening() {
        executor.execute {
            try {
                while (isConnected.get()) {
                    val message = input?.readLine() ?: throw Exception("Stream closed")
                    Log.d(TAG, "Raw message: $message")
                    parseMessage(message)?.let {
                        handler.post { messageListener?.onMessageReceived(it) }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Listen error: ${e.message}")
                disconnect()
                handler.post {
                    messageListener?.onError("Connection lost")
                    messageListener?.onConnectionStatusChanged(false)
                }
                scheduleReconnect()
            }
        }
    }

    fun sendMessage(mensaje: Mensajes) {
        executor.execute {
            if (!isConnected.get()) {
                handler.post { messageListener?.onError("Not connected") }
                return@execute
            }
            try {
                out?.println(formatMessage(mensaje))
                Log.d(TAG, "Message sent")
            } catch (e: Exception) {
                Log.e(TAG, "Send failed: ${e.message}")
                handler.post { messageListener?.onError("Send failed") }
            }
        }
    }

    fun disconnect() {
        executor.execute {
            try {
                if (isConnected.compareAndSet(true, false)) { // Asegura una sola ejecución
                    input?.close()
                    out?.close()
                    socket?.close()
                    handler.post {
                        messageListener?.onConnectionStatusChanged(false)
                    }
                    Log.d(TAG, "Desconexión exitosa")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al desconectar: ${e.message}")
            }
        }
    }

    private fun scheduleReconnect() {
        if (!isConnected.get()) { // Solo reconecta si está desconectado
            handler.postDelayed({ connect() }, RECONNECT_DELAY)
        }
    }

    private fun formatMessage(mensaje: Mensajes): String {
        return listOf(
            "0",
            mensaje.usuari_id.toString(),
            mensaje.nom_usuari,
            mensaje.missatge,
            "0"
                     ).joinToString("|")
    }

    private fun parseMessage(message: String): Mensajes? {
        return try {
            val parts = message.split("|")
            if (parts.size != 5) throw Exception("Invalid format")

            Mensajes(
                id = parts[0],
                usuari_id = parts[1].toInt(),
                nom_usuari = parts[2],
                missatge = parts[3],
                dataEnviament = Date() // Usar fecha del servidor
                    )
        } catch (e: Exception) {
            Log.e(TAG, "Parse error: ${e.message}")
            null
        }
    }
}