package com.example.frontweatherapp.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.api.service.WeatherApiService;
import com.example.frontweatherapp.models.models.InstantWeather;
import com.example.frontweatherapp.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final int UPDATE_INTERVAL = 10 * 1000; // 10 segundos

    private TextView tempText, humidityText, pressureText, windText, cloudText, lastUpdatedText, currentTempLarge;
    private ImageView weatherIcon;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int pendingRequests = 0;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Inicializar vistas
        tempText = rootView.findViewById(R.id.tempText);
        humidityText = rootView.findViewById(R.id.humidityText);
        pressureText = rootView.findViewById(R.id.pressureText);
        windText = rootView.findViewById(R.id.windText);
        cloudText = rootView.findViewById(R.id.cloudText);
        lastUpdatedText = rootView.findViewById(R.id.lastUpdatedText);
        currentTempLarge = rootView.findViewById(R.id.currentTempLarge);
        weatherIcon = rootView.findViewById(R.id.weatherIcon);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);

        Log.d(TAG, "Vistas inicializadas correctamente");

        // Verificar si las vistas están correctamente asignadas
        checkViewsInitialized();

        swipeRefreshLayout.setOnRefreshListener(this::fetchInstantWeather);

        fetchInstantWeather();
        scheduleUpdates();

        return rootView;
    }

    private void checkViewsInitialized() {
        if (tempText == null) Log.e(TAG, "Error: tempText es NULL");
        if (humidityText == null) Log.e(TAG, "Error: humidityText es NULL");
        if (pressureText == null) Log.e(TAG, "Error: pressureText es NULL");
        if (windText == null) Log.e(TAG, "Error: windText es NULL");
        if (cloudText == null) Log.e(TAG, "Error: cloudText es NULL");
        if (lastUpdatedText == null) Log.e(TAG, "Error: lastUpdatedText es NULL");
        if (currentTempLarge == null) Log.e(TAG, "Error: currentTempLarge es NULL");
        if (weatherIcon == null) Log.e(TAG, "Error: weatherIcon es NULL");
    }

    private void fetchInstantWeather() {
        Log.d(TAG, "Método fetchInstantWeather() iniciado");

        incrementPendingRequests();
        Log.d(TAG, "incrementPendingRequests() ejecutado");

        WeatherApiService apiService = RetrofitClient.getInstance(requireContext()).create(WeatherApiService.class);
        String token = requireContext()
                .getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
                .getString("TOKEN", null);

        if (token == null || token.isEmpty()) {
            decrementPendingRequests();
            //showToast("Error: no hay token disponible.");
            Log.e(TAG, "Token no disponible");
            return;
        }

        Log.d(TAG, "Realizando solicitud a la API...");

        apiService.getLastInstantWeather("Bearer " + token, "application/json").enqueue(new Callback<InstantWeather>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(Call<InstantWeather> call, Response<InstantWeather> response) {
                Log.d(TAG, "onResponse() ejecutado. Código de respuesta: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    InstantWeather weather = response.body();
                    Log.d(TAG, "Respuesta exitosa. Datos recibidos: " + weather.toString());

                    // Verificar si el fragmento sigue adjunto antes de actualizar la UI
                    if (!isAdded() || getView() == null) {
                        Log.e(TAG, "El fragmento ya no está adjunto. No se puede actualizar la UI.");
                        return;
                    }

                    // Verificar si los valores no son nulos antes de actualizar la UI


                    Log.d(TAG, "weather.getAirTemperature(): " + weather.getAirTemperature());
                    Log.d(TAG, "weather.getRelativeHumidity(): " + weather.getRelativeHumidity());
                    Log.d(TAG, "weather.getAirPressureAtSeaLevel(): " + weather.getAirPressureAtSeaLevel());
                    Log.d(TAG, "weather.getWindSpeed(): " + weather.getWindSpeed());

                    // Actualizar UI en el hilo principal
                    new Handler(Looper.getMainLooper()).post(() -> {
                        float temperature = (float) weather.getAirTemperature();
                        tempText.setText(String.format("Temperatura: %.1f°C", temperature));
                        currentTempLarge.setText(String.format(" %.1f°C", temperature));
                        humidityText.setText(String.format("Humedad: %.1f%%", weather.getRelativeHumidity()));
                        pressureText.setText(String.format("Presión: %.1f hPa", weather.getAirPressureAtSeaLevel()));
                        windText.setText(String.format("Viento: %.1f m/s", weather.getWindSpeed()));
                        cloudText.setText(String.format("Nubosidad: %.1f%%", weather.getCloudAreaFraction()));

                        // Lógica para cambiar el color según la temperatura
                        if (temperature < 10) {
                            // Temperaturas bajas (azul)

                            currentTempLarge.setTextColor(Color.BLUE);

                        } else if (temperature >= 10 && temperature <= 25) {
                            // Temperaturas moderadas (verde)
                            currentTempLarge.setTextColor(Color.GREEN);
                        } else {
                            // Temperaturas altas (rojo)
                            currentTempLarge.setTextColor(Color.RED);
                        }

                        humidityText.setVisibility(View.VISIBLE);
                        pressureText.setVisibility(View.VISIBLE);
                        windText.setVisibility(View.VISIBLE);
                        cloudText.setVisibility(View.VISIBLE);
                        tempText.setVisibility(View.VISIBLE);

                        Log.d(TAG, "Datos actualizados en UI correctamente");
                    });


                    long currentTime = System.currentTimeMillis();
                    lastUpdatedText.setText("Última actualización: " + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(currentTime)));
                    Log.d(TAG, "Última actualización: " + currentTime);
                } else {
                    Log.e(TAG, "Error en la respuesta de la API. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<InstantWeather> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "Error en la solicitud al servidor.", t);
            }
        });
    }

    private void incrementPendingRequests() {
        pendingRequests++;
    }

    private void decrementPendingRequests() {
        pendingRequests--;
    }

    private void scheduleUpdates() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchInstantWeather();
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        }, UPDATE_INTERVAL);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}
