package com.example.frontweatherapp.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AirQuality implements Serializable {
    @SerializedName("aqi")
    private int aqi;
    @SerializedName("components")
    private Components components;
    @SerializedName("timestamp") // ¡NUEVO CAMPO PARA LA FECHA/HORA!
    private long timestamp; // Usaremos long para almacenar segundos desde Epoch

    // Constructor vacío (necesario para Gson)
    public AirQuality() {}

    // Getters
    public int getAqi() {
        return aqi;
    }

    public Components getComponents() {
        return components;
    }

    public long getTimestamp() { // ¡NUEVO MÉTODO GETTER!
        return timestamp;
    }

    // Setters (opcional)
    public void setAqi(int aqi) {
        this.aqi = aqi;
    }

    public void setComponents(Components components) {
        this.components = components;
    }

    public void setTimestamp(long timestamp) { // ¡NUEVO MÉTODO SETTER!
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "AirQuality{" +
                "aqi=" + aqi +
                ", components=" + components +
                ", timestamp=" + timestamp +
                '}';
    }
}
