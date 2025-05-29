package com.example.frontweatherapp.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.api.service.WeatherApiService;
import com.example.frontweatherapp.models.HistoricalWeatherEntry;
import com.example.frontweatherapp.models.HourlyForecast;
import com.example.frontweatherapp.models.LocationCoordinates;
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
import com.github.mikephil.charting.formatter.ValueFormatter; // Importar ValueFormatter

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraficFragment extends Fragment {

    private static final String TAG = "GraficFragment";

    private LineChart lineChart;
    private BarChart barChart;
    private PieChart pieChart;
    private LoadingDialogFragment loadingDialog;

    private EditText editTextLocation;
    private Button buttonFetchWeather;

    private static final int DEFAULT_HISTORICAL_LIMIT = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Iniciando la creación de la vista del fragmento.");
        View rootView = inflater.inflate(R.layout.fragment_graphs, container, false);

        editTextLocation = rootView.findViewById(R.id.editTextLocation);
        buttonFetchWeather = rootView.findViewById(R.id.buttonFetchWeather);
        lineChart = rootView.findViewById(R.id.lineChart);
        barChart = rootView.findViewById(R.id.barChart);
        pieChart = rootView.findViewById(R.id.pieChart);

        // Configuración inicial de los gráficos para evitar problemas de visualización
        setupChartDefaults(lineChart);
        setupChartDefaults(barChart);
        setupChartDefaults(pieChart);


        buttonFetchWeather.setOnClickListener(v -> {
            Log.d(TAG, "Botón 'Cargar Clima' clickeado.");
            fetchCoordinatesAndWeatherData();
        });

        return rootView;
    }

    // Método para configurar defaults en los gráficos y evitar ejes pegados al inicio
    private void setupChartDefaults(com.github.mikephil.charting.charts.Chart chart) {
        chart.setNoDataText("Cargando datos...");
        chart.setNoDataTextColor(Color.GRAY);
        chart.invalidate(); // Refrescar el gráfico
    }


    private void fetchCoordinatesAndWeatherData() {
        String locationName = editTextLocation.getText().toString().trim();
        Log.d(TAG, "fetchCoordinatesAndWeatherData: Intentando obtener coordenadas para: '" + locationName + "'");

        if (locationName.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, ingresa un nombre de ubicación.", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "fetchCoordinatesAndWeatherData: Nombre de ubicación vacío. No se procede con la solicitud.");
            return;
        }

        showLoading(true);

        SharedPreferences preferences = requireContext().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        String token = preferences.getString("TOKEN", null);

        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Error: Token de autorización no disponible.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "fetchCoordinatesAndWeatherData: Token de autorización nulo o vacío.");
            showLoading(false);
            return;
        }

        WeatherApiService apiService = RetrofitClient.getInstance(requireContext()).create(WeatherApiService.class);
        Log.d(TAG, "fetchCoordinatesAndWeatherData: Realizando llamada a la API para obtener coordenadas.");

        apiService.getCoordinatesForLocation("Bearer " + token, locationName)
                .enqueue(new Callback<LocationCoordinates>() {
                    @Override
                    public void onResponse(@NonNull Call<LocationCoordinates> call, @NonNull Response<LocationCoordinates> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            LocationCoordinates coords = response.body();
                            Log.d(TAG, "onResponse (Coordenadas): Coordenadas obtenidas: Lat=" + coords.getLatitude() + ", Lon=" + coords.getLongitude());
                            fetchWeatherData(coords.getLatitude(), coords.getLongitude());
                        } else {
                            String errorBody = "";
                            try {
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "onResponse (Coordenadas): Error al leer errorBody: " + e.getMessage());
                            }
                            Toast.makeText(getContext(), "Ubicación no encontrada o error en el servicio de geocodificación: " + response.code(), Toast.LENGTH_LONG).show();
                            Log.e(TAG, "onResponse (Coordenadas): Fallo en la respuesta. Código: " + response.code() + ", Mensaje: " + response.message() + ", Error Body: " + errorBody);
                            showLoading(false);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<LocationCoordinates> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), "Error de red al obtener coordenadas: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onFailure (Coordenadas): Error de red al obtener coordenadas. Mensaje: " + t.getMessage(), t);
                        t.printStackTrace();
                        showLoading(false);
                    }
                });
    }

    private void fetchWeatherData(double latitude, double longitude) {
        Log.d(TAG, "fetchWeatherData: Intentando obtener datos históricos para Lat=" + latitude + ", Lon=" + longitude);
        SharedPreferences preferences = requireContext().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        String token = preferences.getString("TOKEN", null);

        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Error: Token no disponible", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "fetchWeatherData: Token de autorización nulo o vacío.");
            showLoading(false);
            return;
        }

        WeatherApiService apiService = RetrofitClient.getInstance(requireContext()).create(WeatherApiService.class);
        Log.d(TAG, "fetchWeatherData: Realizando llamada a la API para obtener datos históricos.");

        apiService.getHistoricalWeather("Bearer " + token, latitude, longitude, DEFAULT_HISTORICAL_LIMIT)
                .enqueue(new Callback<List<HistoricalWeatherEntry>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<HistoricalWeatherEntry>> call, @NonNull Response<List<HistoricalWeatherEntry>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Log.d(TAG, "onResponse (Histórico): Datos históricos obtenidos. Número de entradas: " + response.body().size());
                            List<HourlyForecast> forecasts = response.body().get(0).getHourlyForecasts();
                            if (forecasts != null && !forecasts.isEmpty()) {
                                Log.d(TAG, "onResponse (Histórico): Pronósticos por hora encontrados: " + forecasts.size());
                                updateCharts(forecasts);
                            } else {
                                Toast.makeText(getContext(), "No se encontraron pronósticos por hora para la ubicación seleccionada.", Toast.LENGTH_LONG).show();
                                Log.w(TAG, "onResponse (Histórico): La lista de pronósticos por hora está vacía o nula.");
                                clearCharts();
                                showLoading(false);
                            }
                        } else {
                            String errorBody = "";
                            try {
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "onResponse (Histórico): Error al leer errorBody: " + e.getMessage());
                            }
                            Toast.makeText(getContext(), "Error al obtener datos históricos: " + response.code(), Toast.LENGTH_LONG).show();
                            Log.e(TAG, "onResponse (Histórico): Fallo en la respuesta. Código: " + response.code() + ", Mensaje: " + response.message() + ", Error Body: " + errorBody);
                            clearCharts();
                            showLoading(false);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<HistoricalWeatherEntry>> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), "Error de red al obtener datos históricos: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onFailure (Histórico): Error de red al obtener datos históricos. Mensaje: " + t.getMessage(), t);
                        t.printStackTrace();
                        clearCharts();
                        showLoading(false);
                    }
                });
    }

    private void updateCharts(List<HourlyForecast> forecasts) {
        Log.d(TAG, "updateCharts: Actualizando gráficos con " + forecasts.size() + " pronósticos.");
        updateLineChart(forecasts);
        updateBarChart(forecasts);
        updatePieChart(forecasts);
        showLoading(false);
    }

    private void clearCharts() {
        Log.d(TAG, "clearCharts: Limpiando todos los gráficos.");
        lineChart.clear();
        lineChart.invalidate();
        barChart.clear();
        barChart.invalidate();
        pieChart.clear();
        pieChart.invalidate();
    }

    private void updateLineChart(List<HourlyForecast> forecasts) {
        Log.d(TAG, "updateLineChart: Actualizando gráfico de líneas.");
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        if (forecasts.isEmpty()) {
            Log.w(TAG, "updateLineChart: No hay datos para el gráfico de líneas. Limpiando.");
            lineChart.clear();
            lineChart.invalidate();
            return;
        }

        for (int i = 0; i < forecasts.size(); i++) {
            HourlyForecast data = forecasts.get(i);
            entries.add(new Entry(i, (float) data.getAirTemperature()));
            labels.add(formatTime(data.getTime()));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "Temperatura (°C)");
        lineDataSet.setColor(Color.RED);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setDrawCircleHole(false);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Asegura que las etiquetas se muestren en cada valor entero
        xAxis.setLabelCount(labels.size(), false); // Intenta mostrar todas las etiquetas si es posible, sin forzar solapamiento
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(45f); // <--- ROTAR ETIQUETAS 45 GRADOS
        xAxis.setTextSize(10f); // <--- REDUCIR TAMAÑO DE TEXTO
        xAxis.setCenterAxisLabels(false); // No centrar, deja que el formatter decida


        lineChart.getDescription().setEnabled(false);
        lineChart.animateX(1000);
        lineChart.invalidate();
        Log.d(TAG, "updateLineChart: Gráfico de líneas actualizado.");
    }

    /** 📊 **Gráfico de Barras (Viento y Precipitación)** **/
    private void updateBarChart(List<HourlyForecast> forecasts) {
        Log.d(TAG, "updateBarChart: Actualizando gráfico de barras.");
        ArrayList<BarEntry> windEntries = new ArrayList<>();
        ArrayList<BarEntry> rainEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        if (forecasts.isEmpty()) {
            Log.w(TAG, "updateBarChart: No hay datos para el gráfico de barras. Limpiando.");
            barChart.clear();
            barChart.invalidate();
            return;
        }

        for (int i = 0; i < forecasts.size(); i++) {
            HourlyForecast data = forecasts.get(i);
            windEntries.add(new BarEntry(i, (float) data.getWindSpeed()));
            rainEntries.add(new BarEntry(i, (float) data.getPrecipitationAmount()));
            labels.add(formatTime(data.getTime()));
        }

        BarDataSet windDataSet = new BarDataSet(windEntries, "Viento (m/s)");
        windDataSet.setColor(Color.BLUE);
        windDataSet.setValueTextColor(Color.BLACK);

        BarDataSet rainDataSet = new BarDataSet(rainEntries, "Precipitación (mm)");
        rainDataSet.setColor(Color.CYAN);
        rainDataSet.setValueTextColor(Color.BLACK);

        BarData barData = new BarData(windDataSet, rainDataSet);
        float groupSpace = 0.05f; // Espacio entre grupos de barras
        float barSpace = 0.02f; // Espacio entre barras individuales en un grupo
        float barWidth = 0.45f; // Ancho de cada barra
        // (barWidth + barSpace) * 2 + groupSpace = 1.00 -> para que el grupo ocupe 1 unidad en el eje X
        barData.setBarWidth(barWidth);


        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size(), false); // Mostrar todas las etiquetas si es posible
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(45f); // <--- ROTAR ETIQUETAS 45 GRADOS
        xAxis.setTextSize(10f); // <--- REDUCIR TAMAÑO DE TEXTO

        barChart.getXAxis().setCenterAxisLabels(true);
        // Ajustar el grupo de barras para que las etiquetas coincidan con el centro del grupo
        // Si hay N grupos de barras, y quieres que cada etiqueta esté centrada en su grupo,
        // el rangoVisibleX debe ser lo suficientemente grande para cubrir N grupos.
        // El offset inicial (0.5f) se usa para alinear el primer grupo.
        barChart.groupBars(0f, groupSpace, barSpace); // Offset inicial, espacio entre grupos, espacio entre barras en un grupo

        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
        Log.d(TAG, "updateBarChart: Gráfico de barras actualizado.");
    }

    private void updatePieChart(List<HourlyForecast> forecasts) {
        Log.d(TAG, "updatePieChart: Actualizando gráfico circular.");
        float lluvia = 0;
        float precipitacionLigera = 0;
        float sinPrecipitacion = 0;
        int totalEntries = forecasts.size();

        if (totalEntries == 0) {
            Log.w(TAG, "updatePieChart: No hay datos para el gráfico circular. Limpiando.");
            pieChart.clear();
            pieChart.invalidate();
            pieChart.setNoDataText("No hay datos de precipitación."); // Mensaje específico
            return;
        }

        for (HourlyForecast data : forecasts) {
            double precipitation = data.getPrecipitationAmount();

            if (precipitation > 5) {
                lluvia++;
            } else if (precipitation > 0) {
                precipitacionLigera++;
            } else {
                sinPrecipitacion++;
            }
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        if (lluvia > 0) entries.add(new PieEntry(lluvia, "Lluvia 🌧"));
        if (precipitacionLigera > 0) entries.add(new PieEntry(precipitacionLigera, "Precipitación Ligera ☔"));
        if (sinPrecipitacion > 0) entries.add(new PieEntry(sinPrecipitacion, "Sin Precipitación ☀"));

        if (entries.isEmpty()) {
            Log.w(TAG, "updatePieChart: Las entradas para el gráfico circular están vacías. Limpiando.");
            pieChart.clear();
            pieChart.invalidate();
            pieChart.setNoDataText("No hay categorías de precipitación para mostrar."); // Mensaje específico
            return;
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "Condiciones de Precipitación");
        pieDataSet.setColors(Color.BLUE, Color.GRAY, Color.YELLOW);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(12f);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);
        pieChart.invalidate();
        Log.d(TAG, "updatePieChart: Gráfico circular actualizado.");
    }

    private String formatTime(String time) {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            Date date = originalFormat.parse(time);

            SimpleDateFormat targetFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return targetFormat.format(date);
        } catch (Exception e) {
            Log.e(TAG, "formatTime: Error al formatear la hora: " + time + ". Mensaje: " + e.getMessage(), e);
            e.printStackTrace();
            return "";
        }
    }

    public void showLoading(boolean show) {
        if (show) {
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialogFragment();
                Log.d(TAG, "showLoading: Creando nueva instancia de LoadingDialogFragment.");
            }
            // Asegúrate de que el fragmento no esté ya añadido antes de mostrarlo
            if (!loadingDialog.isAdded() && getParentFragmentManager().findFragmentByTag("loading") == null) {
                loadingDialog.show(getParentFragmentManager(), "loading");
                Log.d(TAG, "showLoading: Mostrando LoadingDialogFragment.");
            } else {
                Log.d(TAG, "showLoading: LoadingDialogFragment ya está visible o añadido.");
            }
        } else {
            if (loadingDialog != null) {
                // Solo llama a dismiss si el diálogo está visible
                if (loadingDialog.getDialog() != null && loadingDialog.getDialog().isShowing()) {
                    loadingDialog.dismiss();
                    Log.d(TAG, "showLoading: Ocultando LoadingDialogFragment.");
                } else {
                    Log.d(TAG, "showLoading: LoadingDialogFragment no está visible, no se puede ocultar.");
                }
            } else {
                Log.d(TAG, "showLoading: LoadingDialogFragment es nulo, no se puede ocultar.");
            }
        }
    }
}