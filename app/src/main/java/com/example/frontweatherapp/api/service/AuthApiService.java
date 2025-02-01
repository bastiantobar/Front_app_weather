package com.example.frontweatherapp.api.service;



import com.example.frontweatherapp.models.LoginRequest;
import com.example.frontweatherapp.models.LoginResponse;
import com.example.frontweatherapp.models.RegisterRequest;

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

}
