package com.example.frontweatherapp.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.api.service.WeatherApiService;
import com.example.frontweatherapp.models.models.WeatherData;
import com.example.frontweatherapp.network.RetrofitClient;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraficFragment extends Fragment {

    private LineChart lineChart;
    private BarChart barChart;
    private PieChart pieChart;
    private LoadingDialogFragment loadingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_graphs, container, false);
        showLoading(true);
        lineChart = rootView.findViewById(R.id.lineChart);
        barChart = rootView.findViewById(R.id.barChart);
        pieChart = rootView.findViewById(R.id.pieChart);

        fetchWeatherData();

        return rootView;
    }

    private void fetchWeatherData() {
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
                    updateCharts(forecasts);
                } else {
                    Toast.makeText(getContext(), "Error al obtener datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<WeatherData>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCharts(List<WeatherData> forecasts) {
        updateLineChart(forecasts);
        updateBarChart(forecasts);
        updatePieChart(forecasts);
        showLoading(false);
    }

     private void updateLineChart(List<WeatherData> forecasts) {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < forecasts.size(); i++) {
            WeatherData data = forecasts.get(i);
            entries.add(new Entry(i, (float) data.getAirTemperature()));

            labels.add(formatTime(data.getTime())); // Formateamos la fecha
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "Temperatura (°C)");
        lineDataSet.setColor(Color.RED);
        lineDataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        // Formatear el eje X con las horas
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        lineChart.getDescription().setEnabled(false);
        lineChart.animateX(1000);
        lineChart.invalidate();
    }

    /** 📊 **Gráfico de Barras (Viento y Precipitación)** **/
    private void updateBarChart(List<WeatherData> forecasts) {
        ArrayList<BarEntry> windEntries = new ArrayList<>();
        ArrayList<BarEntry> rainEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < forecasts.size(); i++) {
            WeatherData data = forecasts.get(i);
            windEntries.add(new BarEntry(i, (float) data.getWindSpeed()));
            rainEntries.add(new BarEntry(i, (float) data.getPrecipitationAmount()));

            labels.add(formatTime(data.getTime()));
        }

        BarDataSet windDataSet = new BarDataSet(windEntries, "Viento (m/s)");
        windDataSet.setColor(Color.BLUE);

        BarDataSet rainDataSet = new BarDataSet(rainEntries, "Precipitación (mm)");
        rainDataSet.setColor(Color.CYAN);

        BarData barData = new BarData(windDataSet, rainDataSet);
        barChart.setData(barData);

        // Formatear el eje X con las horas
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void updatePieChart(List<WeatherData> forecasts) {
        float lluvia = 0;
        float nublado = 0;
        float despejado = 0;

        for (WeatherData data : forecasts) {
            float cloudiness = (float) data.getPrecipitationAmount(); // 🌧️ Cambio: Usar precipitación en mm

            if (cloudiness > 5) { // Mucha lluvia
                lluvia++;
            } else if (cloudiness > 1) { // Ligeramente nublado
                nublado++;
            } else { // Soleado
                despejado++;
            }
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(lluvia, "Lluvia 🌧"));
        entries.add(new PieEntry(nublado, "Nublado ☁"));
        entries.add(new PieEntry(despejado, "Despejado ☀"));

        PieDataSet pieDataSet = new PieDataSet(entries, "Condiciones Climáticas");
        pieDataSet.setColors(Color.BLUE, Color.GRAY, Color.YELLOW);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private String formatTime(String time) {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            Date date = originalFormat.parse(time);

            SimpleDateFormat targetFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return targetFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void showLoading(boolean show) {
        if (show) {
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialogFragment();
            }
            loadingDialog.show(getParentFragmentManager(), "loading"); 
        } else {
            if (loadingDialog != null) {
                loadingDialog.dismiss();
            }
        }
    }
}
