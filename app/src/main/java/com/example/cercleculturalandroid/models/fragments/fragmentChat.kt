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
import com.example.cercleculturalandroid.api.ApiService
import com.example.cercleculturalandroid.api.RetrofitClient
import com.example.cercleculturalandroid.databinding.FragmentChatBinding
import com.example.cercleculturalandroid.models.adapters.ChatAdapter
import com.example.cercleculturalandroid.models.clases.Mensajes
import com.example.cercleculturalandroid.models.clases.SocketManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class fragmentChat : Fragment(), SocketManager.MessageListener {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatAdapter: ChatAdapter
    private val userId by lazy { arguments?.getInt("userId") ?: -1 }
    private val userName by lazy { arguments?.getString("userName") ?: "Usuari" }
    private val apiService: ApiService = RetrofitClient.getClient().create(ApiService::class.java)

    class ChatViewModel(
        private val listener: SocketManager.MessageListener,
        private val apiService: ApiService
                       ) : ViewModel() {
        val socketManager = SocketManager(listener).apply { connect() }

        fun loadInitialMessages(callback: (List<Mensajes>) -> Unit) {
            apiService.getMensajes().enqueue(object : Callback<List<Mensajes>> {
                override fun onResponse(call: Call<List<Mensajes>>, response: Response<List<Mensajes>>) {
                    if (response.isSuccessful) {
                        callback(response.body() ?: emptyList())
                    } else {
                        listener.onError("Error cargando mensajes: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<Mensajes>>, t: Throwable) {
                    listener.onError("Error de conexión: ${t.message}")
                }
            })
        }

        fun sendMessageToApi(mensaje: Mensajes) {
            apiService.postMensaje(mensaje).enqueue(object : Callback<Mensajes> {
                override fun onResponse(call: Call<Mensajes>, response: Response<Mensajes>) {
                    if (!response.isSuccessful) {
                        listener.onError("Error al guardar mensaje: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Mensajes>, t: Throwable) {
                    listener.onError("Error de red: ${t.message}")
                }
            })
        }
    }

    class ChatViewModelFactory(
        private val listener: SocketManager.MessageListener,
        private val apiService: ApiService
                              ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ChatViewModel(listener, apiService) as T
            }
            throw IllegalArgumentException("ViewModel class no reconocida")
        }
    }

    private val viewModel: ChatViewModel by viewModels { ChatViewModelFactory(this, apiService) }

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
            id = "",
            usuari_id = userId,
            nom_usuari = userName,
            missatge = text,
            dataEnviament = Date()
                                 )

        // Envía a través de Socket y guarda en la API
        viewModel.socketManager.sendMessage(newMessage)
        viewModel.sendMessageToApi(newMessage)
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        disconnectFromServer()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        if (isRemoving) {
            disconnectFromServer()
        }
    }

    private fun disconnectFromServer() {
        viewModel.socketManager.disconnect()
        Toast.makeText(requireContext(), "Desconectado del chat", Toast.LENGTH_SHORT).show()
    }
}