package com.example.frontweatherapp.ui.fragments;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.models.LoginRequest;
import com.example.frontweatherapp.models.LoginResponse;
import com.example.frontweatherapp.models.RegisterRequest;
import com.example.frontweatherapp.api.service.AuthApiService;
import com.example.frontweatherapp.network.RetrofitClient;
import com.example.frontweatherapp.ui.main.MenuActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private AuthApiService authApiService;
    private TextInputEditText emailEditText, passwordEditText, confirmPasswordEditText;
    private TextInputLayout emailLayout, passwordLayout, confirmPasswordLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        authApiService = RetrofitClient.getInstance(requireContext()).create(AuthApiService.class);

        emailEditText = view.findViewById(R.id.editTextEmail);
        passwordEditText = view.findViewById(R.id.editTextPassword);
        confirmPasswordEditText = view.findViewById(R.id.editTextConfirmPassword);

        emailLayout = view.findViewById(R.id.textInputEmail);
        passwordLayout = view.findViewById(R.id.textInputPassword);
        confirmPasswordLayout = view.findViewById(R.id.textInputConfirmPassword);

        MaterialButton registerButton = view.findViewById(R.id.button_register);
        MaterialButton backToLoginButton = view.findViewById(R.id.button_backToLogin);

        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (!validateInputs(email, password, confirmPassword)) {
                return;
            }

            performRegister(email, password);
        });

        backToLoginButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private boolean validateInputs(String email, String password, String confirmPassword) {
        boolean isValid = true;

        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("El email es obligatorio");
            isValid = false;
        } else {
            emailLayout.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("La contraseña es obligatoria");
            isValid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError("Debe tener al menos 6 caracteres");
            isValid = false;
        } else {
            passwordLayout.setError(null);
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordLayout.setError("Debe confirmar la contraseña");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Las contraseñas no coinciden");
            isValid = false;
        } else {
            confirmPasswordLayout.setError(null);
        }

        return isValid;
    }



    private void navigateToHome() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }


    private void performRegister(String email, String password) {
        RegisterRequest request = new RegisterRequest(email, password);

        authApiService.registerUser(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Registro exitoso. Procediendo a iniciar sesión automáticamente.");
                    loginAfterRegister(email, password); // Iniciar sesión después del registro
                } else {
                    Log.e(TAG, "Error en el registro. Código: " + response.code());
                    Toast.makeText(getContext(), "Error en el registro", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error de red al registrar", t);
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para iniciar sesión automáticamente después del registro
    private void loginAfterRegister(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);

        authApiService.loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    Log.d(TAG, "Inicio de sesión automático exitoso. Token recibido: " + token);

                    // Guardar el token en SharedPreferences
                    saveAuthToken(token);

                    // Redirigir al home después del login exitoso
                    navigateToMenuActivity();
                } else {
                    Log.e(TAG, "Error al iniciar sesión tras el registro. Código: " + response.code());
                    Toast.makeText(getContext(), "Registro exitoso, pero error al iniciar sesión.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Error de red al iniciar sesión después del registro", t);
                Toast.makeText(getContext(), "Registro exitoso, pero error de red al iniciar sesión.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Guardar el token en SharedPreferences
    private void saveAuthToken(String token) {
        requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                .edit()
                .putString("auth_token", token)
                .apply();
    }





    // Método para abrir MenuActivity
    private void navigateToMenuActivity() {
        Intent intent = new Intent(requireContext(), MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
