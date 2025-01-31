package com.example.frontweatherapp.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.models.models.RegisterRequest;
import com.example.frontweatherapp.api.service.AuthApiService;
import com.example.frontweatherapp.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private AuthApiService authApiService;
    private TextInputEditText emailEditText, passwordEditText;
    private TextInputLayout emailLayout, passwordLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Configurar Retrofit
        authApiService = RetrofitClient.getInstance(requireContext()).create(AuthApiService.class);

        // Configurar vistas
        emailEditText = view.findViewById(R.id.editTextEmail);
        passwordEditText = view.findViewById(R.id.editTextPassword);
        emailLayout = view.findViewById(R.id.textInputEmail);
        passwordLayout = view.findViewById(R.id.textInputPassword);
        MaterialButton registerButton = view.findViewById(R.id.button_register);
        MaterialButton backToLoginButton = view.findViewById(R.id.button_backToLogin);

        // Lógica para el botón de registro
        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!validateInputs(email, password)) {
                return;
            }

            performRegister(email, password);
        });

        // Lógica para el botón de volver al login
        backToLoginButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    private boolean validateInputs(String email, String password) {
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

        return isValid;
    }

    private void performRegister(String email, String password) {
        RegisterRequest request = new RegisterRequest(email, password);

        authApiService.registerUser(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
                    navigateToHome();
                } else {
                    Toast.makeText(getContext(), "Error en el registro", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHome() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }
}
