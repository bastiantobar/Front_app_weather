package com.example.frontweatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.frontweatherapp.api.AuthApiService;
import com.example.frontweatherapp.api.AuthApiService.LoginRequest;
import com.example.frontweatherapp.api.AuthApiService.LoginResponse;
import com.example.frontweatherapp.api.AuthApiService.RegisterRequest;
import com.example.frontweatherapp.network.RetrofitClient;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.frontweatherapp.databinding.ActivityMainBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private AuthApiService authApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar Retrofit y AuthApiService
        authApiService = RetrofitClient.getInstance().create(AuthApiService.class);

        // Inicializar elementos del formulario
        EditText emailEditText = findViewById(R.id.editTextEmail);
        EditText passwordEditText = findViewById(R.id.editTextPassword);
        Button registerButton = findViewById(R.id.button_register);
        Button loginButton = findViewById(R.id.button_login);

        // Configurar acciones para los botones
        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            performRegister(email, password);
        });

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            performLogin(email, password);
        });
    }



    // Método para manejar el login
    private void performLogin(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);

        authApiService.loginUser(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    RetrofitClient.setToken(token); // Guardar el token para futuras solicitudes
                    Toast.makeText(MainActivity.this, "Login exitoso", Toast.LENGTH_SHORT).show();

                    // Redirigir a LoggedInActivity
                    Intent intent = new Intent(MainActivity.this, LoggedInActivity.class);
                    startActivity(intent);
                    finish(); // Opcional: cerrar MainActivity para evitar que el usuario regrese
                } else {
                    Toast.makeText(MainActivity.this, "Error en el login: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("MainActivity", "Error en la solicitud: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }



    // Método para manejar el registro
    private void performRegister(String email, String password) {
        RegisterRequest request = new RegisterRequest(email, password);
        authApiService.registerUser(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error en el registro", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MainActivity", "Error: " + t.getMessage());
                Snackbar.make(binding.getRoot(), "Error de red: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
/*
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }*/
}
