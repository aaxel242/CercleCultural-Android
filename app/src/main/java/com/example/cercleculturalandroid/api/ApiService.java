package com.example.cercleculturalandroid.api;

import com.example.cercleculturalandroid.models.clases.Espai;
import com.example.cercleculturalandroid.models.clases.Eventos;
import com.example.cercleculturalandroid.models.clases.Mensajes;
import com.example.cercleculturalandroid.models.clases.Reserva;
import com.example.cercleculturalandroid.models.clases.Usuari;

import java.util.List;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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

    @GET("api/Usuaris/{id}")
    Call<Usuari> getUsuari(@Path("id") int id);

    @GET("api/Mensajes")
    Call<List<Mensajes>> getMensaje();

    @POST("api/Mensajes")
    Call<Mensajes> postMensaje(@Body Mensajes mensaje);

    @GET("api/Reservas/ReservasPerfil/{userId}")
    Call<List<Reserva>> getReservasPerfil(@Path("userId") int userId);

    @Multipart
    @POST("api/Usuaris/UploadImage/{userId}")
    Call<Usuari> uploadProfileImage(
            @Path("userId") int userId,
            @Part MultipartBody.Part file
    );

    @POST("api/Reservas")
    Call<Reserva> postReserva(@Body Reserva reserva);

}