package com.example.frontweatherapp.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final Context context;
    private static final String TAG = "AuthInterceptor";

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
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

        // Configurar encabezado Accept dinámicamente según la solicitud
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
    }
}
