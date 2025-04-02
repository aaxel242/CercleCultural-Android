package com.example.cercleculturalandroid.api;

import com.example.cercleculturalandroid.models.Mensajes;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/Mensajes")
    Call<List<Mensajes>> getMensajes();

    @GET("api/Mensajes/{id}")
    Call<Mensajes> getMensaje(@Path("id") int id);

    @POST("api/Mensajes")
    Call<Mensajes> postMensaje(@Body Mensajes mensaje);
}
