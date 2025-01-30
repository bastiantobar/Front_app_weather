package com.example.frontweatherapp.api.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.frontweatherapp.models.models.WeatherData;

import java.util.List;

public class WeatherCalculationService extends Service {

    // Métodos para obtener los valores máximos y mínimos de temperatura, viento y precipitación

    public double getMaxTemperature(List<WeatherData> weatherDataList) {
        double maxTemp = Double.MIN_VALUE;
        for (WeatherData data : weatherDataList) {
            if (data.getAirTemperature() > maxTemp) {
                maxTemp = data.getAirTemperature();
            }
        }
        return maxTemp;
    }

    public double getMinTemperature(List<WeatherData> weatherDataList) {
        double minTemp = Double.MAX_VALUE;
        for (WeatherData data : weatherDataList) {
            if (data.getAirTemperature() < minTemp) {
                minTemp = data.getAirTemperature();
            }
        }
        return minTemp;
    }

    public double getMaxWindSpeed(List<WeatherData> weatherDataList) {
        double maxWindSpeed = Double.MIN_VALUE;
        for (WeatherData data : weatherDataList) {
            if (data.getWindSpeed() > maxWindSpeed) {
                maxWindSpeed = data.getWindSpeed();
            }
        }
        return maxWindSpeed;
    }

    public double getMinWindSpeed(List<WeatherData> weatherDataList) {
        double minWindSpeed = Double.MAX_VALUE;
        for (WeatherData data : weatherDataList) {
            if (data.getWindSpeed() < minWindSpeed) {
                minWindSpeed = data.getWindSpeed();
            }
        }
        return minWindSpeed;
    }

    public double getMaxPrecipitation(List<WeatherData> weatherDataList) {
        double maxPrecipitation = Double.MIN_VALUE;
        for (WeatherData data : weatherDataList) {
            if (data.getPrecipitationAmount() > maxPrecipitation) {
                maxPrecipitation = data.getPrecipitationAmount();
            }
        }
        return maxPrecipitation;
    }

    public double getMinPrecipitation(List<WeatherData> weatherDataList) {
        double minPrecipitation = Double.MAX_VALUE;
        for (WeatherData data : weatherDataList) {
            if (data.getPrecipitationAmount() < minPrecipitation) {
                minPrecipitation = data.getPrecipitationAmount();
            }
        }
        return minPrecipitation;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Este servicio no es utilizado con binding, por lo que retornamos null
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Aquí puedes realizar los cálculos y pasar los resultados a las actividades o fragmentos
        // Por ejemplo, podrías enviar los resultados a través de un broadcast, Intent o callback
        // Retornar START_NOT_STICKY para que el servicio no se reinicie si se detiene
        return START_NOT_STICKY;
    }
}
