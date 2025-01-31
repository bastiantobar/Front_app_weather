package com.example.frontweatherapp.ui.fragments;

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
import com.example.frontweatherapp.utils.NotificationManager;

public class NotificationFragment extends Fragment {

    private Switch switchNotification;
    private TextView notificationStatus;
    private ImageView iconNotification;

    private static final String TAG = "NotificationFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);

        // Inicializar vistas
        switchNotification = rootView.findViewById(R.id.switchNotification);
        notificationStatus = rootView.findViewById(R.id.notificationStatus);
        iconNotification = rootView.findViewById(R.id.iconNotification);

        // Obtener el estado actual de Firebase
        NotificationManager.getNotificationPreference(isEnabled -> {
            switchNotification.setChecked(isEnabled);
            updateUI(isEnabled);
        });

        // Listener para detectar cambios en el switch
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            NotificationManager.updateNotificationPreference(isChecked);
            updateUI(isChecked);
        });

        return rootView;
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
}
