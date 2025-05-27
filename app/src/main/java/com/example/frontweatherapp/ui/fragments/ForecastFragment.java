package com.example.frontweatherapp.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.api.service.WeatherApiService;
import com.example.frontweatherapp.adapters.HourlyForecastAdapter; // Importar el adaptador
import com.example.frontweatherapp.models.WeatherResponse; // Importar la clase de respuesta completa
import com.example.frontweatherapp.models.HourlyForecast; // Importar la clase de pronóstico horario
import com.example.frontweatherapp.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastFragment extends Fragment {

    private static final String TAG = "ForecastFragment";
    private RecyclerView hourlyForecastRecyclerView;
    private HourlyForecastAdapter adapter;
    private List<HourlyForecast> hourlyForecastList;
    private LoadingDialogFragment loadingDialog;
    private TextView forecastTitle; // Título para mostrar la ubicación

    private String currentAddress = "Hijuelas, Chile"; // Dirección por defecto

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);
        showLoading(true); // Mostrar el diálogo de carga

        forecastTitle = rootView.findViewById(R.id.forecastTitle);
        hourlyForecastRecyclerView = rootView.findViewById(R.id.hourlyForecastRecyclerView);

        hourlyForecastList = new ArrayList<>();
        adapter = new HourlyForecastAdapter(hourlyForecastList);
        hourlyForecastRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        hourlyForecastRecyclerView.setAdapter(adapter);

        // Intentar obtener la dirección de los argumentos del Bundle
        if (getArguments() != null) {
            String address = getArguments().getString("addressQuery");
            if (address != null && !address.isEmpty()) {
                currentAddress = address; // Usar la dirección pasada
                forecastTitle.setText(String.format("Pronóstico Horario para %s", address));
            }
        } else {
            // Si no se pasó ninguna dirección, usar la predeterminada
            forecastTitle.setText(String.format("Pronóstico Horario para %s", currentAddress));
        }

        // Intentar obtener los pronósticos horarios directamente del Bundle
        if (getArguments() != null && getArguments().containsKey("hourlyForecasts")) {
            List<HourlyForecast> forecastsFromBundle = (List<HourlyForecast>) getArguments().getSerializable("hourlyForecasts");
            if (forecastsFromBundle != null && !forecastsFromBundle.isEmpty()) {
                adapter.updateData(forecastsFromBundle);
                Log.d(TAG, "Pronósticos horarios cargados desde Bundle.");
                showLoading(false); // Ocultar carga si los datos ya están en el Bundle
                return rootView; // Salir, ya que los datos se cargaron
            }
        }

        // Si no hay datos en el Bundle, hacer una llamada a la API
        fetchHourlyForecasts();

        return rootView;
    }

    /**
     * Realiza la llamada a la API para obtener los pronósticos horarios.
     * Se llama si los datos no están disponibles en el Bundle.
     */
    private void fetchHourlyForecasts() {
        Log.d(TAG, "Método fetchHourlyForecasts() iniciado para: " + currentAddress);

        SharedPreferences preferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String token = preferences.getString("auth_token", null); // Asegúrate de que el token se guarda con esta clave

        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Error: Token de autenticación no disponible.", Toast.LENGTH_SHORT).show();
            showLoading(false);
            return;
        }

        WeatherApiService apiService = RetrofitClient.getInstance(requireContext()).create(WeatherApiService.class);

        // Usar el método getWeatherData que devuelve WeatherResponse
        apiService.getWeatherData("Bearer " + token, "application/json", currentAddress).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    List<HourlyForecast> forecasts = weatherResponse.getHourlyForecasts();

                    // Asegurarse de que el fragmento sigue adjunto antes de actualizar la UI
                    if (!isAdded() || getView() == null) {
                        Log.e(TAG, "El fragmento ya no está adjunto. No se puede actualizar la UI.");
                        return;
                    }

                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (forecasts != null && !forecasts.isEmpty()) {
                            adapter.updateData(forecasts);
                            Log.d(TAG, "Pronósticos horarios actualizados en UI correctamente.");
                        } else {
                            Log.d(TAG, "No se recibieron pronósticos horarios.");
                            Toast.makeText(getContext(), "No hay pronósticos horarios disponibles.", Toast.LENGTH_SHORT).show();
                        }
                        showLoading(false); // Ocultar carga
                    });

                } else {
                    Log.e(TAG, "Error al obtener los datos del pronóstico. Código: " + response.code() + ", Mensaje: " + response.message());
                    Toast.makeText(getContext(), "Error al obtener pronóstico: " + response.message(), Toast.LENGTH_SHORT).show();
                    showLoading(false); // Ocultar carga en caso de error
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error de red al obtener el pronóstico.", t);
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                showLoading(false); // Ocultar carga en caso de fallo de red
            }
        });
    }

    /**
     * Muestra u oculta un diálogo de carga.
     * @param show true para mostrar el diálogo, false para ocultarlo.
     */
    public void showLoading(boolean show) {
        if (show) {
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialogFragment();
            }
            if (isAdded()) { // Asegurarse de que el fragmento esté adjunto
                loadingDialog.show(getParentFragmentManager(), "loading");
            }
        } else {
            if (loadingDialog != null && loadingDialog.isAdded()) { // Comprobar si está adjunto antes de dismiss
                loadingDialog.dismiss();
            }
        }
    }
}
