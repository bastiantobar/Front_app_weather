package com.example.frontweatherapp.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable; // Añadido para permitir pasar objetos entre Fragments

public class CurrentWeather implements Serializable {
    private static final long serialVersionUID = 1L; // Recomendado para Serializable

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
    // No hay 'symbolCode' directo en tu JSON, pero lo incluimos por si lo necesitas más adelante
    // @SerializedName("symbolCode")
    // private String symbolCode;

    // Getters
    public double getAirTemperature() { return airTemperature; }
    public double getRelativeHumidity() { return relativeHumidity; }
    public double getAirPressureAtSeaLevel() { return airPressureAtSeaLevel; }
    public double getWindSpeed() { return windSpeed; }
    public double getCloudAreaFraction() { return cloudAreaFraction; }
    // public String getSymbolCode() { return symbolCode; } // Descomentar si añades el campo

    // Setters
    public void setAirTemperature(double airTemperature) { this.airTemperature = airTemperature; }
    public void setRelativeHumidity(double relativeHumidity) { this.relativeHumidity = relativeHumidity; }
    public void setAirPressureAtSeaLevel(double airPressureAtSeaLevel) { this.airPressureAtSeaLevel = airPressureAtSeaLevel; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
    public void setCloudAreaFraction(double cloudAreaFraction) { this.cloudAreaFraction = cloudAreaFraction; }
    // public void setSymbolCode(String symbolCode) { this.symbolCode = symbolCode; } // Descomentar si añades el campo

    @Override
    public String toString() {
        return "CurrentWeather{" +
                "airTemperature=" + airTemperature +
                ", relativeHumidity=" + relativeHumidity +
                ", airPressureAtSeaLevel=" + airPressureAtSeaLevel +
                ", windSpeed=" + windSpeed +
                ", cloudAreaFraction=" + cloudAreaFraction +
                // (symbolCode != null ? ", symbolCode='" + symbolCode + '\'' : "") +
                '}';
    }
}