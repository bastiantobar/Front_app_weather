package com.example.frontweatherapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.models.WeatherData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private final List<WeatherData> weatherDataList;

    public WeatherAdapter(List<WeatherData> weatherDataList) {
        this.weatherDataList = weatherDataList;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_hour, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        WeatherData data = weatherDataList.get(position);

        // Formatear la fecha
        String formattedDate = formatDate(data.getTime());
        holder.dateTimeTextView.setText(formattedDate);

        // Configurar otros datos
        holder.temperatureTextView.setText("Temperatura: " + data.getAirTemperature() + "°C");
        holder.windSpeedTextView.setText("Viento: " + data.getWindSpeed() + " m/s");
        holder.precipitationTextView.setText("Precipitación: " + data.getPrecipitationAmount() + " mm");
    }

    @Override
    public int getItemCount() {
        return weatherDataList.size();
    }

    private String formatDate(String isoDate) {
        try {
            // Parsear fecha en formato ISO-8601
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            Date date = isoFormat.parse(isoDate);

            // Formatear a un formato legible
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy - HH:mm", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return isoDate; // Devolver la fecha sin formatear si hay un error
        }
    }

    static class WeatherViewHolder extends RecyclerView.ViewHolder {

        TextView dateTimeTextView, temperatureTextView, windSpeedTextView, precipitationTextView;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTimeTextView = itemView.findViewById(R.id.text_date_time);
            temperatureTextView = itemView.findViewById(R.id.text_temperature);
            windSpeedTextView = itemView.findViewById(R.id.text_wind_speed);
            precipitationTextView = itemView.findViewById(R.id.text_precipitation);
        }
    }
}
