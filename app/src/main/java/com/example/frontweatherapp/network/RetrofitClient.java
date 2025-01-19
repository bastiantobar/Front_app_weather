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

            // Interceptor para agregar el token de autorización y el encabezado "Accept"
            Interceptor authInterceptor = chain -> {
                Request original = chain.request();

                // Excluir el token en solicitudes al endpoint de login
                if (original.url().encodedPath().equals("/auth/login")) {
                    return chain.proceed(original);
                }

                // Obtener el token de SharedPreferences
                SharedPreferences preferences = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
                String token = preferences.getString("TOKEN", null);

                if (token != null) {
                    // Validar si el token ya incluye el prefijo "Bearer"
                    String formattedToken = token.startsWith("Bearer ") ? token : "Bearer " + token.trim();
                    Log.d(TAG, "Enviando token: " + formattedToken);

                    // Modificar la solicitud para incluir encabezados
                    Request modified = original.newBuilder()
                            .header("Authorization", formattedToken)
                            .header("Accept", "image/svg+xml") // Encabezado específico para el gráfico
                            .build();
                    return chain.proceed(modified);
                } else {
                    Log.e(TAG, "Token no encontrado. Se envía sin autorización.");
                }

                return chain.proceed(original);
            };

            // Crear el cliente HTTP con los interceptores
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor) // Interceptor para logging
                    .addInterceptor(authInterceptor) // Interceptor para autorización
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
