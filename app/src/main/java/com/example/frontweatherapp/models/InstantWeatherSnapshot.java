package com.example.frontweatherapp.models;

import com.google.gson.annotations.SerializedName;

public class InstantWeatherSnapshot {
    @SerializedName("airTemperature")
    private double airTemperature;
    @SerializedName("relativeHumidity")
    private double relativeHumidity;
    @SerializedName("airPressureAtSeaLevel")
    private double airPressureAtSeaLevel;
    @SerializedName("windSpeed")
    private double windSpeed;
    @SerializedName("cloudAreaFraction")
    private double cloudAreaFraction;

    // Getters y Setters
    public double getAirTemperature() {
        return airTemperature;
    }

    public void setAirTemperature(double airTemperature) {
        this.airTemperature = airTemperature;
    }

    public double getRelativeHumidity() {
        return relativeHumidity;
    }

    public void setRelativeHumidity(double relativeHumidity) {
        this.relativeHumidity = relativeHumidity;
    }

    public double getAirPressureAtSeaLevel() {
        return airPressureAtSeaLevel;
    }

    public void setAirPressureAtSeaLevel(double airPressureAtSeaLevel) {
        this.airPressureAtSeaLevel = airPressureAtSeaLevel;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getCloudAreaFraction() {
        return cloudAreaFraction;
    }

    public void setCloudAreaFraction(double cloudAreaFraction) {
        this.cloudAreaFraction = cloudAreaFraction;
    }
}