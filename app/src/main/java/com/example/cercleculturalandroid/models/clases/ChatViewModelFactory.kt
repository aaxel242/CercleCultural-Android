package com.example.cercleculturalandroid.models.clases

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cercleculturalandroid.models.fragments.fragmentChat

class ChatViewModelFactory(
    private val listener: SocketManager.MessageListener // Cambiar de Context a MessageListener
                          ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(fragmentChat.ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return fragmentChat.ChatViewModel(
                listener, apiService = TODO()
                                             ) as T // Pasar el listener
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
