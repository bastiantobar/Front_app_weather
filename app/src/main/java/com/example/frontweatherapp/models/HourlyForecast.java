package com.example.frontweatherapp.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable; // Añadido para permitir pasar objetos entre Fragments

public class HourlyForecast implements Serializable {
    private static final long serialVersionUID = 1L; // Recomendado para Serializable

    @SerializedName("time")
    private String time;
    @SerializedName("airTemperature")
    private double airTemperature;
    @SerializedName("windSpeed")
    private double windSpeed;
    @SerializedName("precipitationAmount")
    private double precipitationAmount;

    // Getters
    public String getTime() { return time; }
    public double getAirTemperature() { return airTemperature; }
    public double getWindSpeed() { return windSpeed; }
    public double getPrecipitationAmount() { return precipitationAmount; }

    // Setters
    public void setTime(String time) { this.time = time; }
    public void setAirTemperature(double airTemperature) { this.airTemperature = airTemperature; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
    public void setPrecipitationAmount(double precipitationAmount) { this.precipitationAmount = precipitationAmount; }

    @Override
    public String toString() {
        return "HourlyForecast{" +
                "time='" + time + '\'' +
                ", airTemperature=" + airTemperature +
                ", windSpeed=" + windSpeed +
                ", precipitationAmount=" + precipitationAmount +
                '}';
    }
}