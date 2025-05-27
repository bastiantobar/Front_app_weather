package com.example.frontweatherapp.ui.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView; // Importar ImageView
import android.widget.RelativeLayout; // Importar RelativeLayout
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.models.AirQuality;
import com.example.frontweatherapp.models.Components;
import com.example.frontweatherapp.models.WeatherResponse;
import com.example.frontweatherapp.models.Location;

// Importaciones para MPAndroidChart
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AirQualityFragment extends Fragment {

    private static final String TAG = "AirQualityFragment";
    private LoadingDialogFragment loadingDialog;

    // aqiValueTextView ha sido eliminado ya que no está en el layout actual
    private TextView aqiCategoryTextView;
    private TextView aqiDescriptionTextView;
    private TextView coValueTextView, noValueTextView, no2ValueTextView, o3ValueTextView,
            so2ValueTextView, pm25ValueTextView, pm10ValueTextView, nh3ValueTextView;

    private TextView aqiDateTimeTextView;
    private TextView aqiLocationTextView;

    private ImageView aqiGaugeBackground;
    private ImageView aqiGaugeIndicator;

    private HorizontalBarChart pollutantBarChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_air_quality, container, false);
        showLoading(true);

        // Inicializar vistas
        aqiCategoryTextView = rootView.findViewById(R.id.aqiCategoryTextView);
        aqiDescriptionTextView = rootView.findViewById(R.id.aqiDescriptionTextView);
        coValueTextView = rootView.findViewById(R.id.coValueTextView);
        noValueTextView = rootView.findViewById(R.id.noValueTextView);
        no2ValueTextView = rootView.findViewById(R.id.no2ValueTextView);
        o3ValueTextView = rootView.findViewById(R.id.o3ValueTextView);
        so2ValueTextView = rootView.findViewById(R.id.so2ValueTextView);
        pm25ValueTextView = rootView.findViewById(R.id.pm25ValueTextView);
        pm10ValueTextView = rootView.findViewById(R.id.pm10ValueTextView);
        nh3ValueTextView = rootView.findViewById(R.id.nh3ValueTextView);

        aqiDateTimeTextView = rootView.findViewById(R.id.aqiDateTimeTextView);
        aqiLocationTextView = rootView.findViewById(R.id.aqiLocationTextView);

        aqiGaugeBackground = rootView.findViewById(R.id.aqiGaugeBackground);
        aqiGaugeIndicator = rootView.findViewById(R.id.aqiGaugeIndicator);

        pollutantBarChart = rootView.findViewById(R.id.pollutantBarChart);

        // Recuperar el WeatherResponse de los argumentos
        if (getArguments() != null && getArguments().containsKey("fullWeatherData")) {
            WeatherResponse fullWeatherData = (WeatherResponse) getArguments().getSerializable("fullWeatherData");
            if (fullWeatherData != null && fullWeatherData.getAirQuality() != null) {
                updateUIWithAirQualityData(fullWeatherData.getAirQuality(), fullWeatherData.getLocation());
            } else {
                Log.e(TAG, "No se encontraron datos de calidad del aire en el WeatherResponse recibido.");
                Toast.makeText(getContext(), "Datos de calidad del aire no disponibles.", Toast.LENGTH_LONG).show();
                showLoading(false);
            }
        } else {
            Log.e(TAG, "No se recibió WeatherResponse en los argumentos. Esto no debería ocurrir si la navegación es correcta.");
            Toast.makeText(getContext(), "Error: Datos de calidad del aire no recibidos.", Toast.LENGTH_LONG).show();
            showLoading(false);
        }

        return rootView;
    }

    @SuppressLint("DefaultLocale")
    private void updateUIWithAirQualityData(AirQuality airQuality, Location location) {
        if (airQuality == null) {
            Log.e(TAG, "AirQuality object is null, cannot update UI.");
            showLoading(false);
            return;
        }

        // Actualizar textos de categoría y descripción
        String category = getAqiCategory(airQuality.getAqi());
        aqiCategoryTextView.setText(category);
        aqiDescriptionTextView.setText(getAqiDescription(airQuality.getAqi()));

        // Actualizar fecha/hora y ubicación
        if (airQuality.getTimestamp() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm EEE, MMM d,yyyy", Locale.getDefault());
            aqiDateTimeTextView.setText(sdf.format(new Date(airQuality.getTimestamp() * 1000L)));
        } else {
            aqiDateTimeTextView.setText("Fecha/Hora no disponible");
        }

        if (location != null && location.getName() != null) {
            aqiLocationTextView.setText(location.getName());
        } else {
            aqiLocationTextView.setText("Ubicación desconocida");
        }

        // Posicionar y colorear el indicador del medidor
        positionGaugeIndicator(airQuality.getAqi());
        colorGaugeIndicator(airQuality.getAqi());

        Components components = airQuality.getComponents();
        if (components != null) {
            coValueTextView.setText(String.format("%.2f µg/m³", components.getCo()));
            noValueTextView.setText(String.format("%.2f µg/m³", components.getNo()));
            no2ValueTextView.setText(String.format("%.2f µg/m³", components.getNo2()));
            o3ValueTextView.setText(String.format("%.2f µg/m³", components.getO3()));
            so2ValueTextView.setText(String.format("%.2f µg/m³", components.getSo2()));
            pm25ValueTextView.setText(String.format("%.2f µg/m³", components.getPm2_5()));
            pm10ValueTextView.setText(String.format("%.2f µg/m³", components.getPm10()));
            nh3ValueTextView.setText(String.format("%.2f µg/m³", components.getNh3()));

            setupPollutantBarChart(components);

        } else {
            Log.e(TAG, "Components object is null in AirQuality data.");
            Toast.makeText(getContext(), "Datos de componentes de calidad del aire no disponibles.", Toast.LENGTH_SHORT).show();
        }

        showLoading(false);
    }

    private void positionGaugeIndicator(int aqiValue) {
        aqiGaugeBackground.post(() -> {
            int gaugeWidth = aqiGaugeBackground.getWidth();
            if (gaugeWidth == 0) {
                Log.w(TAG, "Gauge background width is 0, cannot position indicator.");
                return;
            }

            float maxAqiDisplay = 300f;
            float normalizedAqi = Math.min(aqiValue, maxAqiDisplay) / maxAqiDisplay;

            float indicatorX = normalizedAqi * gaugeWidth - (aqiGaugeIndicator.getWidth() / 2f);

            indicatorX = Math.max(0, indicatorX);
            indicatorX = Math.min(gaugeWidth - aqiGaugeIndicator.getWidth(), indicatorX);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) aqiGaugeIndicator.getLayoutParams();
            params.leftMargin = (int) indicatorX;
            aqiGaugeIndicator.setLayoutParams(params);
        });
    }

    private void colorGaugeIndicator(int aqiValue) {
        int color = getAqiColor(aqiValue);
        GradientDrawable newBackground = new GradientDrawable();
        newBackground.setShape(GradientDrawable.OVAL);
        newBackground.setColor(color);
        aqiGaugeIndicator.setBackground(newBackground);
    }

    private void setupPollutantBarChart(Components components) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        entries.add(new BarEntry(0f, (float) components.getCo())); labels.add("CO"); colors.add(Color.parseColor("#FFC107"));
        entries.add(new BarEntry(1f, (float) components.getNo())); labels.add("NO"); colors.add(Color.parseColor("#8BC34A"));
        entries.add(new BarEntry(2f, (float) components.getNo2())); labels.add("NO₂"); colors.add(Color.parseColor("#FF5722"));
        entries.add(new BarEntry(3f, (float) components.getO3())); labels.add("O₃"); colors.add(Color.parseColor("#2196F3"));
        entries.add(new BarEntry(4f, (float) components.getSo2())); labels.add("SO₂"); colors.add(Color.parseColor("#9C27B0"));
        entries.add(new BarEntry(5f, (float) components.getPm2_5())); labels.add("PM2.5"); colors.add(Color.parseColor("#F44336"));
        entries.add(new BarEntry(6f, (float) components.getPm10())); labels.add("PM10"); colors.add(Color.parseColor("#E91E63"));
        entries.add(new BarEntry(7f, (float) components.getNh3())); labels.add("NH₃"); colors.add(Color.parseColor("#00BCD4"));

        BarDataSet dataSet = new BarDataSet(entries, "Concentración (µg/m³)");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.BLACK);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        pollutantBarChart.setData(barData);
        pollutantBarChart.setFitBars(true);

        XAxis xAxis = pollutantBarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.BLACK);

        YAxis leftAxis = pollutantBarChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setTextSize(10f);
        leftAxis.setTextColor(Color.BLACK);

        YAxis rightAxis = pollutantBarChart.getAxisRight();
        rightAxis.setEnabled(false);

        pollutantBarChart.getDescription().setEnabled(false);
        pollutantBarChart.setDrawValueAboveBar(true);
        pollutantBarChart.setTouchEnabled(true);
        pollutantBarChart.setDragEnabled(true);
        pollutantBarChart.setScaleEnabled(true);
        pollutantBarChart.setPinchZoom(false);

        Legend legend = pollutantBarChart.getLegend();
        legend.setEnabled(false);

        pollutantBarChart.animateY(1500);
        pollutantBarChart.invalidate();
    }

    private String getAqiCategory(int aqi) {
        if (aqi <= 50) return "Buena";
        else if (aqi <= 100) return "Moderada";
        else if (aqi <= 150) return "Dañina para grupos sensibles";
        else if (aqi <= 200) return "Dañina";
        else if (aqi <= 300) return "Muy dañina";
        else return "Peligrosa";
    }

    private String getAqiDescription(int aqi) {
        if (aqi <= 50) return "La calidad del aire es satisfactoria y la contaminación del aire presenta poco o ningún riesgo.";
        else if (aqi <= 100) return "La calidad del aire es aceptable; sin embargo, para algunos contaminantes, puede haber un riesgo moderado para un número muy pequeño de personas inusualmente sensibles a la contaminación del aire.";
        else if (aqi <= 150) return "Los miembros de grupos sensibles pueden experimentar efectos en la salud. Es poco probable que el público en general se vea afectado.";
        else if (aqi <= 200) return "Todos pueden comenzar a experimentar efectos en la salud; los miembros de grupos sensibles pueden experimentar efectos más graves en la salud.";
        else if (aqi <= 300) return "Advertencias de salud de condiciones de emergencia. Es más probable que toda la población se vea afectada.";
        else return "Alerta de salud: todos pueden experimentar efectos más graves en la salud.";
    }

    private int getAqiColor(int aqi) {
        if (aqi <= 50) return Color.parseColor("#00E676"); // Verde
        else if (aqi <= 100) return Color.parseColor("#FFD600"); // Amarillo
        else if (aqi <= 150) return Color.parseColor("#FFAB00"); // Naranja
        else if (aqi <= 200) return Color.parseColor("#FF1744"); // Rojo
        else if (aqi <= 300) return Color.parseColor("#9C27B0"); // Morado
        else return Color.parseColor("#B71C1C"); // Granate/Rojo oscuro
    }

    private void showToast(String message) {
        if (getActivity() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    public void showLoading(boolean show) {
        if (show) {
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialogFragment();
            }
            if (isAdded()) {
                loadingDialog.show(getParentFragmentManager(), "loading");
            }
        } else {
            if (loadingDialog != null && loadingDialog.isAdded()) {
                loadingDialog.dismiss();
            }
        }
    }
}
