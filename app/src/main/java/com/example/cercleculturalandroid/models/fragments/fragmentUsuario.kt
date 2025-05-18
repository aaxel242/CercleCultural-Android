package com.example.cercleculturalandroid.models.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cercleculturalandroid.R
import com.example.cercleculturalandroid.api.ApiService
import com.example.cercleculturalandroid.api.RetrofitClient
import com.example.cercleculturalandroid.models.adapters.ReservasAdapter
import com.example.cercleculturalandroid.models.clases.Reserva
import com.example.cercleculturalandroid.models.clases.Usuari
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class fragmentUsuario : Fragment() {
    private lateinit var editUsuari: EditText
    private lateinit var editCorreu: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var imgProfile: ImageView
    private val userId by lazy { arguments?.getInt("userId") ?: -1 }
    private lateinit var reservasAdapter: ReservasAdapter
    private var currentPhotoPath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
                             ): View? {
        return inflater.inflate(R.layout.fragment_usuario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupProfileImage()
        setupRecyclerView()
        loadUserData()
        loadReservasData()
    }

    private fun initViews(view: View) {
        editUsuari = view.findViewById(R.id.EditTextUsuari)
        editCorreu = view.findViewById(R.id.EditTextCorreu)
        recyclerView = view.findViewById(R.id.recyclerReservas)
        imgProfile = view.findViewById(R.id.logo_admin)
    }

    private fun setupProfileImage() {
        imgProfile.setOnClickListener {
            checkPermissionsAndOpenPicker()
        }
    }

    private fun checkPermissionsAndOpenPicker() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
                                 )

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
                                             ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(permissions, REQUEST_IMAGE_PERMISSION)
        } else {
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val photoFile = createImageFile()
        val photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
                                                 )
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }

        val chooserIntent = Intent.createChooser(galleryIntent, "Seleccionar imagen")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))
        startActivityForResult(chooserIntent, REQUEST_IMAGE_CAPTURE)
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
                                  ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
                                           ) {
        if (requestCode == REQUEST_IMAGE_PERMISSION && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            openImagePicker()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageUri = Uri.fromFile(File(currentPhotoPath ?: return))
                    handleImageSelection(imageUri)
                }
                else -> {
                    data?.data?.let { uri ->
                        handleImageSelection(uri)
                    }
                }
            }
        }
    }

    private fun handleImageSelection(imageUri: Uri) {
        imgProfile.setImageURI(imageUri)
        uploadImageToServer(imageUri)
    }

    private fun uploadImageToServer(imageUri: Uri) {
        val file = File(currentPhotoPath ?: return)
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        RetrofitClient.getClient()
            .create(ApiService::class.java)
            .uploadProfileImage(userId, body)
            .enqueue(object : Callback<Usuari> {
                override fun onResponse(call: Call<Usuari>, response: Response<Usuari>) {
                    if (response.isSuccessful) {
                        Log.d("Upload", "Imagen subida: ${response.body()?.profileImage}")
                        loadUserData()
                    } else {
                        Log.e("Upload", "Error del servidor: ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<Usuari>, t: Throwable) {
                    Log.e("Upload", "Error de red: ${t.message}")
                }
            })
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
        if (userId == -1) return

        RetrofitClient.getClient()
            .create(ApiService::class.java)
            .getUsuari(userId)
            .enqueue(object : Callback<Usuari> {
                override fun onResponse(call: Call<Usuari>, response: Response<Usuari>) {
                    if (response.isSuccessful) {
                        response.body()?.let { user ->
                            editUsuari.setText(user.nom)
                            editCorreu.setText(user.email)
                            user.profileImage?.let { imageName ->
                                Glide.with(requireContext())
                                    //.load("${RetrofitClient.BASE_URL}api/Usuaris/GetImage/$imageName")
                                   // .circleCrop()
                                    //.placeholder(R.drawable.img_cc_logo_blanco_y_negro)
                                    //.into(imgProfile)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<Usuari>, t: Throwable) {
                    showError("Error de conexión: ${t.message}")
                }
            })
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1001
        private const val REQUEST_IMAGE_PERMISSION = 1002

        fun newInstance(userId: Int) = fragmentUsuario().apply {
            arguments = Bundle().apply {
                putInt("userId", userId)
            }
        }
    }
}