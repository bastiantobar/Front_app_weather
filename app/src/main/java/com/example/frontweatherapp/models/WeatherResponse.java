package com.example.frontweatherapp.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable; // Importar Serializable
import java.util.List;

public class WeatherResponse implements Serializable { // Implementar Serializable
    @SerializedName("location")
    private Location location;
    @SerializedName("currentWeather")
    private CurrentWeather currentWeather;
    @SerializedName("hourlyForecasts")
    private List<HourlyForecast> hourlyForecasts;
    @SerializedName("airQuality")
    private AirQuality airQuality;
    @SerializedName("windMap")
    private WindMap windMap;
    @SerializedName("astronomicalTimes")
    private AstronomicalTimes astronomicalTimes;
    @SerializedName("nasaApod")
    private NasaApod nasaApod;

    // Getters
    public Location getLocation() { return location; }
    public CurrentWeather getCurrentWeather() { return currentWeather; }
    public List<HourlyForecast> getHourlyForecasts() { return hourlyForecasts; }
    public AirQuality getAirQuality() { return airQuality; }
    public WindMap getWindMap() { return windMap; }
    public AstronomicalTimes getAstronomicalTimes() { return astronomicalTimes; }
    public NasaApod getNasaApod() { return nasaApod; }

    // Setters (opcional, si necesitas modificar los objetos después de la deserialización)
    public void setLocation(Location location) { this.location = location; }
    public void setCurrentWeather(CurrentWeather currentWeather) { this.currentWeather = currentWeather; }
    public void setHourlyForecasts(List<HourlyForecast> hourlyForecasts) { this.hourlyForecasts = hourlyForecasts; }
    public void setAirQuality(AirQuality airQuality) { this.airQuality = airQuality; }
    public void setWindMap(WindMap windMap) { this.windMap = windMap; }
    public void setAstronomicalTimes(AstronomicalTimes astronomicalTimes) { this.astronomicalTimes = astronomicalTimes; }
    public void setNasaApod(NasaApod nasaApod) { this.nasaApod = nasaApod; }

    @Override
    public String toString() {
        return "WeatherResponse{" +
                "location=" + location +
                ", currentWeather=" + currentWeather +
                ", hourlyForecasts=" + hourlyForecasts +
                ", airQuality=" + airQuality +
                ", windMap=" + windMap +
                ", astronomicalTimes=" + astronomicalTimes +
                ", nasaApod=" + nasaApod +
                '}';
    }
}