package com.example.frontweatherapp.helpers;

import com.example.frontweatherapp.models.models.WeatherData;

import java.util.List;

public class WeatherUtils {

    // Método para obtener el máximo y mínimo de la temperatura
    public static double getMaxTemperature(List<WeatherData> weatherDataList) {
        double maxTemp = Double.MIN_VALUE;
        for (WeatherData data : weatherDataList) {
            if (data.getAirTemperature() > maxTemp) {
                maxTemp = data.getAirTemperature();
            }
        }
        return maxTemp;
    }

    public static double getMinTemperature(List<WeatherData> weatherDataList) {
        double minTemp = Double.MAX_VALUE;
        for (WeatherData data : weatherDataList) {
            if (data.getAirTemperature() < minTemp) {
                minTemp = data.getAirTemperature();
            }
        }
        return minTemp;
    }

    // Método para obtener el máximo y mínimo de la velocidad del viento
    public static double getMaxWindSpeed(List<WeatherData> weatherDataList) {
        double maxWindSpeed = Double.MIN_VALUE;
        for (WeatherData data : weatherDataList) {
            if (data.getWindSpeed() > maxWindSpeed) {
                maxWindSpeed = data.getWindSpeed();
            }
        }
        return maxWindSpeed;
    }

    public static double getMinWindSpeed(List<WeatherData> weatherDataList) {
        double minWindSpeed = Double.MAX_VALUE;
        for (WeatherData data : weatherDataList) {
            if (data.getWindSpeed() < minWindSpeed) {
                minWindSpeed = data.getWindSpeed();
            }
        }
        return minWindSpeed;
    }

    // Método para obtener el máximo y mínimo de la cantidad de precipitación
    public static double getMaxPrecipitation(List<WeatherData> weatherDataList) {
        double maxPrecipitation = Double.MIN_VALUE;
        for (WeatherData data : weatherDataList) {
            if (data.getPrecipitationAmount() > maxPrecipitation) {
                maxPrecipitation = data.getPrecipitationAmount();
            }
        }
        return maxPrecipitation;
    }

    public static double getMinPrecipitation(List<WeatherData> weatherDataList) {
        double minPrecipitation = Double.MAX_VALUE;
        for (WeatherData data : weatherDataList) {
            if (data.getPrecipitationAmount() < minPrecipitation) {
                minPrecipitation = data.getPrecipitationAmount();
            }
        }
        return minPrecipitation;
    }
}
