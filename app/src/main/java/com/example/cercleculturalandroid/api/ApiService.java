package com.example.cercleculturalandroid.api;

import com.example.cercleculturalandroid.models.clases.Espai;
import com.example.cercleculturalandroid.models.clases.Eventos;
import com.example.cercleculturalandroid.models.clases.Mensajes;
import com.example.cercleculturalandroid.models.clases.Usuari;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/Mensajes")
    Call<List<Mensajes>> getMensajes();

    @GET("api/Esdeveniments")
    Call<List<Eventos>> getEsdeveniments();

    @GET("api/Espais")
    Call<List<Espai>> getEspais();

    @GET("api/Usuaris")
    Call<List<Usuari>> getUsuaris();

    @GET("api/Mensajes/{id}")
    Call<Mensajes> getMensaje(@Path("id") int id);

    @POST("api/Mensajes")
    Call<Mensajes> postMensaje(@Body Mensajes mensaje);
}
