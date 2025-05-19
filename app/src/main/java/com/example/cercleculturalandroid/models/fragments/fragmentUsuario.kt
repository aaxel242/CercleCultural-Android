package com.example.cercleculturalandroid.models.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
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
import com.example.cercleculturalandroid.models.clases.IdiomaDTO
import com.example.cercleculturalandroid.models.clases.Reserva
import com.example.cercleculturalandroid.models.clases.Usuari
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
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


    companion object {
        private const val REQUEST_IMAGE_CAPTURE    = 1001
        private const val REQUEST_IMAGE_PERMISSION = 1002


        fun newInstance(userId: Int) = fragmentUsuario().apply {
            arguments = Bundle().apply {
                putInt("userId", userId)
            }
        }
    }

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
        view.findViewById<Button>(R.id.button_catalan).setOnClickListener { setLanguage("ca") }
        view.findViewById<Button>(R.id.button_spanish).setOnClickListener { setLanguage("es") }
        view.findViewById<Button>(R.id.button_english).setOnClickListener { setLanguage("en") }
    }

    private fun setupProfileImage() {
        imgProfile.setOnClickListener {
            checkPermissionsAndOpenPicker()
        }
    }

    private fun checkPermissionsAndOpenPicker() {
        // Cámara
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_IMAGE_PERMISSION
            )
            return
        }
        // Lectura externa
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_IMAGE_PERMISSION
            )
            return
        }
        // Ya tenemos ambos permisos
        openImagePicker()
    }


    private fun openImagePicker() {
        // Galería
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Fichero temporal
        val photoFile = createImageFile()
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )
        // Cámara
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            .apply { putExtra(MediaStore.EXTRA_OUTPUT, uri) }

        // Chooser
        val chooser = Intent.createChooser(galleryIntent, "Selecciona imagen")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))
        startActivityForResult(chooser, REQUEST_IMAGE_CAPTURE)
    }

    private fun createImageFile(): File {
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())
        val dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${ts}_", ".jpg", dir).also {
            currentPhotoPath = it.absolutePath
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_IMAGE_PERMISSION) {
            if (grantResults.isNotEmpty()
                && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            ) {
                openImagePicker()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Necesitamos permisos para tomar/seleccionar la foto",
                    Toast.LENGTH_LONG
                ).show()
                openImagePicker()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        val imageUri: Uri? = when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                currentPhotoPath?.let { path -> Uri.fromFile(File(path)) }
            }
            else -> data?.data
        }
        imageUri?.let {
            imgProfile.setImageURI(it)
            uploadImageToServer(it)
        }
    }


    private fun handleImageSelection(imageUri: Uri) {
        imgProfile.setImageURI(imageUri)
        uploadImageToServer(imageUri)
    }

    private fun uploadImageToServer(imageUri: Uri) {
        // 1) Construimos MultipartBody.Part
        val path = currentPhotoPath ?: return
        val file = File(path)
        val reqFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", file.name, reqFile)

        // 2) Log del payload para depuración
        Log.d("UPLOAD_PAYLOAD", "Uploading file: ${file.name}, path: $path")

        RetrofitClient.getService().uploadProfileImage(userId, part)
            .enqueue(object : Callback<Usuari> {
                override fun onResponse(call: Call<Usuari>, resp: Response<Usuari>) {
                    if (resp.isSuccessful) {
                        // éxito…
                        loadUserData()
                    } else {
                        // 1) Leo cuerpo completo
                        val code = resp.code()
                        val bodyStr = resp.errorBody()?.string() ?: "{}"
                        Log.e("UPLOAD_ERROR", "HTTP $code\n$bodyStr")

                        // 2) Intento parsear el JSON para extraer “Details”
                        try {
                            val obj = JSONObject(bodyStr)
                            val err = obj.optString("Error", "Error $code")
                            val details = obj.optJSONArray("Details")
                            val detailMsg = StringBuilder(err).apply {
                                if (details != null) {
                                    for (i in 0 until details.length()) {
                                        append("\n• ").append(details.getString(i))
                                    }
                                } else {
                                    append("\n$bodyStr")
                                }
                            }.toString()

                            showErrorDialog("Error al subir imagen (HTTP $code)", detailMsg)
                        } catch (e: Exception) {
                            // si no es JSON, muestro todo el texto
                            showErrorDialog("Error al subir imagen (HTTP $code)", bodyStr)
                        }
                    }
                }

                override fun onFailure(call: Call<Usuari>, t: Throwable) {
                    showErrorDialog("Fallo de red", t.localizedMessage ?: t.toString())
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
        RetrofitClient.getService()
            .getUsuari(userId)
            .enqueue(object : Callback<Usuari> {
                override fun onResponse(call: Call<Usuari>, resp: Response<Usuari>) {
                    if (!resp.isSuccessful) {
                        Toast.makeText(requireContext(),
                                       "Error al cargar usuario: ${resp.code()}",
                                       Toast.LENGTH_LONG).show()
                        return
                    }
                    resp.body()?.let { user ->
                        // 1) Nombre y correo (existente)
                        editUsuari.setText(user.nom)
                        editCorreu.setText(user.email)


                        // 3) FotoPerfil (existente)
                        user.fotoPerfil?.takeIf { it.isNotBlank() }?.let { filename ->
                            val imageUrl = "${RetrofitClient.BASE_URL}imagenes/$filename"
                            Glide.with(requireContext())
                                .load(imageUrl)
                                .circleCrop()
                                .placeholder(R.drawable.img_cc_logo_blanco_y_negro)
                                .into(imgProfile)
                        } ?: run {
                            imgProfile.setImageResource(R.drawable.img_cc_logo_blanco_y_negro)
                        }
                    }
                }

                override fun onFailure(call: Call<Usuari>, t: Throwable) {
                    Toast.makeText(requireContext(),
                                   "Error de conexión: ${t.localizedMessage}",
                                   Toast.LENGTH_LONG).show()
                }
            })
    }


    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun showErrorDialog(title: String, message: String) {
        val tv = TextView(requireContext()).apply {
            text = message
            setPadding(24,24,24,24)
        }
        val scroll = ScrollView(requireContext()).apply {
            addView(tv)
        }
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(scroll)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun setLanguage(languageCode: String) {
        // 1. Guardar idioma en SharedPreferences
        val prefs = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        prefs.edit().putString("MY_LANG", languageCode).apply()

        // 2. Actualizar configuración
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)

        // 3. Aplicar cambios (forma correcta para Android 10+)
        requireContext().createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)

        // 4. Recargar actividad
        activity?.recreate()
    }

// En onViewCreated:




}
