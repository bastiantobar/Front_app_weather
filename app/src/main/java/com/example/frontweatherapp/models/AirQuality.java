package com.example.frontweatherapp.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable; // Añadido para permitir pasar objetos entre Fragments

public class AirQuality implements Serializable {
    private static final long serialVersionUID = 1L; // Recomendado para Serializable

    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("components")
    private Components components;
    @SerializedName("timestamp")
    private long timestamp;
    @SerializedName("aqiCategory")
    private String aqiCategory;
    @SerializedName("aqi")
    private int aqi;

    // Getters
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public Components getComponents() { return components; }
    public long getTimestamp() { return timestamp; }
    public String getAqiCategory() { return aqiCategory; }
    public int getAqi() { return aqi; }

    // Setters
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setComponents(Components components) { this.components = components; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setAqiCategory(String aqiCategory) { this.aqiCategory = aqiCategory; }
    public void setAqi(int aqi) { this.aqi = aqi; }

    @Override
    public String toString() {
        return "AirQuality{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", components=" + components +
                ", timestamp=" + timestamp +
                ", aqiCategory='" + aqiCategory + '\'' +
                ", aqi=" + aqi +
                '}';
    }
}