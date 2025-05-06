package com.example.cercleculturalandroid.models.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cercleculturalandroid.databinding.FragmentChatBinding
import com.example.cercleculturalandroid.models.adapters.ChatAdapter
import com.example.cercleculturalandroid.models.clases.Mensajes
import com.example.cercleculturalandroid.models.clases.SocketManager
import java.util.Date

class fragmentChat : Fragment(), SocketManager.MessageListener {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatAdapter: ChatAdapter
    private val userId by lazy { arguments?.getInt("userId") ?: -1 }

    // ViewModel personalizado
    class ChatViewModel(private val listener: SocketManager.MessageListener) : ViewModel() {
        val socketManager = SocketManager(listener).apply { connect() }

        fun loadInitialMessages(callback: (List<Mensajes>) -> Unit) {
            // Simular carga de mensajes desde API
            val mockMessages = listOf(
                Mensajes("1", 1, "Sistema", "Bienvenido al chat", Date()),
                Mensajes("2", 2, "Soporte", "¿En qué podemos ayudarte?", Date())
                                     )
            callback(mockMessages)
        }
    }

    // Factory para el ViewModel
    class ChatViewModelFactory(
        private val listener: SocketManager.MessageListener
                              ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ChatViewModel(listener) as T
            }
            throw IllegalArgumentException("ViewModel class no reconocida")
        }
    }

    private val viewModel: ChatViewModel by viewModels { ChatViewModelFactory(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
                             ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChat()
        loadInitialMessages()
    }

    private fun setupChat() {
        setupRecyclerView()
        setupSendButton()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(mutableListOf(), userId)
        binding.rvMensajesChat.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
    }

    private fun setupSendButton() {
        binding.btnSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessageToServer(messageText)
                binding.etMessage.text?.clear()
            }
        }
    }

    private fun loadInitialMessages() {
        viewModel.loadInitialMessages { messages ->
            activity?.runOnUiThread {
                chatAdapter.updateMessages(messages.toMutableList())
                scrollToBottom()
            }
        }
    }

    private fun sendMessageToServer(text: String) {
        val newMessage = Mensajes(
            id = "", // El servidor asignará ID
            usuari_id = userId,
            nom_usuari = "Usuario", // Reemplazar con nombre real del usuario
            missatge = text,
            dataEnviament = Date()
                                 )
        viewModel.socketManager.sendMessage(newMessage)
    }

    // SocketManager.MessageListener implementations
    override fun onMessageReceived(mensaje: Mensajes) {
        activity?.runOnUiThread {
            chatAdapter.addMessage(mensaje)
            scrollToBottom()
        }
    }

    override fun onConnectionStatusChanged(connected: Boolean) {
        activity?.runOnUiThread {
            val status = if (connected) "Conectado" else "Desconectado"
            Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onError(error: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        }
    }

    private fun scrollToBottom() {
        binding.rvMensajesChat.smoothScrollToPosition(chatAdapter.itemCount - 1)
    }

    // En fragmentChat.kt
    override fun onDestroyView() {
        super.onDestroyView()
        disconnectFromServer()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        if (isRemoving) { // Solo si el fragmento está siendo eliminado
            disconnectFromServer()
        }
    }

    private fun disconnectFromServer() {
        viewModel.socketManager.disconnect()
        Toast.makeText(requireContext(), "Desconectado del chat", Toast.LENGTH_SHORT).show()
    }
}