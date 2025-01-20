package com.example.frontweatherapp.ui.fragments;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.os.Handler;
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
    private static final int UPDATE_INTERVAL = 10 * 60 * 1000; // 10 minutos
    private ImageView meteogramImageView;
    private TextView weatherInfo;
    private final Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Inicializar vistas
        meteogramImageView = rootView.findViewById(R.id.meteogramImageView);
        weatherInfo = rootView.findViewById(R.id.weatherInfo);

        // Llamar al servicio para obtener el meteograma y datos instantáneos
        fetchMeteogram();
        fetchInstantWeather();

        // Configurar actualización periódica
        startWeatherUpdates();

        return rootView;
    }

    private void fetchMeteogram() {
        Log.d(TAG, "Iniciando la solicitud para obtener el meteograma.");
        WeatherApiService apiService = RetrofitClient.getInstance(requireContext()).create(WeatherApiService.class);

        apiService.getMeteogram("image/svg+xml").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "Respuesta recibida del servidor. Código: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    try (InputStream inputStream = response.body().byteStream()) {
                        Log.d(TAG, "Procesando el archivo SVG.");

                        // Procesar el SVG
                        SVG svg = SVG.getFromInputStream(inputStream);
                        PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());

                        meteogramImageView.setImageDrawable(drawable);
                        Log.d(TAG, "SVG procesado y renderizado correctamente.");

                    } catch (Exception e) {
                        Log.e(TAG, "Error al procesar el SVG.", e);
                        showToast("Error al mostrar el gráfico.");
                    }
                } else {
                    Log.e(TAG, "Error en la respuesta del servidor. Código: " + response.code());
                    showToast("Error al obtener el meteograma.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Error en la solicitud al servidor.", t);
                showToast("Error de red al obtener el meteograma.");
            }
        });
    }

    private void fetchInstantWeather() {
        Log.d(TAG, "Iniciando la solicitud para obtener el último clima instantáneo.");
        WeatherApiService apiService = RetrofitClient.getInstance(requireContext()).create(WeatherApiService.class);

        // Recuperar el token de SharedPreferences
        String token = requireContext()
                .getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
                .getString("TOKEN", null);

        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token no disponible. No se puede obtener el clima instantáneo.");
            showToast("Error: no hay token disponible.");
            return;
        }

        apiService.getLastInstantWeather("Bearer " + token, "application/json").enqueue(new Callback<InstantWeather>() {
            @Override
            public void onResponse(Call<InstantWeather> call, Response<InstantWeather> response) {
                if (response.isSuccessful() && response.body() != null) {
                    InstantWeather weather = response.body();
                    String info = String.format(
                            "Temperatura: %.1f°C\nHumedad: %.1f%%\nPresión: %.1f hPa\nViento: %.1f m/s\nNubosidad: %.1f%%",
                            weather.getAirTemperature(),
                            weather.getRelativeHumidity(),
                            weather.getAirPressureAtSeaLevel(),
                            weather.getWindSpeed(),
                            weather.getCloudAreaFraction()
                    );
                    weatherInfo.setText(info);
                    Log.d(TAG, "Datos de clima actualizados: " + info);
                } else {
                    Log.e(TAG, "Error en la respuesta del servidor. Código: " + response.code());
                    showToast("Error al obtener el clima instantáneo.");
                }
            }

            @Override
            public void onFailure(Call<InstantWeather> call, Throwable t) {
                Log.e(TAG, "Error en la solicitud al servidor.", t);
                showToast("Error de red al obtener el clima instantáneo.");
            }
        });
    }



    private void startWeatherUpdates() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Actualización periódica del clima.");
                //fetchInstantWeather();
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        }, 0);
    }

    private void showToast(String message) {
        if (getActivity() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null); // Detener las actualizaciones cuando se destruya la vista
    }
}
