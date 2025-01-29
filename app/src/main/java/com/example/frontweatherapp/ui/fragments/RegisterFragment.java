package com.example.frontweatherapp.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.api.service.AuthApiService;
import com.example.frontweatherapp.network.RetrofitClient;
import com.example.frontweatherapp.ui.mainmenu.MainMenuActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";
    private FirebaseAuth firebaseAuth;
    private AuthApiService authApiService;

    private EditText emailEditText, passwordEditText;
    private Switch temperatureSwitch, windSwitch, humiditySwitch;
    private Button registerButton, backToLoginButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        authApiService = RetrofitClient.getInstance(requireContext()).create(AuthApiService.class);

        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        temperatureSwitch = view.findViewById(R.id.switchTemperature);
        windSwitch = view.findViewById(R.id.switchWind);
        humiditySwitch = view.findViewById(R.id.switchHumidity);
        registerButton = view.findViewById(R.id.registerButton);
        backToLoginButton = view.findViewById(R.id.backToLoginButton);

        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                showToast("Por favor, completa todos los campos");
                return;
            }

            performRegister(email, password);
        });

        backToLoginButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void performRegister(String email, String password) {
        Log.d(TAG, "Iniciando registro de usuario con email: " + email);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            Log.d(TAG, "Usuario registrado con UID: " + userId);
                            // Primero obtenemos el token FCM más reciente
                            getFcmTokenAndSavePreferences(userId, email);
                        }
                    } else {
                        Log.e(TAG, "Error en el registro", task.getException());
                        Toast.makeText(getContext(), "Error al registrarse", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getFcmTokenAndSavePreferences(String userId, String email) {
        // Obtener el token más reciente
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        Log.d(TAG, "Token FCM obtenido: " + token);
                        // Guardar las preferencias en el backend usando el token
                        savePreferences(userId, email, token);
                    } else {
                        Log.e(TAG, "Error al obtener el token FCM");
                        showToast("Error al obtener el token FCM");
                    }
                });
    }

    private void savePreferences(String userId, String email, String token) {
        if (TextUtils.isEmpty(token)) {
            Log.e(TAG, "No se encontró token de autenticación");
            showToast("Error: no hay token disponible.");
            return;
        }

        Log.d(TAG, "Token de autenticación obtenido: " + token);

        // Validar las preferencias antes de enviarlas
        Map<String, Boolean> preferences = new HashMap<>();
        preferences.put("temperature", temperatureSwitch.isChecked());
        preferences.put("wind", windSwitch.isChecked());
        preferences.put("humidity", humiditySwitch.isChecked());

        Map<String, Object> request = new HashMap<>();
        request.put("userId", userId);  // Asegúrate de enviar el userId, no el token
        request.put("email", email);
        request.put("preferences", preferences);

        Log.d(TAG, "Enviando preferencias al backend: " + request);

        // Realizar la solicitud para guardar las preferencias
        authApiService.createPreferences("Bearer " + token, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Preferencias guardadas exitosamente");
                    saveUserPreferencesLocally(userId, email);
                } else {
                    Log.e(TAG, "Error al guardar preferencias: " + response.code() + ", mensaje: " + response.message());
                    showToast("Error al guardar preferencias: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error de red al guardar preferencias: " + t.getMessage());
                showToast("Error de red: " + t.getMessage());
            }
        });
    }

    private void saveUserPreferencesLocally(String userId, String email) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_ID", userId);
        editor.putString("EMAIL", email);
        editor.putBoolean("temperature", temperatureSwitch.isChecked());
        editor.putBoolean("wind", windSwitch.isChecked());
        editor.putBoolean("humidity", humiditySwitch.isChecked());
        editor.putBoolean("LOGGED_IN", true);
        editor.apply();

        Intent intent = new Intent(getContext(), MainMenuActivity.class);
        showToast("Usuario registrado con éxito.");
        startActivity(intent);
        requireActivity().finish();
    }

    private void showToast(String message) {
        if (getActivity() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
