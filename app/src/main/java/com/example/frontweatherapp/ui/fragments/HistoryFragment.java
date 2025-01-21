package com.example.frontweatherapp.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.api.service.WeatherApiService;
import com.example.frontweatherapp.models.WeatherData;
import com.example.frontweatherapp.network.RetrofitClient;
import com.example.frontweatherapp.ui.adapters.WeatherAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_history);
        progressBar = view.findViewById(R.id.progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Llamar al API
        fetchHourlyForecasts();

        return view;
    }

    private void fetchHourlyForecasts() {
        // Mostrar barra de carga
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        // Obtener el token desde SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        String token = preferences.getString("TOKEN", null);

        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Error: Token no disponible", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            return;
        }

        WeatherApiService apiService = RetrofitClient.getInstance(requireContext()).create(WeatherApiService.class);

        apiService.getHourlyForecasts("Bearer " + token).enqueue(new Callback<List<WeatherData>>() {
            @Override
            public void onResponse(@NonNull Call<List<WeatherData>> call, @NonNull Response<List<WeatherData>> response) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    List<WeatherData> forecasts = response.body();
                    WeatherAdapter adapter = new WeatherAdapter(forecasts);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<WeatherData>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
