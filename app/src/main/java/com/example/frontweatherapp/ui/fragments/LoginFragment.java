package com.example.frontweatherapp.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.models.models.LoginRequest;
import com.example.frontweatherapp.models.models.LoginResponse;
import com.example.frontweatherapp.api.service.AuthApiService;
import com.example.frontweatherapp.network.RetrofitClient;
import com.example.frontweatherapp.ui.mainmenu.MainMenuActivity;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private AuthApiService authApiService;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, container, false);

        // Configurar Retrofit
        authApiService = RetrofitClient.getInstance(requireContext()).create(AuthApiService.class);

        // Configurar vistas
        EditText emailEditText = view.findViewById(R.id.editTextEmail);
        EditText passwordEditText = view.findViewById(R.id.editTextPassword);
        Button loginButton = view.findViewById(R.id.button_login);
        Button registerButton = view.findViewById(R.id.button_register);

        // Configurar acción para Login
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            performLogin(email, password);
        });

        // Configurar acción para ir al RegisterFragment
        registerButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new RegisterFragment()) // Cargar RegisterFragment
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void performLogin(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);

        authApiService.loginUser(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String authToken = response.body().getToken();

                    // Guardar el token de autenticación en SharedPreferences
                    SharedPreferences preferences = requireContext().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("TOKEN", authToken.trim());
                    editor.putBoolean("LOGGED_IN", true); // Marcar al usuario como autenticado
                    editor.apply();

                    Log.d(TAG, "Login exitoso, token de autenticación guardado.");

                    // Solicitar el token de FCM
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    String fcmToken = task.getResult();
                                    Log.d(TAG, "Token FCM obtenido: " + fcmToken);

                                    // Enviar el token FCM al backend
                                    sendFcmTokenToBackend(authToken, fcmToken);

                                    // Solicitar permisos para notificaciones
                                    requestNotificationPermission();
                                } else {
                                    Log.e(TAG, "Error al obtener el token FCM", task.getException());
                                }
                            });

                    // Navegar a la pantalla principal
                    Intent intent = new Intent(getContext(), MainMenuActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                } else {
                    Toast.makeText(getContext(), "Error en el login", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error login: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    private void sendFcmTokenToBackend(String authToken, String fcmToken) {
        // Crear el cuerpo de la solicitud
        Map<String, String> body = new HashMap<>();
        body.put("fcmToken", fcmToken);

        // Llamar al servicio
        authApiService.updateFcmToken(authToken, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Token FCM enviado al backend exitosamente.");
                } else {
                    Log.e(TAG, "Error al enviar el token FCM: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error de red al enviar el token FCM: " + t.getMessage());
            }
        });
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permiso de notificaciones concedido.");
            } else {
                Log.e(TAG, "Permiso de notificaciones denegado.");
                Toast.makeText(getContext(), "Las notificaciones requieren permiso para funcionar.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
