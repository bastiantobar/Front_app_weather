package com.example.frontweatherapp.network;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;

    public static Retrofit getInstance(Context context) {
        if (retrofit == null) {
            // Interceptor de logging para ver las solicitudes y respuestas HTTP
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Cliente HTTP con los interceptores
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor) // Interceptor de logs
                    .addInterceptor(new AuthInterceptor(context)) // Interceptor de autenticación
                    .build();

            // Crear instancia de Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080/") // Cambia si usas otra IP
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create()) // Convertidor JSON
                    .build();
        }
        return retrofit;
    }
}
