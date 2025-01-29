package com.example.frontweatherapp.api.models;

public class LoginResponse {
    private String token;

    public LoginResponse() {}

    // Getter y Setter para el token
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
