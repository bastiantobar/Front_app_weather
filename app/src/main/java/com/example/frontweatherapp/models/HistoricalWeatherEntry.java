package com.example.frontweatherapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HistoricalWeatherEntry {
    @SerializedName("id")
    private String id;
    @SerializedName("location")
    private Location location;
    @SerializedName("recordedAt")
    private String recordedAt; // Considera usar java.time.Instant y un adaptador Gson si necesitas manipularlo como objeto Date/Time
    @SerializedName("instantWeatherSnapshot")
    private InstantWeatherSnapshot instantWeatherSnapshot;
    @SerializedName("hourlyForecasts")
    private List<HourlyForecast> hourlyForecasts;
    @SerializedName("airQualitySnapshot")
    private AirQualitySnapshot airQualitySnapshot;

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(String recordedAt) {
        this.recordedAt = recordedAt;
    }

    public InstantWeatherSnapshot getInstantWeatherSnapshot() {
        return instantWeatherSnapshot;
    }

    public void setInstantWeatherSnapshot(InstantWeatherSnapshot instantWeatherSnapshot) {
        this.instantWeatherSnapshot = instantWeatherSnapshot;
    }

    public List<HourlyForecast> getHourlyForecasts() {
        return hourlyForecasts;
    }

    public void setHourlyForecasts(List<HourlyForecast> hourlyForecasts) {
        this.hourlyForecasts = hourlyForecasts;
    }

    public AirQualitySnapshot getAirQualitySnapshot() {
        return airQualitySnapshot;
    }

    public void setAirQualitySnapshot(AirQualitySnapshot airQualitySnapshot) {
        this.airQualitySnapshot = airQualitySnapshot;
    }
}