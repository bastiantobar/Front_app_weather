package com.example.frontweatherapp.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.api.service.WeatherApiService;
import com.example.frontweatherapp.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    private Switch switchNotification;
    private TextView notificationStatus;
    private ImageView iconNotification;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);

        // Inicializar vistas
        switchNotification = rootView.findViewById(R.id.switchNotification);
        notificationStatus = rootView.findViewById(R.id.notificationStatus);
        iconNotification = rootView.findViewById(R.id.iconNotification);

        sharedPreferences = requireActivity().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);

        // Cargar estado guardado
        boolean isEnabled = sharedPreferences.getBoolean("NOTIFICATIONS_ENABLED", false);
        switchNotification.setChecked(isEnabled);
        updateUI(isEnabled);

        // Listener para detectar cambios en el switch
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
        //    saveNotificationPreference(isChecked);
           updateUI(isChecked);
            //sendNotificationPreferenceToServer(isChecked);
        });

        return rootView;
    }

    private void saveNotificationPreference(boolean isEnabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("NOTIFICATIONS_ENABLED", isEnabled);
        editor.apply();
    }

    private void updateUI(boolean isEnabled) {
        if (isEnabled) {
            notificationStatus.setText("✅ Notificaciones activadas");
            notificationStatus.setTextColor(getResources().getColor(R.color.color_hot)); // Rojo
            iconNotification.setImageResource(R.drawable.ic_notifications_active);
            iconNotification.setColorFilter(getResources().getColor(R.color.color_hot));
        } else {
            notificationStatus.setText("❌ Notificaciones desactivadas");
            notificationStatus.setTextColor(getResources().getColor(R.color.color_cold)); // Azul
            iconNotification.setImageResource(R.drawable.ic_notifications_off);
            iconNotification.setColorFilter(getResources().getColor(R.color.color_cold));
        }

        // Animación suave en el Switch
        switchNotification.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150)
                .withEndAction(() -> switchNotification.animate().scaleX(1f).scaleY(1f).setDuration(150))
                .start();
    }
/*
    private void sendNotificationPreferenceToServer(boolean isEnabled) {
        String token = sharedPreferences.getString("TOKEN", null);
        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Error: Token no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        WeatherApiService apiService = RetrofitClient.getInstance(requireContext()).create(WeatherApiService.class);
        Call<Void> call = isEnabled ? apiService.enableNotifications("Bearer " + token) : apiService.disableNotifications("Bearer " + token);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("NotificationFragment", "Preferencia de notificaciones actualizada en el servidor.");
                } else {
                    Toast.makeText(getContext(), "Error al actualizar en el servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red al actualizar notificaciones", Toast.LENGTH_SHORT).show();
            }
        });
    }*/
}
