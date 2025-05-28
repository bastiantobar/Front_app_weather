package com.example.frontweatherapp.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.models.WeatherResponse;
import com.example.frontweatherapp.models.AstronomicalTimes;
import com.example.frontweatherapp.models.Location;
import com.example.frontweatherapp.models.NasaApod;

import com.example.frontweatherapp.ui.fragments.AirQualityFragment;
import com.example.frontweatherapp.ui.fragments.AstronomicalDataFragment;
import com.example.frontweatherapp.ui.fragments.ForecastFragment;
import com.example.frontweatherapp.ui.fragments.GraficFragment;
import com.example.frontweatherapp.ui.fragments.HistoryFragment;
import com.example.frontweatherapp.ui.fragments.HomeFragment;
import com.example.frontweatherapp.ui.fragments.MapFragment;
import com.example.frontweatherapp.ui.fragments.NasaApodFragment;
import com.example.frontweatherapp.ui.fragments.NotificationFragment;

import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;

public class MenuActivity extends AppCompatActivity implements HomeFragment.OnWeatherResponseReceivedListener {

    private static final String TAG = "MenuActivity";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private WeatherResponse fullWeatherData; // Variable para almacenar el WeatherResponse completo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ImageView backgroundImage = findViewById(R.id.background_image);

        if (isNightMode()) {
            backgroundImage.setImageResource(R.drawable.bg_night);
        } else {
            backgroundImage.setImageResource(R.drawable.bg_day);
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            Log.d(TAG, "onNavigationItemSelected: Item seleccionado: " + item.getTitle() + " (ID: " + itemId + ")");

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
                Log.d(TAG, "onNavigationItemSelected: Cargando HomeFragment.");
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new MapFragment();
                if (fullWeatherData != null && fullWeatherData.getWindMap() != null) {
                    Bundle args = new Bundle();
                    args.putSerializable("windMap", fullWeatherData.getWindMap());
                    selectedFragment.setArguments(args);
                    Log.d(TAG, "onNavigationItemSelected: Cargando MapFragment con datos de viento.");
                } else {
                    Toast.makeText(this, "Datos del mapa no disponibles. Intente refrescar la pantalla de inicio.", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "onNavigationItemSelected: Datos de mapa no disponibles. Mostrando Toast.");
                    selectedFragment = new HomeFragment(); // Fallback
                }
            } else if (itemId == R.id.nav_settings) {
                selectedFragment = new ForecastFragment();
                if (fullWeatherData != null && fullWeatherData.getHourlyForecasts() != null) {
                    Bundle args = new Bundle();
                    args.putSerializable("hourlyForecasts", (Serializable) fullWeatherData.getHourlyForecasts());
                    selectedFragment.setArguments(args);
                    Log.d(TAG, "onNavigationItemSelected: Cargando ForecastFragment con pronóstico horario.");
                } else {
                    Toast.makeText(this, "Pronóstico horario no disponible. Intente refrescar la pantalla de inicio.", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "onNavigationItemSelected: Pronóstico horario no disponible. Mostrando Toast.");
                    selectedFragment = new HomeFragment(); // Fallback
                }
            } else if (itemId == R.id.nav_graficos) {
                selectedFragment = new GraficFragment();
                if (fullWeatherData != null && fullWeatherData.getHourlyForecasts() != null) {
                    Bundle args = new Bundle();
                    args.putSerializable("hourlyForecasts", (Serializable) fullWeatherData.getHourlyForecasts());
                    selectedFragment.setArguments(args);
                    Log.d(TAG, "onNavigationItemSelected: Cargando GraficFragment con datos de pronóstico.");
                } else {
                    Toast.makeText(this, "Datos para gráficos no disponibles. Intente refrescar la pantalla de inicio.", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "onNavigationItemSelected: Datos para gráficos no disponibles. Mostrando Toast.");
                    selectedFragment = new HomeFragment(); // Fallback
                }
            } else if (itemId == R.id.nav_notifications) {
                selectedFragment = new NotificationFragment();
                Log.d(TAG, "onNavigationItemSelected: Cargando NotificationFragment.");
            } else if (itemId == R.id.nav_air_quality) {
                selectedFragment = new AirQualityFragment();
                if (fullWeatherData != null && fullWeatherData.getAirQuality() != null) {
                    Bundle args = new Bundle();
                    args.putSerializable("fullWeatherData", fullWeatherData);
                    selectedFragment.setArguments(args);
                    Log.d(TAG, "onNavigationItemSelected: Cargando AirQualityFragment con datos de calidad del aire.");
                } else {
                    Toast.makeText(this, "Datos de calidad del aire no disponibles. Intente refrescar la pantalla de inicio.", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "onNavigationItemSelected: Datos de calidad del aire no disponibles. Mostrando Toast.");
                    selectedFragment = new HomeFragment(); // Fallback
                }
            } else if (itemId == R.id.nav_astronomical_data) { // Lógica para Datos Astronómicos
                Log.d(TAG, "onNavigationItemSelected: Intentando cargar AstronomicalDataFragment.");
                Log.d(TAG, "onNavigationItemSelected: fullWeatherData es " + (fullWeatherData != null ? "NO nulo" : "nulo"));
                if (fullWeatherData != null) {
                    Log.d(TAG, "onNavigationItemSelected: fullWeatherData.getAstronomicalTimes() es " + (fullWeatherData.getAstronomicalTimes() != null ? "NO nulo" : "nulo"));
                }

                if (fullWeatherData != null && fullWeatherData.getAstronomicalTimes() != null) {
                    String locationName = (fullWeatherData.getLocation() != null && fullWeatherData.getLocation().getName() != null) ?
                            fullWeatherData.getLocation().getName() : "Ubicación desconocida";
                    selectedFragment = AstronomicalDataFragment.newInstance(fullWeatherData.getAstronomicalTimes(), locationName);
                    Log.d(TAG, "onNavigationItemSelected: Cargando AstronomicalDataFragment con datos.");
                } else {
                    Toast.makeText(this, "Datos astronómicos no disponibles. Intente refrescar la pantalla de inicio.", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "onNavigationItemSelected: Datos astronómicos no disponibles. Mostrando Toast y cargando HomeFragment.");
                    selectedFragment = new HomeFragment(); // Fallback a HomeFragment si los datos no están
                }
            } else if (itemId == R.id.nav_nasa_apod) { // Lógica para NASA APOD
                Log.d(TAG, "onNavigationItemSelected: Intentando cargar NasaApodFragment.");
                Log.d(TAG, "onNavigationItemSelected: fullWeatherData es " + (fullWeatherData != null ? "NO nulo" : "nulo"));
                if (fullWeatherData != null) {
                    Log.d(TAG, "onNavigationItemSelected: fullWeatherData.getNasaApod() es " + (fullWeatherData.getNasaApod() != null ? "NO nulo" : "nulo"));
                }

                // Modificación aquí: Simplificamos la validación
                if (fullWeatherData != null && fullWeatherData.getNasaApod() != null && fullWeatherData.getNasaApod().getUrl() != null) {
                    NasaApod nasaApodData = fullWeatherData.getNasaApod();
                    Bundle args = new Bundle();
                    args.putSerializable("nasaApodData", nasaApodData);
                    selectedFragment = new NasaApodFragment();
                    selectedFragment.setArguments(args);
                    Log.d(TAG, "onNavigationItemSelected: Cargando NasaApodFragment con APOD data (URL presente).");
                } else {
                    Toast.makeText(this, "Imagen Astronómica del Día no disponible (sin URL o datos). Intente refrescar la pantalla de inicio.", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "onNavigationItemSelected: Datos de APOD no disponibles o URL nula. Mostrando Toast y cargando HomeFragment.");
                    selectedFragment = new HomeFragment(); // Fallback
                }
            } else if (itemId == R.id.action_logout) {
                cerrarSesion();
                return true;
            }

            if (selectedFragment != null) {
                Log.d(TAG, "onNavigationItemSelected: Reemplazando fragmento con: " + selectedFragment.getClass().getSimpleName());
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, selectedFragment)
                        .commit();
                drawerLayout.closeDrawer(navigationView);
            } else {
                Log.w(TAG, "onNavigationItemSelected: selectedFragment es nulo. No se realizó ninguna transacción de fragmentos.");
            }
            return true;
        });

        if (savedInstanceState == null) {
            Log.d(TAG, "onCreate: savedInstanceState es nulo. Cargando HomeFragment inicial.");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, new HomeFragment())
                    .commit();
        }
    }

    @Override
    public void onWeatherResponseReceived(WeatherResponse weatherResponse) {
        this.fullWeatherData = weatherResponse;
        Log.d(TAG, "onWeatherResponseReceived: WeatherResponse completo recibido y almacenado en MenuActivity.");
        // Opcional: Si quieres forzar una recarga del fragmento actual si es el de datos astronómicos,
        // podrías hacerlo aquí, pero primero asegúrate de que los datos se cargan correctamente.
        // Por ahora, solo nos aseguramos de que 'fullWeatherData' se almacene.
    }

    private void cerrarSesion() {
        Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "cerrarSesion: Iniciando cierre de sesión.");

        getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                .edit()
                .remove("auth_token")
                .apply();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        Log.d(TAG, "cerrarSesion: Sesión cerrada, redirigiendo a MainActivity.");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            cerrarSesion();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isNightMode() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
}
