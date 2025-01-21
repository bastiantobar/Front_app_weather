package com.example.frontweatherapp.ui.fragments;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.caverock.androidsvg.SVG;
import com.example.frontweatherapp.R;
import com.example.frontweatherapp.api.service.WeatherApiService;
import com.example.frontweatherapp.models.InstantWeather;
import com.example.frontweatherapp.network.RetrofitClient;

import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final int UPDATE_INTERVAL = 10 * 1000; // 10 segundos

   // private static final int UPDATE_INTERVAL = 10 * 60 * 1000; // 10 minutos

    private ImageView meteogramImageView;
    private TextView tempText, humidityText, pressureText, windText, cloudText;
    private ProgressBar progressBar;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int pendingRequests = 0; // Contador de solicitudes pendientes

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Inicializar vistas
        meteogramImageView = rootView.findViewById(R.id.meteogramImageView);
        tempText = rootView.findViewById(R.id.tempText);
        humidityText = rootView.findViewById(R.id.humidityText);
        pressureText = rootView.findViewById(R.id.pressureText);
        windText = rootView.findViewById(R.id.windText);
        cloudText = rootView.findViewById(R.id.cloudText);
        progressBar = rootView.findViewById(R.id.progressBar);

        // Mostrar loading inicialmente
        showLoading(true);

        // Llamar al servicio para obtener el meteograma y los datos instantáneos
        fetchMeteogram();
        fetchInstantWeather();

        // Configurar actualización periódica
        scheduleUpdates();

        return rootView;
    }

    private void fetchMeteogram() {
        incrementPendingRequests();

        WeatherApiService apiService = RetrofitClient.getInstance(requireContext()).create(WeatherApiService.class);
        apiService.getMeteogram("image/svg+xml").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                decrementPendingRequests();
                if (response.isSuccessful() && response.body() != null) {
                    try (InputStream inputStream = response.body().byteStream()) {
                        SVG svg = SVG.getFromInputStream(inputStream);
                        PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
                        meteogramImageView.setImageDrawable(drawable);
                    } catch (Exception e) {
                        Log.e(TAG, "Error al procesar el SVG.", e);
                        showToast("Error al mostrar el gráfico.");
                    }
                } else {
                    Log.e(TAG, "Error en la respuesta del servidor.");
                    showToast("Error al obtener el meteograma.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                decrementPendingRequests();
                Log.e(TAG, "Error en la solicitud al servidor.", t);
                showToast("Error de red al obtener el meteograma.");
            }
        });
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
            @Override
            public void onResponse(Call<InstantWeather> call, Response<InstantWeather> response) {
                decrementPendingRequests();
                if (response.isSuccessful() && response.body() != null) {
                    InstantWeather weather = response.body();

                    // Actualizar los datos del clima en los TextView
                    tempText.setText(String.format("Temperatura: %.1f°C", weather.getAirTemperature()));
                    humidityText.setText(String.format("Humedad: %.1f%%", weather.getRelativeHumidity()));
                    pressureText.setText(String.format("Presión: %.1f hPa", weather.getAirPressureAtSeaLevel()));
                    windText.setText(String.format("Viento: %.1f m/s", weather.getWindSpeed()));
                    cloudText.setText(String.format("Nubosidad: %.1f%%", weather.getCloudAreaFraction()));

                    // Mostrar mensaje de éxito
                    showToast("Información actualizada correctamente");
                } else {
                    showToast("Error al obtener el clima instantáneo.");
                }
            }


            @Override
            public void onFailure(Call<InstantWeather> call, Throwable t) {
                decrementPendingRequests();
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
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);

        int visibility = isLoading ? View.GONE : View.VISIBLE;
        tempText.setVisibility(visibility);
        humidityText.setVisibility(visibility);
        pressureText.setVisibility(visibility);
        windText.setVisibility(visibility);
        cloudText.setVisibility(visibility);

        // El gráfico siempre será visible
        meteogramImageView.setVisibility(View.VISIBLE);
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
