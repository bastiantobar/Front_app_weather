package com.example.frontweatherapp.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.PictureDrawable;
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
    private ImageView weatherIcon; // Declaramos ImageView globalmente
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int pendingRequests = 0; // Contador de solicitudes pendientes
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
        currentTempLarge = rootView.findViewById(R.id.currentTempLarge);  // Temperature large
        weatherIcon = rootView.findViewById(R.id.weatherIcon);  // Icono dinámico
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);

        // Configura los colores de la animación del SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );

        // Configurar el listener para el SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Llamar a la función de actualización
            fetchInstantWeather();
        });

        // Llamar al servicio para obtener los datos del clima
        fetchInstantWeather();

        // Configurar actualización periódica
        scheduleUpdates();

        return rootView;
    }

    private void fetchInstantWeather() {
        incrementPendingRequests();

        WeatherApiService apiService = RetrofitClient.getInstance(requireContext()).create(WeatherApiService.class);
        String token = requireContext()
                .getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
                .getString("TOKEN", null);

        if (token == null || token.isEmpty()) {
            decrementPendingRequests();
            showToast("Error: no hay token disponible.");
            return;
        }

        apiService.getLastInstantWeather("Bearer " + token, "application/json").enqueue(new Callback<InstantWeather>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(Call<InstantWeather> call, Response<InstantWeather> response) {

                if (response.isSuccessful() && response.body() != null) {
                    InstantWeather weather = response.body();

                    // Actualizar los datos del clima
                    tempText.setText(String.format("Temperatura: %.1f°C", weather.getAirTemperature()));
                    humidityText.setText(String.format("Humedad: %.1f%%", weather.getRelativeHumidity()));
                    pressureText.setText(String.format("Presión: %.1f hPa", weather.getAirPressureAtSeaLevel()));
                    windText.setText(String.format("Viento: %.1f m/s", weather.getWindSpeed()));
                    cloudText.setText(String.format("Nubosidad: %.1f%%", weather.getCloudAreaFraction()));

                    // Actualizar la temperatura grande y su color
                    float temperature = (float) weather.getAirTemperature();
                    currentTempLarge.setText(String.format("%.1f°C", temperature));

                    // Cambiar el color de la temperatura en función del valor
                    if (temperature > 30) {
                        currentTempLarge.setTextColor(getResources().getColor(R.color.hotTemperatureColor)); // Color rojo o cálido
                        weatherIcon.setImageResource(R.drawable.ic_sun);  // Icono soleado
                    } else if (temperature > 20) {
                        currentTempLarge.setTextColor(getResources().getColor(R.color.warmTemperatureColor)); // Color anaranjado
                        weatherIcon.setImageResource(R.drawable.ic_cloud); // Icono nublado
                    } else {
                        currentTempLarge.setTextColor(getResources().getColor(R.color.coldTemperatureColor)); // Color azul o frío
                        weatherIcon.setImageResource(R.drawable.ic_rain); // Icono lluvioso
                    }

                    // Actualizar la hora de última actualización
                    long currentTime = System.currentTimeMillis();
                    lastUpdatedText.setText("Última actualización: " + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(currentTime)));

                    // Mostrar mensaje de éxito
                    showToast("Información actualizada correctamente");
                } else {
                    showToast("Error al obtener el clima instantáneo.");
                }
            }

            @Override
            public void onFailure(Call<InstantWeather> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "Error en la solicitud al servidor.", t);
                showToast("Error de red al obtener el clima instantáneo.");
            }
        });
    }

    private void incrementPendingRequests() {
        pendingRequests++;
        showLoading(true);
    }

    private void decrementPendingRequests() {
        pendingRequests--;
        if (pendingRequests == 0) {
            showLoading(false);
        }
    }

    private void scheduleUpdates() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchInstantWeather(); // Actualizar solo los TextView
                showToast("Datos de clima actualizados"); // Mostrar mensaje de actualización
                handler.postDelayed(this, UPDATE_INTERVAL); // Reprogramar la actualización
            }
        }, UPDATE_INTERVAL);
    }

    private void showLoading(boolean isLoading) {
        int visibility = isLoading ? View.GONE : View.VISIBLE;
        tempText.setVisibility(visibility);
        humidityText.setVisibility(visibility);
        pressureText.setVisibility(visibility);
        windText.setVisibility(visibility);
        cloudText.setVisibility(visibility);
        lastUpdatedText.setVisibility(visibility);
    }

    private void showToast(String message) {
        if (getActivity() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null); // Detener actualizaciones automáticas
    }
}
