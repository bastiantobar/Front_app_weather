package com.example.frontweatherapp.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.models.HourlyForecast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar; // Importar Calendar para la hora del día
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.HourlyForecastViewHolder> {

    private List<HourlyForecast> hourlyForecastList;

    public HourlyForecastAdapter(List<HourlyForecast> hourlyForecastList) {
        this.hourlyForecastList = hourlyForecastList;
    }

    public void updateData(List<HourlyForecast> newData) {
        this.hourlyForecastList.clear();
        this.hourlyForecastList.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HourlyForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly_forecast_item, parent, false);
        return new HourlyForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyForecastViewHolder holder, int position) {
        HourlyForecast forecast = hourlyForecastList.get(position);
        holder.bind(forecast);
    }

    @Override
    public int getItemCount() {
        return hourlyForecastList.size();
    }

    public static class HourlyForecastViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView;
        TextView tempTextView;
        ImageView hourlyWeatherIcon;
        TextView weatherConditionTextView;
        TextView windTextView;
        TextView precipitationTextView;

        public HourlyForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            tempTextView = itemView.findViewById(R.id.tempTextView);
            hourlyWeatherIcon = itemView.findViewById(R.id.hourlyWeatherIcon);
            weatherConditionTextView = itemView.findViewById(R.id.weatherConditionTextView);
            windTextView = itemView.findViewById(R.id.windTextView);
            precipitationTextView = itemView.findViewById(R.id.precipitationTextView);
        }

        @SuppressLint("DefaultLocale")
        public void bind(HourlyForecast forecast) {
            // Formatear la hora
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = null;
            try {
                date = inputFormat.parse(forecast.getTime());
                timeTextView.setText(outputFormat.format(date));
            } catch (ParseException e) {
                timeTextView.setText(forecast.getTime());
                Log.e("HourlyForecastAdapter", "Error parsing date: " + e.getMessage());
            }

            tempTextView.setText(String.format("%.1f°C", forecast.getAirTemperature()));
            windTextView.setText(String.format("%.1f m/s", forecast.getWindSpeed()));
            precipitationTextView.setText(String.format("%.1f mm", forecast.getPrecipitationAmount()));

            // Lógica mejorada para el icono y la descripción del clima
            updateHourlyWeatherIconAndDescription(forecast.getAirTemperature(), forecast.getPrecipitationAmount(), date);

            // Ajustar el color del texto de la temperatura
            if (forecast.getAirTemperature() < 10) {
                tempTextView.setTextColor(Color.BLUE);
            } else if (forecast.getAirTemperature() >= 10 && forecast.getAirTemperature() <= 25) {
                tempTextView.setTextColor(Color.GREEN);
            } else {
                tempTextView.setTextColor(Color.RED);
            }
        }

        private void updateHourlyWeatherIconAndDescription(double temperature, double precipitationAmount, Date forecastDate) {
            int iconResId;
            String conditionText;

            // Determinar si es de día o de noche
            Calendar calendar = Calendar.getInstance();
            if (forecastDate != null) {
                calendar.setTime(forecastDate);
            }
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            boolean isDayTime = (hour >= 6 && hour < 18); // Asumimos día entre 6 AM y 6 PM

            // Lógica de prioridad: Precipitación > Temperatura/Nubosidad > Día/Noche
            if (precipitationAmount > 0.5) { // Si hay precipitación significativa
                if (temperature < 0) { // Si la temperatura es bajo cero, es nieve
                    iconResId = R.drawable.ic_snow;
                    conditionText = "Nevando";
                } else if (precipitationAmount > 5) { // Más de 5mm, posible lluvia fuerte/tormenta
                    iconResId = R.drawable.ic_thunderstorm;
                    conditionText = "Lluvia Fuerte";
                } else { // Lluvia ligera a moderada
                    iconResId = R.drawable.ic_rain;
                    conditionText = "Lluvioso";
                }
            } else if (temperature < 5 && temperature > -5) { // Frío, pero sin precipitación, podría ser niebla o muy nublado
                iconResId = R.drawable.ic_fog;
                conditionText = "Niebla/Nublado";
            } else { // Sin precipitación, basamos en la hora del día y una inferencia simple de nubosidad
                if (isDayTime) {
                    // Aquí podrías añadir lógica para ic_partly_cloudy si tu API te da un valor de nubosidad
                    // Por ahora, asumimos "Soleado" si no hay precipitación y es de día
                    iconResId = R.drawable.ic_sunny;
                    conditionText = "Soleado";
                } else {
                    iconResId = R.drawable.ic_moon;
                    conditionText = "Despejado Noche";
                }
            }

            hourlyWeatherIcon.setImageResource(iconResId);
            weatherConditionTextView.setText(conditionText);
            hourlyWeatherIcon.setColorFilter(null); // Quitar cualquier filtro de color previo
        }
    }
}
