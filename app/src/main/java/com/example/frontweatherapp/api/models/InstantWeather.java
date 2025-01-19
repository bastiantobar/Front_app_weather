package com.example.frontweatherapp.models;

public class InstantWeather {
    private double airTemperature;
    private double relativeHumidity;
    private double airPressureAtSeaLevel;
    private double windSpeed;
    private double cloudAreaFraction;

    // Getters y setters
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
