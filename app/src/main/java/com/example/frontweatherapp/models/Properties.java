package com.example.frontweatherapp.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable; // Añadido para permitir pasar objetos entre Fragments

public class Properties implements Serializable {
    private static final long serialVersionUID = 1L; // Recomendado para Serializable

    @SerializedName("windSpeed")
    private double windSpeed;
    @SerializedName("windDirection")
    private double windDirection;
    @SerializedName("time")
    private String time;

    // Getters
    public double getWindSpeed() { return windSpeed; }
    public double getWindDirection() { return windDirection; }
    public String getTime() { return time; }

    // Setters
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
    public void setWindDirection(double windDirection) { this.windDirection = windDirection; }
    public void setTime(String time) { this.time = time; }

    @Override
    public String toString() {
        return "Properties{" +
                "windSpeed=" + windSpeed +
                ", windDirection=" + windDirection +
                ", time='" + time + '\'' +
                '}';
    }
}