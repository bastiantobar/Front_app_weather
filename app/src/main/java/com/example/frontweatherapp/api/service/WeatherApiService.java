package com.example.frontweatherapp.api.service;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query; // Importar Query

import com.example.frontweatherapp.models.HistoricalWeatherEntry;
import com.example.frontweatherapp.models.InstantWeather; // Mantener si aún se usa en otras partes
import com.example.frontweatherapp.models.WeatherData; // Mantener si aún se usa en otras partes
import com.example.frontweatherapp.models.WeatherResponse;
import com.example.frontweatherapp.models.LocationCoordinates;// ¡IMPORTANTE! Importar la nueva clase de modelo

public interface WeatherApiService {

    @GET("weather/hourly")
    Call<List<WeatherData>> getHourlyForecasts(@Header("Authorization") String token);

    @GET("/weather/full-report")
    Call<WeatherResponse> getWeatherData(
            @Header("Authorization") String authorization,
            @Header("Accept") String accept,
            @Query("addressQuery") String addressQuery // ¡Parámetro de consulta añadido aquí!
    );

    @GET("weather/historical")
    Call<List<HistoricalWeatherEntry>> getHistoricalWeather(
            @Header("Authorization") String authToken,
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("limit") int limit
    );

    @GET("weather/location") // Endpoint para tu servicio de geocodificación en el backend
    Call<LocationCoordinates> getCoordinatesForLocation(
            @Header("Authorization") String authToken,
            @Query("addressQuery") String address
    );
}
