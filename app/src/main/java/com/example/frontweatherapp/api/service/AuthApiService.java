package com.example.frontweatherapp.api.service;



import com.example.frontweatherapp.api.models.LoginRequest;
import com.example.frontweatherapp.api.models.LoginResponse;
import com.example.frontweatherapp.api.models.RegisterRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthApiService {
    @POST("auth/register")
    Call<Void> registerUser(@Body RegisterRequest request);

    @POST("auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest request);

    @POST("/auth/update-fcm-token")
    Call<Void> updateFcmToken(
            @Header("Authorization") String authToken,
            @Body Map<String, String> body
    );
    @POST("/auth/create-preferences")
    Call<Void> createPreferences(
            @Header("Authorization") String authToken,
            @Body Map<String, Object> preferences
    );
}
