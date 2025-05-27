package com.example.frontweatherapp.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log; // Importar Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.models.HourlyForecast; // ¡Cambiado de WeatherData a HourlyForecast!

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.HourlyForecastViewHolder> {

    private List<HourlyForecast> hourlyForecastList; // ¡Tipo de lista cambiado!

    public HourlyForecastAdapter(List<HourlyForecast> hourlyForecastList) { // ¡Tipo de constructor cambiado!
        this.hourlyForecastList = hourlyForecastList;
    }

    // Método para actualizar los datos del adaptador
    public void updateData(List<HourlyForecast> newData) { // ¡Tipo de parámetro cambiado!
        this.hourlyForecastList.clear();
        this.hourlyForecastList.addAll(newData);
        notifyDataSetChanged(); // Notifica a la RecyclerView que los datos han cambiado
    }

    @NonNull
    @Override
    public HourlyForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly_forecast_item, parent, false);
        return new HourlyForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyForecastViewHolder holder, int position) {
        HourlyForecast forecast = hourlyForecastList.get(position); // ¡Tipo de objeto cambiado!
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
        TextView windTextView;
        TextView precipitationTextView;

        public HourlyForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            tempTextView = itemView.findViewById(R.id.tempTextView);
            hourlyWeatherIcon = itemView.findViewById(R.id.hourlyWeatherIcon);
            windTextView = itemView.findViewById(R.id.windTextView);
            precipitationTextView = itemView.findViewById(R.id.precipitationTextView);
        }

        @SuppressLint("DefaultLocale")
        public void bind(HourlyForecast forecast) { // ¡Tipo de parámetro cambiado!
            // Formatear la hora
            try {
                // Asegúrate de que el formato de fecha de entrada coincide con el de tu API
                // Ejemplo: "2025-05-27T10:00:00Z"
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                Date date = inputFormat.parse(forecast.getTime());
                timeTextView.setText(outputFormat.format(date));
            } catch (ParseException e) {
                timeTextView.setText(forecast.getTime()); // En caso de error, muestra la cadena original
                Log.e("HourlyForecastAdapter", "Error parsing date: " + e.getMessage());
            }

            tempTextView.setText(String.format("%.1f°C", forecast.getAirTemperature()));
            windTextView.setText(String.format("%.1f m/s", forecast.getWindSpeed()));
            precipitationTextView.setText(String.format("%.1f mm", forecast.getPrecipitationAmount()));

            // Lógica para el icono y color (simplificada para el ejemplo)
            // Puedes expandir esta lógica para usar más datos de HourlyForecast si tu API los proporciona
            // o si puedes inferir un estado del cielo (soleado, nublado, etc.) de los datos existentes.
            updateHourlyWeatherIconAndColor(forecast.getAirTemperature(), forecast.getPrecipitationAmount());
        }

        private void updateHourlyWeatherIconAndColor(double temperature, double precipitationAmount) {
            // Lógica simple para el icono:
            // Si hay precipitación, muestra icono de lluvia, si no, sol.
            // Puedes añadir más complejidad si tienes datos de nubosidad o tipo de precipitación.
            if (precipitationAmount > 0.0) {
                hourlyWeatherIcon.setImageResource(R.drawable.ic_rainy); // Asume que tienes un ic_rainy
                hourlyWeatherIcon.setColorFilter(Color.GRAY);
            } else {
                hourlyWeatherIcon.setImageResource(R.drawable.ic_sunny); // Asume que tienes un ic_sunny
                hourlyWeatherIcon.setColorFilter(Color.YELLOW);
            }

            // Lógica para el color de la temperatura (similar a HomeFragment)
            if (temperature < 10) {
                tempTextView.setTextColor(Color.BLUE);
            } else if (temperature >= 10 && temperature <= 25) {
                tempTextView.setTextColor(Color.GREEN);
            } else {
                tempTextView.setTextColor(Color.RED);
            }
        }
    }
}
