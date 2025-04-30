package com.example.cercleculturalandroid.api;

import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.0.248/CCAPI/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(
                            GsonConverterFactory.create(
                                    new GsonBuilder()
                                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                            .create()
                            )
                    )
                    .build();
        }
        return retrofit;
    }
}