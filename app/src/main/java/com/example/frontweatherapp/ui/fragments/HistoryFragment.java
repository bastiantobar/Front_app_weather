package com.example.frontweatherapp.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.api.service.WeatherApiService;
import com.example.frontweatherapp.api.models.WeatherData;
import com.example.frontweatherapp.network.RetrofitClient;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout para este fragmento
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        // Inicializar el gráfico
        LineChart lineChart = rootView.findViewById(R.id.lineChart);

        // Llamar a la función para obtener las predicciones horarias
        fetchHourlyForecasts(lineChart);

        return rootView;
    }

    private void fetchHourlyForecasts(LineChart lineChart) {
        // Obtener el token desde SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        String token = preferences.getString("TOKEN", null);

        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Error: Token no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        WeatherApiService apiService = RetrofitClient.getInstance(requireContext()).create(WeatherApiService.class);

        apiService.getHourlyForecasts("Bearer " + token).enqueue(new Callback<List<WeatherData>>() {
            @Override
            public void onResponse(@NonNull Call<List<WeatherData>> call, @NonNull Response<List<WeatherData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<WeatherData> forecasts = response.body();

                    // Actualizamos el gráfico con los datos de temperatura
                    updateChart(lineChart, forecasts);
                } else {
                    Toast.makeText(getContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<WeatherData>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateChart(LineChart lineChart, List<WeatherData> forecasts) {
        // Crear una lista de entradas para el gráfico
        ArrayList<Entry> entries = new ArrayList<>();

        // Variables para almacenar las temperaturas máxima y mínima
        double maxTemp = Double.MIN_VALUE;
        double minTemp = Double.MAX_VALUE;

        // Iterar sobre los datos de los pronósticos
        for (int i = 0; i < forecasts.size(); i++) {
            WeatherData data = forecasts.get(i);
            entries.add(new Entry(i, (float) data.getAirTemperature())); // Usamos 'i' como eje X y la temperatura como eje Y

            // Calcular la temperatura máxima y mínima
            maxTemp = Math.max(maxTemp, data.getAirTemperature());
            minTemp = Math.min(minTemp, data.getAirTemperature());
        }

        // Actualizar los valores de las tarjetas con las temperaturas máxima y mínima
        TextView maxTempValue = getView().findViewById(R.id.maxTempValue);
        TextView minTempValue = getView().findViewById(R.id.minTempValue);

        maxTempValue.setText(String.format("%.1f°C", maxTemp)); // Mostrar la temperatura máxima
        minTempValue.setText(String.format("%.1f°C", minTemp)); // Mostrar la temperatura mínima

        // Crear un conjunto de datos (LineDataSet)
        LineDataSet lineDataSet = new LineDataSet(entries, "Temperatura (°C)");

        // Personalizar el color de la línea
        lineDataSet.setColor(getResources().getColor(R.color.colorAccent));
        lineDataSet.setValueTextColor(getResources().getColor(R.color.light_gray)); // Color de los valores

        // Crear LineData con el conjunto de datos
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        // Configuración de los ejes X y Y para el zoom
        lineChart.setDragEnabled(true); // Habilitar el desplazamiento (arrastrar)
        lineChart.setScaleEnabled(true); // Habilitar el zoom

        // Configuración de los ejes X para mostrar las horas
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                WeatherData data = forecasts.get((int) value); // Usamos el índice para obtener el tiempo
                return data.getTime(); // Devolver el tiempo en formato adecuado
            }
        });

        // Personalizar el gráfico (opcional)
        lineChart.getDescription().setEnabled(false); // Deshabilitar la descripción
        lineChart.animateX(1000); // Animación para el gráfico

        // Habilitar el zoom y desplazamiento en los ejes
        lineChart.setPinchZoom(true); // Habilitar zoom con pinch (pellizcar)

        // Actualizar el gráfico
        lineChart.invalidate();
    }




}
