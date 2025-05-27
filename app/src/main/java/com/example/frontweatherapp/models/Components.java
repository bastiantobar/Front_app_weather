package com.example.frontweatherapp.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable; // Añadido para permitir pasar objetos entre Fragments

public class Components implements Serializable {
    private static final long serialVersionUID = 1L; // Recomendado para Serializable

    @SerializedName("co")
    private double co;
    @SerializedName("no")
    private double no;
    @SerializedName("no2")
    private double no2;
    @SerializedName("o3")
    private double o3;
    @SerializedName("so2")
    private double so2;
    @SerializedName("pm2_5")
    private double pm2_5;
    @SerializedName("pm10")
    private double pm10;
    @SerializedName("nh3")
    private double nh3;

    // Getters
    public double getCo() { return co; }
    public double getNo() { return no; }
    public double getNo2() { return no2; }
    public double getO3() { return o3; }
    public double getSo2() { return so2; }
    public double getPm2_5() { return pm2_5; }
    public double getPm10() { return pm10; }
    public double getNh3() { return nh3; }

    // Setters
    public void setCo(double co) { this.co = co; }
    public void setNo(double no) { this.no = no; }
    public void setNo2(double no2) { this.no2 = no2; }
    public void setO3(double o3) { this.o3 = o3; }
    public void setSo2(double so2) { this.so2 = so2; }
    public void setPm2_5(double pm2_5) { this.pm2_5 = pm2_5; }
    public void setPm10(double pm10) { this.pm10 = pm10; }
    public void setNh3(double nh3) { this.nh3 = nh3; }

    @Override
    public String toString() {
        return "Components{" +
                "co=" + co +
                ", no=" + no +
                ", no2=" + no2 +
                ", o3=" + o3 +
                ", so2=" + so2 +
                ", pm2_5=" + pm2_5 +
                ", pm10=" + pm10 +
                ", nh3=" + nh3 +
                '}';
    }
}
