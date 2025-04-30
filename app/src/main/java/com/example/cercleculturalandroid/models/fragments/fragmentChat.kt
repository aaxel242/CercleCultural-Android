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
import com.example.cercleculturalandroid.models.adapters.ChatAdapter
import com.example.cercleculturalandroid.models.clases.Mensajes
import com.example.cercleculturalandroid.models.clases.SocketManager
import java.util.Date

class fragmentChat : Fragment(), SocketManager.MessageListener {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatAdapter: ChatAdapter
    private val currentUserId = 1 // ID del usuario
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
        setupRecyclerView()
        setupSocket()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(mutableListOf(), currentUserId)
        binding.rvMensajesChat.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }
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
                    nom_usuari = "Tú"
                    missatge = messageText
                    dataEnviament = Date()
                }
                chatAdapter.addMessage(newMessage)
                binding.rvMensajesChat.smoothScrollToPosition(chatAdapter.itemCount - 1)
                socketManager.sendMessage(newMessage)
                binding.etMessage.text.clear()
            }
        }
    }

    override fun onMessageReceived(mensaje: Mensajes) {
        requireActivity().runOnUiThread {
            chatAdapter.addMessage(mensaje)
            binding.rvMensajesChat.smoothScrollToPosition(chatAdapter.itemCount - 1)
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
    }
}