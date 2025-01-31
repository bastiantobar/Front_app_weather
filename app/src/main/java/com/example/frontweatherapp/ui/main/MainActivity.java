package com.example.frontweatherapp.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.ui.fragments.LoginFragment;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Log.d(TAG, "Permiso POST_NOTIFICATIONS concedido.");
                        subscribeToWeatherAlerts();
                    } else {
                        Log.e(TAG, "Permiso POST_NOTIFICATIONS denegado.");
                    }
                }
        );

        checkAndRequestNotificationPermission();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment()) // Cargar LoginFragment
                    .commit();
        }
    }

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Solicitar permiso de notificaciones
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                Log.d(TAG, "Permiso POST_NOTIFICATIONS ya concedido.");
                subscribeToWeatherAlerts();
            }
        } else {
            subscribeToWeatherAlerts();
        }
    }

    private void subscribeToWeatherAlerts() {
        FirebaseMessaging.getInstance().subscribeToTopic("weather_alerts")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Suscripción exitosa al tópico weather_alerts");
                    } else {
                        Log.e(TAG, "Error al suscribirse al tópico weather_alerts", task.getException());
                    }
                });
    }
}
