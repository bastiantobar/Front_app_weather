package com.example.frontweatherapp.api.service;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query; // Importar Query

import com.example.frontweatherapp.models.InstantWeather; // Mantener si aún se usa en otras partes
import com.example.frontweatherapp.models.WeatherData; // Mantener si aún se usa en otras partes
import com.example.frontweatherapp.models.WeatherResponse; // ¡IMPORTANTE! Importar la nueva clase de modelo

public interface WeatherApiService {

    @GET("/weather/grafic")
    Call<ResponseBody> getMeteogram(@Header("Accept") String accept);

    @GET("/weather/instant/last")
    Call<InstantWeather> getLastInstantWeather(
            @Header("Authorization") String authorization,
            @Header("Accept") String accept
    );

    @GET("weather/hourly")
    Call<List<WeatherData>> getHourlyForecasts(@Header("Authorization") String token);

    // ¡NUEVO MÉTODO! Para obtener la respuesta completa del clima desde /weather/full-report
    @GET("/weather/full-report") // ¡IMPORTANTE! Reemplaza con la URL de tu nuevo endpoint que devuelve el JSON completo
    Call<WeatherResponse> getWeatherData(
            @Header("Authorization") String authorization,
            @Header("Accept") String accept,
            @Query("addressQuery") String addressQuery // ¡Parámetro de consulta añadido aquí!
    );
}
