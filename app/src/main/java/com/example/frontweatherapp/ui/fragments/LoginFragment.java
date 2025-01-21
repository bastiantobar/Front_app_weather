package com.example.frontweatherapp.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.fragment.app.Fragment;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.api.models.LoginRequest;
import com.example.frontweatherapp.api.models.LoginResponse;
import com.example.frontweatherapp.api.service.AuthApiService;

import com.example.frontweatherapp.network.RetrofitClient;
import com.example.frontweatherapp.ui.mainmenu.MainMenuActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private AuthApiService authApiService;

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
                    String token = response.body().getToken();

                    SharedPreferences preferences = requireContext().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    editor.putString("TOKEN", token.trim());
                    editor.apply();
                    Log.d("RetrofitClient", "Token utilizado: " + token);
                    Toast.makeText(getContext(), "Login exitoso", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getContext(), MainMenuActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                } else {
                    Toast.makeText(getContext(), "Error en el login", Toast.LENGTH_SHORT).show();
                    Log.e("LoginFragment", "Error login: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                Log.e("LoginFragment", "Error: " + t.getMessage());
            }
        });
    }
}
