package com.example.frontweatherapp.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable; // Añadido para permitir pasar objetos entre Fragments
import java.util.List;

public class Geometry implements Serializable {
    private static final long serialVersionUID = 1L; // Recomendado para Serializable

    @SerializedName("type")
    private String type;
    @SerializedName("coordinates")
    private List<Double> coordinates;

    // Getters
    public String getType() { return type; }
    public List<Double> getCoordinates() { return coordinates; }

    // Setters
    public void setType(String type) { this.type = type; }
    public void setCoordinates(List<Double> coordinates) { this.coordinates = coordinates; }

    @Override
    public String toString() {
        return "Geometry{" +
                "type='" + type + '\'' +
                ", coordinates=" + coordinates +
                '}';
    }
}