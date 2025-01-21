package com.example.frontweatherapp.models;

public class WeatherData {
    private String time;
    private double airTemperature;
    private double windSpeed;
    private double precipitationAmount;

    public WeatherData(String time, double airTemperature, double windSpeed, double precipitationAmount) {
        this.time = time;
        this.airTemperature = airTemperature;
        this.windSpeed = windSpeed;
        this.precipitationAmount = precipitationAmount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getAirTemperature() {
        return airTemperature;
    }

    public void setAirTemperature(double airTemperature) {
        this.airTemperature = airTemperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getPrecipitationAmount() {
        return precipitationAmount;
    }

    public void setPrecipitationAmount(double precipitationAmount) {
        this.precipitationAmount = precipitationAmount;
    }
}
