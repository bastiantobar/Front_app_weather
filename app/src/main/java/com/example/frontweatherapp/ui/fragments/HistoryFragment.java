package com.example.frontweatherapp.ui.fragments;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
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
import com.example.frontweatherapp.models.models.WeatherData;
import com.example.frontweatherapp.network.RetrofitClient;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    private ProgressBar progressBar;
    private ImageView meteogramImageView;
    private LoadingDialogFragment loadingDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        showLoading(true);
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        meteogramImageView = rootView.findViewById(R.id.meteogramImageView);


        fetchHourlyForecasts();
        fetchMeteogram();

        return rootView;
    }

    private void fetchHourlyForecasts() {
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
                    updateCards(forecasts);
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


    private void updateCards(List<WeatherData> forecasts) {
        // Variables para almacenar los valores máximos y mínimos
        double maxTemp = Double.MIN_VALUE;
        double minTemp = Double.MAX_VALUE;
        double maxWindSpeed = Double.MIN_VALUE;
        double minWindSpeed = Double.MAX_VALUE;
        double maxPrecipitation = Double.MIN_VALUE;
        double minPrecipitation = Double.MAX_VALUE;

        // Iterar sobre los datos de los pronósticos
        for (WeatherData data : forecasts) {
            // Calcular las temperaturas máximas y mínimas
            maxTemp = Math.max(maxTemp, data.getAirTemperature());
            minTemp = Math.min(minTemp, data.getAirTemperature());

            // Calcular las velocidades máximas y mínimas del viento
            maxWindSpeed = Math.max(maxWindSpeed, data.getWindSpeed());
            minWindSpeed = Math.min(minWindSpeed, data.getWindSpeed());

            // Calcular las precipitaciones máximas y mínimas
            maxPrecipitation = Math.max(maxPrecipitation, data.getPrecipitationAmount());
            minPrecipitation = Math.min(minPrecipitation, data.getPrecipitationAmount());
        }

        // Actualizar los valores de las tarjetas con los datos calculados
        TextView maxTempValue = getView().findViewById(R.id.maxTempValue);
        TextView minTempValue = getView().findViewById(R.id.minTempValue);
        TextView maxWindSpeedValue = getView().findViewById(R.id.maxWindSpeedValue);
        TextView minWindSpeedValue = getView().findViewById(R.id.minWindSpeedValue);
        TextView maxPrecipitationValue = getView().findViewById(R.id.maxPrecipitationValue);
        TextView minPrecipitationValue = getView().findViewById(R.id.minPrecipitationValue);

        // Mostrar los valores en las tarjetas
        maxTempValue.setText(String.format("%.1f°C", maxTemp));
        minTempValue.setText(String.format("%.1f°C", minTemp));
        maxWindSpeedValue.setText(String.format("%.1f km/h", maxWindSpeed));
        minWindSpeedValue.setText(String.format("%.1f km/h", minWindSpeed));
        maxPrecipitationValue.setText(String.format("%.1f mm", maxPrecipitation));
        minPrecipitationValue.setText(String.format("%.1f mm", minPrecipitation));

    }

    private void fetchMeteogram() {


        WeatherApiService apiService = RetrofitClient.getInstance(requireContext()).create(WeatherApiService.class);
        apiService.getMeteogram("image/svg+xml").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful() && response.body() != null) {
                    try (InputStream inputStream = response.body().byteStream()) {
                        SVG svg = SVG.getFromInputStream(inputStream);
                        PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
                        meteogramImageView.setImageDrawable(drawable);
                        showLoading(false);
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

                Log.e(TAG, "Error en la solicitud al servidor.", t);
                showToast("Error de red al obtener el meteograma.");
            }
        });
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
            loadingDialog.show(getParentFragmentManager(), "loading"); // 🔥 Cambio aquí
        } else {
            if (loadingDialog != null) {
                loadingDialog.dismiss();
            }
        }
    }
}
