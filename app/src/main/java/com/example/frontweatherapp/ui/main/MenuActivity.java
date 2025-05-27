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
import com.example.frontweatherapp.models.WeatherResponse; // Importar WeatherResponse
import com.example.frontweatherapp.ui.fragments.AirQualityFragment; // Importar AirQualityFragment
import com.example.frontweatherapp.ui.fragments.ForecastFragment;
import com.example.frontweatherapp.ui.fragments.GraficFragment;
import com.example.frontweatherapp.ui.fragments.HistoryFragment;
import com.example.frontweatherapp.ui.fragments.HomeFragment;
import com.example.frontweatherapp.ui.fragments.MapFragment;
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

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_profile) { // CORREGIDO: Usar nav_profile para Mapa
                selectedFragment = new MapFragment();
                // Si tienes un objeto WindMap dentro de fullWeatherData, pásalo aquí
                // if (fullWeatherData != null && fullWeatherData.getWindMap() != null) {
                //     Bundle args = new Bundle();
                //     args.putSerializable("windMap", fullWeatherData.getWindMap());
                //     selectedFragment.setArguments(args);
                // } else {
                //     Toast.makeText(this, "Datos del mapa no disponibles. Intente refrescar la pantalla de inicio.", Toast.LENGTH_SHORT).show();
                // }
            } else if (itemId == R.id.nav_settings) { // CORREGIDO: Usar nav_settings para Pronóstico
                selectedFragment = new ForecastFragment();
                if (fullWeatherData != null && fullWeatherData.getHourlyForecasts() != null) {
                    Bundle args = new Bundle();
                    args.putSerializable("hourlyForecasts", (Serializable) fullWeatherData.getHourlyForecasts());
                    selectedFragment.setArguments(args);
                } else {
                    Toast.makeText(this, "Pronóstico horario no disponible. Intente refrescar la pantalla de inicio.", Toast.LENGTH_SHORT).show();
                }
            } else if (itemId == R.id.nav_graficos) {
                selectedFragment = new GraficFragment();
                if (fullWeatherData != null && fullWeatherData.getHourlyForecasts() != null) {
                    Bundle args = new Bundle();
                    args.putSerializable("hourlyForecasts", (Serializable) fullWeatherData.getHourlyForecasts());
                    selectedFragment.setArguments(args);
                } else {
                    Toast.makeText(this, "Datos para gráficos no disponibles. Intente refrescar la pantalla de inicio.", Toast.LENGTH_SHORT).show();
                }
            } else if (itemId == R.id.nav_notifications) {
                selectedFragment = new NotificationFragment();
            } else if (itemId == R.id.nav_air_quality) { // Manejar la nueva opción de Calidad del Aire
                selectedFragment = new AirQualityFragment();
                if (fullWeatherData != null && fullWeatherData.getAirQuality() != null) {
                    Bundle args = new Bundle();
                    args.putSerializable("fullWeatherData", fullWeatherData); // Pasar el WeatherResponse completo
                    selectedFragment.setArguments(args);
                } else {
                    Toast.makeText(this, "Datos de calidad del aire no disponibles. Intente refrescar la pantalla de inicio.", Toast.LENGTH_SHORT).show();
                }
            } else if (itemId == R.id.action_logout) {
                cerrarSesion();
                return true;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, selectedFragment)
                        .commit();
                drawerLayout.closeDrawer(navigationView);
            }
            return true;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, new HomeFragment())
                    .commit();
        }
    }

    @Override
    public void onWeatherResponseReceived(WeatherResponse weatherResponse) {
        this.fullWeatherData = weatherResponse;
        Log.d(TAG, "WeatherResponse completo recibido y almacenado en MenuActivity.");
    }

    private void cerrarSesion() {
        Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show();

        getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                .edit()
                .remove("auth_token")
                .apply();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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
