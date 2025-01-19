package com.example.frontweatherapp.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.api.service.AuthApiService;
import com.example.frontweatherapp.api.service.AuthApiService.LoginRequest;
import com.example.frontweatherapp.api.service.AuthApiService.LoginResponse;
import com.example.frontweatherapp.api.service.AuthApiService.RegisterRequest;
import com.example.frontweatherapp.network.RetrofitClient;
import com.example.frontweatherapp.ui.mainmenu.MainMenuActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AuthApiService authApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configuración de Retrofit
        authApiService = RetrofitClient.getInstance(this).create(AuthApiService.class);


        // Configurar elementos de la interfaz
        EditText emailEditText = findViewById(R.id.editTextEmail);
        EditText passwordEditText = findViewById(R.id.editTextPassword);
        Button loginButton = findViewById(R.id.button_login);
        Button registerButton = findViewById(R.id.button_register);

        // Configurar acciones para Login
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            performLogin(email, password);
        });

        // Configurar acciones para Registro
        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            performRegister(email, password);
        });
    }

    private void performLogin(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);

        authApiService.loginUser(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();

                    SharedPreferences preferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    editor.putString("TOKEN", token.trim());
                    editor.apply();
                    Log.d("RetrofitClient", "Token utilizado: " + token);
                    Toast.makeText(MainActivity.this, "Login exitoso", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Error en el login", Toast.LENGTH_SHORT).show();
                    Log.e("MainActivity", "Error login: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                Log.e("MainActivity", "Error: " + t.getMessage());
            }
        });
    }


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
                Toast.makeText(MainActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                Log.e("MainActivity", "Error: " + t.getMessage());
            }
        });
    }
}
