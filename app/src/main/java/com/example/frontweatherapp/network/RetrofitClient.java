package com.example.frontweatherapp.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String TAG = "RetrofitClient";
    private static Retrofit retrofit;

    public static Retrofit getInstance(Context context) {
        if (retrofit == null) {
            // Interceptor para registrar solicitudes y respuestas HTTP
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Interceptor para agregar el token de autorización y configurar encabezados dinámicos
            Interceptor authInterceptor = chain -> {
                Request original = chain.request();

                // Excluir el token en solicitudes al endpoint de login
                if (original.url().encodedPath().equals("/auth/login")) {
                    Log.d(TAG, "Solicitud al endpoint de login. No se agrega token.");
                    return chain.proceed(original);
                }

                // Obtener el token de SharedPreferences
                SharedPreferences preferences = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
                String token = preferences.getString("TOKEN", null);

                Request.Builder builder = original.newBuilder();

                // Configurar encabezado Accept dinámicamente
                if (original.url().encodedPath().contains("/weather/instant/last")) {
                    builder.header("Accept", "application/json");
                } else if (original.url().encodedPath().contains("/weather/grafic")) {
                    builder.header("Accept", "image/svg+xml");
                }

                // Agregar el token de autorización si está disponible
                if (token != null) {
                    String formattedToken = token.startsWith("Bearer ") ? token : "Bearer " + token.trim();
                    Log.d(TAG, "Enviando token: " + formattedToken);
                    builder.header("Authorization", formattedToken);
                } else {
                    Log.e(TAG, "Token no encontrado. Se envía sin autorización.");
                }

                return chain.proceed(builder.build());
            };

            // Crear cliente HTTP con los interceptores
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor) // Interceptor para logging
                    .addInterceptor(authInterceptor) // Interceptor para autorización y encabezados dinámicos
                    .build();

            // Crear instancia de Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080/") // Cambia esto si usas una IP diferente
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create()) // Convertidor para JSON
                    .build();
        }
        return retrofit;
    }
}
