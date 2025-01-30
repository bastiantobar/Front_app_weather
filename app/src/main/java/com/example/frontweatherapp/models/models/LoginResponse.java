package com.example.frontweatherapp.models.models;

public class LoginResponse {
    private String token;

    // Constructor vacío (opcional, pero recomendado para Gson)
    public LoginResponse() {}

    // Getter y Setter para el token
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
