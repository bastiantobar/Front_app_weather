package com.example.frontweatherapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class AirQualitySnapshot {
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("components")
    private Map<String, Double> components;
    @SerializedName("timestamp")
    private long timestamp;
    @SerializedName("aqiCategory")
    private String aqiCategory;
    @SerializedName("aqi")
    private int aqi;

    // Getters y Setters
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Map<String, Double> getComponents() {
        return components;
    }

    public void setComponents(Map<String, Double> components) {
        this.components = components;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAqiCategory() {
        return aqiCategory;
    }

    public void setAqiCategory(String aqiCategory) {
        this.aqiCategory = aqiCategory;
    }

    public int getAqi() {
        return aqi;
    }

    public void setAqi(int aqi) {
        this.aqi = aqi;
    }
}