package com.example.cercleculturalandroid.models.clases

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class SocketManager(private val messageListener: MessageListener?) {

    interface MessageListener {
        fun onMessageReceived(mensaje: Mensajes)
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
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val isConnected = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    fun connect() {
        executor.execute {
            try {
                if (isConnected.get()) return@execute

                Log.d(TAG, "Connecting to $SERVER_IP:$SERVER_PORT")
                socket = Socket(SERVER_IP, SERVER_PORT)
                socket?.let {
                    out = PrintWriter(it.getOutputStream(), true)
                    input = BufferedReader(InputStreamReader(it.inputStream))
                    isConnected.set(true)
                    Log.d(TAG, "Connected successfully")
                    startListening()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Connection error: ${e.message}")
                handler.postDelayed({ connect() }, RECONNECT_DELAY)
                notifyError("Connection error: ${e.message}")
            }
        }
    }

    private fun startListening() {
        executor.execute {
            try {
                while (isConnected.get()) {
                    val message = input?.readLine() ?: break
                    Log.d(TAG, "Received raw message: $message")
                    parseMessage(message)?.let {
                        handler.post { messageListener?.onMessageReceived(it) }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Listening error: ${e.message}")
                disconnect()
                notifyError("Connection lost: ${e.message}")
            }
        }
    }

    fun sendMessage(mensaje: Mensajes) {
        executor.execute {
            try {
                if (isConnected.get()) {
                    val formatted = formatMessage(mensaje)
                    Log.d(TAG, "Sending message: $formatted")
                    out?.println(formatted)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Send error: ${e.message}")
                notifyError("Failed to send message")
            }
        }
    }

    fun disconnect() {
        try {
            isConnected.set(false)
            input?.close()
            out?.close()
            socket?.close()
            executor.shutdown()
            Log.d(TAG, "Disconnected successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Disconnection error: ${e.message}")
        }
    }

    private fun formatMessage(mensaje: Mensajes): String {
        return listOf(
            mensaje.id.toString(),
            mensaje.usuari_id.toString(),
            mensaje.nom_usuari,
            mensaje.missatge,
            mensaje.dataEnviament.time.toString()
                     ).joinToString("|")
    }

    private fun parseMessage(message: String): Mensajes? {
        return try {
            val parts = message.split("|")
            if (parts.size != 5) throw IllegalArgumentException("Invalid message format")

            Mensajes().apply {
                id = parts[0].toInt()
                usuari_id = parts[1].toInt()
                nom_usuari = parts[2]
                missatge = parts[3]
                dataEnviament = Date(parts[4].toLong())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Message parsing error: ${e.message}")
            null
        }
    }

    private fun notifyError(error: String) {
        handler.post { messageListener?.onError(error) }
    }
}