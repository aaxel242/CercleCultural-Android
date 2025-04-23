package com.example.cercleculturalandroid.models.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cercleculturalandroid.R
import com.example.cercleculturalandroid.api.ApiService
import com.example.cercleculturalandroid.models.adapters.MensajesAdapter
import com.example.cercleculturalandroid.api.RetrofitClient
import com.example.cercleculturalandroid.models.clases.Mensajes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class fragmentChat : Fragment() {

    private lateinit var rvMensajesChat: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
                             ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvMensajesChat = view.findViewById(R.id.rvMensajesChat)
        rvMensajesChat.layoutManager = LinearLayoutManager(requireContext())

        // Llamada a la API para obtener todos los mensajes
        val api = RetrofitClient.getClient().create(ApiService::class.java)
        api.getMensajes().enqueue(object : Callback<List<Mensajes>> {
            override fun onResponse(
                call: Call<List<Mensajes>>,
                response: Response<List<Mensajes>>
                                   ) {
                if (response.isSuccessful) {
                    val lista = response.body().orEmpty()
                    rvMensajesChat.adapter = MensajesAdapter(lista)
                } else {
                    Log.e("fragmentChat", "Código de error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Mensajes>>, t: Throwable) {
                Log.e("fragmentChat", "Error de red: ${t.message}")
            }
        })
    }
}