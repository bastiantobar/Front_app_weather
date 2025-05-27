package com.example.frontweatherapp.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable; // Añadido para permitir pasar objetos entre Fragments

public class Feature implements Serializable {
    private static final long serialVersionUID = 1L; // Recomendado para Serializable

    @SerializedName("geometry")
    private Geometry geometry;
    @SerializedName("properties")
    private Properties properties;
    @SerializedName("type")
    private String type;

    // Getters
    public Geometry getGeometry() { return geometry; }
    public Properties getProperties() { return properties; }
    public String getType() { return type; }

    // Setters
    public void setGeometry(Geometry geometry) { this.geometry = geometry; }
    public void setProperties(Properties properties) { this.properties = properties; }
    public void setType(String type) { this.type = type; }

    @Override
    public String toString() {
        return "Feature{" +
                "geometry=" + geometry +
                ", properties=" + properties +
                ", type='" + type + '\'' +
                '}';
    }
}
