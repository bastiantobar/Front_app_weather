package com.example.frontweatherapp.models;

import com.google.gson.annotations.SerializedName;

public class CurrentWeather {
    @SerializedName("time")
    private String time;
    @SerializedName("airTemperature")
    private double airTemperature;
    @SerializedName("relativeHumidity")
    private double relativeHumidity;
    @SerializedName("airPressureAtSeaLevel")
    private double airPressureAtSeaLevel;
    @SerializedName("windSpeed")
    private double windSpeed;
    @SerializedName("windDirection")
    private double windDirection;
    @SerializedName("cloudAreaFraction")
    private double cloudAreaFraction;
    @SerializedName("weatherCondition") // ¡NUEVO CAMPO!
    private String weatherCondition;

    // Getters
    public String getTime() {
        return time;
    }

    public double getAirTemperature() {
        return airTemperature;
    }

    public double getRelativeHumidity() {
        return relativeHumidity;
    }

    public double getAirPressureAtSeaLevel() {
        return airPressureAtSeaLevel;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getWindDirection() {
        return windDirection;
    }

    public double getCloudAreaFraction() {
        return cloudAreaFraction;
    }

    public String getWeatherCondition() { // ¡NUEVO MÉTODO GETTER!
        return weatherCondition;
    }

    // Setters (opcional, si necesitas modificar los objetos después de la deserialización)
    public void setTime(String time) {
        this.time = time;
    }

    public void setAirTemperature(double airTemperature) {
        this.airTemperature = airTemperature;
    }

    public void setRelativeHumidity(double relativeHumidity) {
        this.relativeHumidity = relativeHumidity;
    }

    public void setAirPressureAtSeaLevel(double airPressureAtSeaLevel) {
        this.airPressureAtSeaLevel = airPressureAtSeaLevel;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setWindDirection(double windDirection) {
        this.windDirection = windDirection;
    }

    public void setCloudAreaFraction(double cloudAreaFraction) {
        this.cloudAreaFraction = cloudAreaFraction;
    }

    public void setWeatherCondition(String weatherCondition) { // ¡NUEVO MÉTODO SETTER!
        this.weatherCondition = weatherCondition;
    }

    @Override
    public String toString() {
        return "CurrentWeather{" +
                "time='" + time + '\'' +
                ", airTemperature=" + airTemperature +
                ", relativeHumidity=" + relativeHumidity +
                ", airPressureAtSeaLevel=" + airPressureAtSeaLevel +
                ", windSpeed=" + windSpeed +
                ", windDirection=" + windDirection +
                ", cloudAreaFraction=" + cloudAreaFraction +
                ", weatherCondition='" + weatherCondition + '\'' +
                '}';
    }
}
