package com.example.cercleculturalandroid.api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static final String BASE_URL = "http://192.168.68.121/CCAPI/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(
                            GsonConverterFactory.create(
                                    new GsonBuilder()
                                            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                            .create()
                            )
                    )
                    .build();
        }
        return retrofit;
    }
}