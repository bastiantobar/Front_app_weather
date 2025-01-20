package com.example.frontweatherapp.api.service;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import com.example.frontweatherapp.models.InstantWeather;


public interface WeatherApiService {

    @GET("/weather/grafic") // Asegúrate de que esta URL coincida con tu backend
    Call<ResponseBody> getMeteogram(@Header("Accept") String accept);

    @GET("/weather/instant/last")
    Call<InstantWeather> getLastInstantWeather(
            @Header("Authorization") String authorization,
            @Header("Accept") String accept
    );



}
