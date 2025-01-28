package com.example.frontweatherapp.ui.login;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.ui.fragments.LoginFragment;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar el lanzador de solicitud de permisos
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

        // Verificar y solicitar el permiso de notificaciones si es necesario
        checkAndRequestNotificationPermission();

        // Cargar el LoginFragment al iniciar
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
            // Si el sistema es menor a Android 13, no es necesario solicitar permiso
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
