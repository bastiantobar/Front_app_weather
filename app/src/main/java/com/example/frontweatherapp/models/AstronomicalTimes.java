package com.example.frontweatherapp.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable; // Añadido para permitir pasar objetos entre Fragments

public class AstronomicalTimes implements Serializable {
    private static final long serialVersionUID = 1L; // Recomendado para Serializable

    @SerializedName("sunrise")
    private String sunrise;
    @SerializedName("sunset")
    private String sunset;
    @SerializedName("solar_noon")
    private String solarNoon;
    @SerializedName("day_length")
    private String dayLength;
    @SerializedName("civil_twilight_begin")
    private String civilTwilightBegin;
    @SerializedName("civil_twilight_end")
    private String civilTwilightEnd;
    @SerializedName("nautical_twilight_begin")
    private String nauticalTwilightBegin;
    @SerializedName("nautical_twilight_end")
    private String nauticalTwilightEnd;
    @SerializedName("astronomical_twilight_begin")
    private String astronomicalTwilightBegin;
    @SerializedName("astronomical_twilight_end")
    private String astronomicalTwilightEnd;

    // Getters
    public String getSunrise() { return sunrise; }
    public String getSunset() { return sunset; }
    public String getSolarNoon() { return solarNoon; }
    public String getDayLength() { return dayLength; }
    public String getCivilTwilightBegin() { return civilTwilightBegin; }
    public String getCivilTwilightEnd() { return civilTwilightEnd; }
    public String getNauticalTwilightBegin() { return nauticalTwilightBegin; }
    public String getNauticalTwilightEnd() { return nauticalTwilightEnd; }
    public String getAstronomicalTwilightBegin() { return astronomicalTwilightBegin; }
    public String getAstronomicalTwilightEnd() { return astronomicalTwilightEnd; }

    // Setters
    public void setSunrise(String sunrise) { this.sunrise = sunrise; }
    public void setSunset(String sunset) { this.sunset = sunset; }
    public void setSolarNoon(String solarNoon) { this.solarNoon = solarNoon; }
    public void setDayLength(String dayLength) { this.dayLength = dayLength; }
    public void setCivilTwilightBegin(String civilTwilightBegin) { this.civilTwilightBegin = civilTwilightBegin; }
    public void setCivilTwilightEnd(String civilTwilightEnd) { this.civilTwilightEnd = civilTwilightEnd; }
    public void setNauticalTwilightBegin(String nauticalTwilightBegin) { this.nauticalTwilightBegin = nauticalTwilightBegin; }
    public void setNauticalTwilightEnd(String nauticalTwilightEnd) { this.nauticalTwilightEnd = nauticalTwilightEnd; }
    public void setAstronomicalTwilightBegin(String astronomicalTwilightBegin) { this.astronomicalTwilightBegin = astronomicalTwilightBegin; }
    public void setAstronomicalTwilightEnd(String astronomicalTwilightEnd) { this.astronomicalTwilightEnd = astronomicalTwilightEnd; }

    @Override
    public String toString() {
        return "AstronomicalTimes{" +
                "sunrise='" + sunrise + '\'' +
                ", sunset='" + sunset + '\'' +
                ", solarNoon='" + solarNoon + '\'' +
                ", dayLength='" + dayLength + '\'' +
                ", civilTwilightBegin='" + civilTwilightBegin + '\'' +
                ", civilTwilightEnd='" + civilTwilightEnd + '\'' +
                ", nauticalTwilightBegin='" + nauticalTwilightBegin + '\'' +
                ", nauticalTwilightEnd='" + nauticalTwilightEnd + '\'' +
                ", astronomicalTwilightBegin='" + astronomicalTwilightBegin + '\'' +
                ", astronomicalTwilightEnd='" + astronomicalTwilightEnd + '\'' +
                '}';
    }
}
