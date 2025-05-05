package com.example.cercleculturalandroid.models.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cercleculturalandroid.databinding.FragmentChatBinding
import com.example.cercleculturalandroid.api.ApiService
import com.example.cercleculturalandroid.api.RetrofitClient
import com.example.cercleculturalandroid.models.adapters.ChatAdapter
import com.example.cercleculturalandroid.models.clases.Mensajes
import com.example.cercleculturalandroid.models.clases.SocketManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class fragmentChat : Fragment(), SocketManager.MessageListener {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatAdapter: ChatAdapter
    private var currentUserId = -1
    private lateinit var socketManager: SocketManager

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
        currentUserId = arguments?.getInt("userId", -1) ?: -1
        Log.d("ChatDebug", "User ID: $currentUserId") // Verificar ID

        // Inicializar SocketManager con el lifecycle
        socketManager = SocketManager(this).apply {
            connect()
        }

        setupRecyclerView()
        loadMessages()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(mutableListOf(), currentUserId)
        binding.rvMensajesChat.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
    }

    private fun loadMessages() {
        val api = RetrofitClient.getClient().create(ApiService::class.java)
        api.getMensajes().enqueue(object : Callback<List<Mensajes>> {
            override fun onResponse(
                call: Call<List<Mensajes>>,
                response: Response<List<Mensajes>>
                                   ) {
                if (response.isSuccessful) {
                    val mensajes = response.body()?.sortedBy { it.dataEnviament } ?: emptyList()
                    chatAdapter.setMessages(mensajes)
                    scrollToBottom()
                } else {
                    Log.e("ChatFragment", "Error al cargar mensajes: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Mensajes>>, t: Throwable) {
                Log.e("ChatFragment", "Fallo en la conexión: ${t.message}")
            }
        })
    }

    private fun setupSocket() {
        socketManager = SocketManager(this)
        socketManager.connect()
    }

    private fun setupClickListeners() {
        binding.btnSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                val newMessage = Mensajes().apply {
                    usuari_id = currentUserId
                    nom_usuari = "Usuario Actual" // Nombre temporal
                    missatge = messageText
                    dataEnviament = Date()
                }
                chatAdapter.addMessage(newMessage)
                socketManager.sendMessage(newMessage)
                binding.etMessage.text.clear()
                scrollToBottom()
            }
        }
    }

    private fun scrollToBottom() {
        binding.rvMensajesChat.postDelayed({
                                               binding.rvMensajesChat.smoothScrollToPosition(chatAdapter.itemCount - 1)
                                           }, 100)
    }

    override fun onMessageReceived(mensaje: Mensajes) {
        requireActivity().runOnUiThread {
            chatAdapter.addMessage(mensaje)
            scrollToBottom()
        }
    }

    override fun onError(error: String) {
        requireActivity().runOnUiThread {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        socketManager.disconnect()
        _binding = null
        Log.d("ChatDebug", "Fragment destroyed") // Verificar ciclo de vida
    }
}