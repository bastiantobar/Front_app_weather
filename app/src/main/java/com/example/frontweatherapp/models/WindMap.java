package com.example.frontweatherapp.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable; // Añadido para permitir pasar objetos entre Fragments
import java.util.List;

public class WindMap implements Serializable {
    private static final long serialVersionUID = 1L; // Recomendado para Serializable

    @SerializedName("type")
    private String type;
    @SerializedName("features")
    private List<Feature> features;

    // Getters
    public String getType() { return type; }
    public List<Feature> getFeatures() { return features; }

    // Setters
    public void setType(String type) { this.type = type; }
    public void setFeatures(List<Feature> features) { this.features = features; }

    @Override
    public String toString() {
        return "WindMap{" +
                "type='" + type + '\'' +
                ", features=" + features +
                '}';
    }
}
