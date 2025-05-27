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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.api.service.WeatherApiService;
import com.example.frontweatherapp.models.WeatherResponse;
import com.example.frontweatherapp.network.RetrofitClient;
import android.icu.util.Calendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final int UPDATE_INTERVAL = 10 * 1000; // 10 segundos
    private LoadingDialogFragment loadingDialog;
    private TextView tempText, humidityText, pressureText, windText, cloudText, lastUpdatedText, currentTempLarge;
    private TextView locationNameTextView;
    private ImageView weatherIcon;
    private EditText addressInputEditText;
    private Button fetchWeatherButton;
    private Button viewForecastButton;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private SwipeRefreshLayout swipeRefreshLayout;

    private String lastSearchedAddress = "Hijuelas, Chile"; // Dirección por defecto

    // Interfaz para comunicar el WeatherResponse a la actividad
    public interface OnWeatherResponseReceivedListener {
        void onWeatherResponseReceived(WeatherResponse weatherResponse);
    }

    private OnWeatherResponseReceivedListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnWeatherResponseReceivedListener) {
            listener = (OnWeatherResponseReceivedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWeatherResponseReceivedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        showLoading(true);

        // Inicializar vistas
        locationNameTextView = rootView.findViewById(R.id.locationNameTextView);
        tempText = rootView.findViewById(R.id.tempText);
        humidityText = rootView.findViewById(R.id.humidityText);
        pressureText = rootView.findViewById(R.id.pressureText);
        windText = rootView.findViewById(R.id.windText);
        cloudText = rootView.findViewById(R.id.cloudText);
        lastUpdatedText = rootView.findViewById(R.id.lastUpdatedText);
        currentTempLarge = rootView.findViewById(R.id.currentTempLarge);
        weatherIcon = rootView.findViewById(R.id.weatherIcon);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        addressInputEditText = rootView.findViewById(R.id.addressInputEditText);
        fetchWeatherButton = rootView.findViewById(R.id.fetchWeatherButton);
        viewForecastButton = rootView.findViewById(R.id.viewForecastButton);

        Log.d(TAG, "Vistas inicializadas correctamente");

        checkViewsInitialized();

        addressInputEditText.setText(lastSearchedAddress);

        // Listener para el botón de búsqueda
        fetchWeatherButton.setOnClickListener(v -> {
            String addressQuery = addressInputEditText.getText().toString().trim();
            if (!addressQuery.isEmpty()) {
                lastSearchedAddress = addressQuery;
                showLoading(true);
                fetchWeatherData(addressQuery);
                hideKeyboard(v);
            } else {
                Log.d(TAG, "El campo de dirección está vacío. No se realiza la búsqueda.");
                Toast.makeText(requireContext(), "Por favor, ingresa una dirección", Toast.LENGTH_SHORT).show();
            }
        });

        // Listener para el nuevo botón "Ver Pronóstico Horario"
        viewForecastButton.setOnClickListener(v -> {
            ForecastFragment forecastFragment = new ForecastFragment();
            Bundle args = new Bundle();
            args.putString("addressQuery", lastSearchedAddress);
            // También puedes pasar el objeto WeatherResponse completo si ya lo tienes
            // if (fullWeatherData != null) { args.putSerializable("fullWeatherData", fullWeatherData); }
            forecastFragment.setArguments(args);

            // Reemplaza R.id.fragment_container con el ID real de tu FrameLayout o contenedor de fragmentos en tu Activity principal
            // Si estás en MenuActivity, el ID es R.id.nav_host_fragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, forecastFragment) // Usar nav_host_fragment
                    .addToBackStack(null)
                    .commit();
        });


        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchWeatherData(lastSearchedAddress);
            swipeRefreshLayout.setRefreshing(false);
        });

        fetchWeatherData(lastSearchedAddress);
        scheduleUpdates();

        return rootView;
    }

    private void checkViewsInitialized() {
        if (locationNameTextView == null) Log.e(TAG, "Error: locationNameTextView es NULL");
        if (tempText == null) Log.e(TAG, "Error: tempText es NULL");
        if (humidityText == null) Log.e(TAG, "Error: humidityText es NULL");
        if (pressureText == null) Log.e(TAG, "Error: pressureText es NULL");
        if (windText == null) Log.e(TAG, "Error: windText es NULL");
        if (cloudText == null) Log.e(TAG, "Error: cloudText es NULL");
        if (lastUpdatedText == null) Log.e(TAG, "Error: lastUpdatedText es NULL");
        if (currentTempLarge == null) Log.e(TAG, "Error: currentTempLarge es NULL");
        if (weatherIcon == null) Log.e(TAG, "Error: weatherIcon es NULL");
        if (swipeRefreshLayout == null) Log.e(TAG, "Error: swipeRefreshLayout es NULL");
        if (addressInputEditText == null) Log.e(TAG, "Error: addressInputEditText es NULL");
        if (fetchWeatherButton == null) Log.e(TAG, "Error: fetchWeatherButton es NULL");
        if (viewForecastButton == null) Log.e(TAG, "Error: viewForecastButton es NULL");
    }

    private void fetchWeatherData(String addressQuery) {
        Log.d(TAG, "Método fetchWeatherData() iniciado para: " + addressQuery);

        WeatherApiService apiService = RetrofitClient.getInstance(requireContext()).create(WeatherApiService.class);
        String token = requireContext()
                .getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
                .getString("TOKEN", null);
        Log.e(TAG, "Token" + token);

        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token no disponible");
            showLoading(false);
            Toast.makeText(requireContext(), "Error: Token de autenticación no disponible.", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "Realizando solicitud a la API para: " + addressQuery);

        apiService.getWeatherData("Bearer " + token, "application/json", addressQuery).enqueue(new Callback<WeatherResponse>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                Log.d(TAG, "onResponse() ejecutado. Código de respuesta: " + response.code());
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    Log.d(TAG, "Respuesta exitosa. Datos recibidos: " + weatherResponse.toString());

                    if (!isAdded() || getView() == null) {
                        Log.e(TAG, "El fragmento ya no está adjunto. No se puede actualizar la UI.");
                        return;
                    }

                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (weatherResponse.getLocation() != null) {
                            locationNameTextView.setText(weatherResponse.getLocation().getName());
                        } else {
                            locationNameTextView.setText("Ubicación desconocida");
                        }

                        if (weatherResponse.getCurrentWeather() != null) {
                            double temperature = weatherResponse.getCurrentWeather().getAirTemperature();
                            double humidity = weatherResponse.getCurrentWeather().getRelativeHumidity();
                            double pressure = weatherResponse.getCurrentWeather().getAirPressureAtSeaLevel();
                            double wind = weatherResponse.getCurrentWeather().getWindSpeed();
                            double cloudAreaFraction = weatherResponse.getCurrentWeather().getCloudAreaFraction();

                            tempText.setText(String.format("Temperatura: %.1f°C", temperature));
                            currentTempLarge.setText(String.format(" %.1f°C", temperature));
                            humidityText.setText(String.format("Humedad: %.1f%%", humidity));
                            pressureText.setText(String.format("Presión: %.1f hPa", pressure));
                            windText.setText(String.format("Viento: %.1f m/s", wind));
                            cloudText.setText(String.format("Nubosidad: %.1f%%", cloudAreaFraction));

                            updateWeatherIconAndColor(temperature, cloudAreaFraction);
                        } else {
                            Log.e(TAG, "CurrentWeather object is null. No se pueden mostrar los datos del clima actual.");
                            Toast.makeText(requireContext(), "No se pudieron obtener los datos del clima actual.", Toast.LENGTH_LONG).show();
                        }

                        humidityText.setVisibility(View.VISIBLE);
                        pressureText.setVisibility(View.VISIBLE);
                        windText.setVisibility(View.VISIBLE);
                        cloudText.setVisibility(View.VISIBLE);
                        tempText.setVisibility(View.VISIBLE);
                        showLoading(false);

                        Log.d(TAG, "Datos actualizados en UI correctamente");
                    });

                    long currentTime = System.currentTimeMillis();
                    lastUpdatedText.setText("Última actualización: " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(currentTime)));
                    Log.d(TAG, "Última actualización: " + currentTime);

                    // Notificar a la actividad que el WeatherResponse está disponible
                    if (listener != null) {
                        listener.onWeatherResponseReceived(weatherResponse);
                    }

                } else {
                    Log.e(TAG, "Error en la respuesta de la API. Código: " + response.code() + ", Mensaje: " + response.message());
                    showLoading(false);
                    Toast.makeText(requireContext(), "Error al obtener datos del clima: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                showLoading(false);
                Log.e(TAG, "Error en la solicitud al servidor.", t);
                Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateWeatherIconAndColor(double temperature, double cloudAreaFraction) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 6 && hour < 18) {
            if (cloudAreaFraction < 25) {
                weatherIcon.setImageResource(R.drawable.ic_sun);
                weatherIcon.setColorFilter(Color.YELLOW);
            } else if (cloudAreaFraction >= 25 && cloudAreaFraction <= 75) {
                weatherIcon.setImageResource(R.drawable.ic_partly_cloudy);
                weatherIcon.setColorFilter(null);
            } else {
                weatherIcon.setImageResource(R.drawable.ic_cloudy);
                weatherIcon.setColorFilter(null);
            }
        } else {
            if (cloudAreaFraction < 25) {
                weatherIcon.setImageResource(R.drawable.ic_moon);
                weatherIcon.setColorFilter(Color.CYAN);
            } else if (cloudAreaFraction >= 25 && cloudAreaFraction <= 75) {
                weatherIcon.setImageResource(R.drawable.ic_partly_cloudy);
                weatherIcon.setColorFilter(null);
            } else {
                weatherIcon.setImageResource(R.drawable.ic_cloudy);
                weatherIcon.setColorFilter(null);
            }
        }

        if (temperature < 10) {
            currentTempLarge.setTextColor(Color.BLUE);
        } else if (temperature >= 10 && temperature <= 25) {
            currentTempLarge.setTextColor(Color.GREEN);
        } else {
            currentTempLarge.setTextColor(Color.RED);
        }
    }

    private void scheduleUpdates() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchWeatherData(lastSearchedAddress);
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        }, UPDATE_INTERVAL);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
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

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
