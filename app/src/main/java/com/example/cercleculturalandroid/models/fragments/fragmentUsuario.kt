package com.example.cercleculturalandroid.models.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cercleculturalandroid.R
import com.example.cercleculturalandroid.api.ApiService
import com.example.cercleculturalandroid.api.RetrofitClient
import com.example.cercleculturalandroid.models.adapters.ReservasAdapter
import com.example.cercleculturalandroid.models.clases.Reserva
import com.example.cercleculturalandroid.models.clases.Usuari
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class fragmentUsuario : Fragment() {

    private lateinit var editUsuari: android.widget.EditText
    private lateinit var editCorreu: android.widget.EditText
    private lateinit var recyclerView: RecyclerView
    private val userId by lazy { arguments?.getInt("userId") ?: -1 }
    private lateinit var reservasAdapter: ReservasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
                             ): View? {
        return inflater.inflate(R.layout.fragment_usuario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupRecyclerView()
        loadUserData()
        loadReservasData()
    }

    private fun initViews(view: View) {
        editUsuari = view.findViewById(R.id.EditTextUsuari)
        editCorreu = view.findViewById(R.id.EditTextCorreu)
        recyclerView = view.findViewById(R.id.recyclerReservas)
    }

    private fun setupRecyclerView() {
        reservasAdapter = ReservasAdapter(emptyList())
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reservasAdapter
            setHasFixedSize(true)
        }
    }

    private fun loadReservasData() {
        RetrofitClient.getClient()
            .create(ApiService::class.java)
            .getReservasPerfil(userId)
            .enqueue(object : Callback<List<Reserva>> {
                override fun onResponse(call: Call<List<Reserva>>, response: Response<List<Reserva>>) {
                    if (response.isSuccessful) {
                        response.body()?.let { reservas ->
                            reservasAdapter.updateData(reservas)
                        } ?: showError("No se encontraron reservas")
                    } else {
                        showError("Error al obtener reservas: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<Reserva>>, t: Throwable) {
                    showError("Error de conexión: ${t.message}")
                }
            })
    }

    private fun loadUserData() {
        if (userId == -1) {
            showError("ID de usuario no válido")
            return
        }

        RetrofitClient.getClient()
            .create(ApiService::class.java)
            .getUsuari(userId)
            .enqueue(object : Callback<Usuari> {
                override fun onResponse(call: Call<Usuari>, response: Response<Usuari>) {
                    if (response.isSuccessful) {
                        response.body()?.let { user ->
                            updateUserInfo(user)
                        } ?: showError("Usuario no encontrado")
                    } else {
                        showError("Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Usuari>, t: Throwable) {
                    showError("Error de conexión: ${t.message}")
                }
            })
    }

    private fun updateUserInfo(user: Usuari) {
        editUsuari.setText(user.nom)
        editCorreu.setText(user.email)
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    companion object {
        fun newInstance(userId: Int) = fragmentUsuario().apply {
            arguments = Bundle().apply {
                putInt("userId", userId)
            }
        }
    }
}