package com.example.frontweatherapp.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.util.Log;

public class NotificationManager {

    private static final String TAG = "NotificationManager";

    public static void updateNotificationPreference(boolean isEnabled) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            // Nueva traza: Mostrar el UID del usuario
            Log.d(TAG, "updateNotificationPreference: User ID = " + userId);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId);
            // Nueva traza: Mostrar la ruta completa de la base de datos
            Log.d(TAG, "updateNotificationPreference: Database Path = " + ref.child("notifications_enabled").getPath());
            // Nueva traza: Mostrar el valor que se intenta guardar
            Log.d(TAG, "updateNotificationPreference: Attempting to save isEnabled = " + isEnabled);


            ref.child("notifications_enabled").setValue(isEnabled)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Preferencia guardada correctamente en Firebase para el User ID: " + userId);
                        Log.d(TAG, "Valor guardado: " + isEnabled);
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error al guardar preferencia en Firebase para el User ID: " + userId, e));
        } else {
            Log.e(TAG, "Usuario no autenticado. No se puede actualizar la preferencia de notificación.");
        }
    }

    public static void getNotificationPreference(NotificationCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            // Nueva traza: Mostrar el UID del usuario
            Log.d(TAG, "getNotificationPreference: User ID = " + userId);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId);
            // Nueva traza: Mostrar la ruta completa de la base de datos
            Log.d(TAG, "getNotificationPreference: Database Path = " + ref.child("notifications_enabled").getPath());


            ref.child("notifications_enabled").get().addOnSuccessListener(dataSnapshot -> {
                boolean isEnabled = dataSnapshot.exists() && Boolean.TRUE.equals(dataSnapshot.getValue(Boolean.class));
                Log.d(TAG, "Preferencia obtenida de Firebase para el User ID: " + userId + ". isEnabled = " + isEnabled);
                callback.onPreferenceLoaded(isEnabled);
            }).addOnFailureListener(e -> Log.e(TAG, "Error al obtener la preferencia de notificaciones para el User ID: " + userId, e));
        } else {
            Log.e(TAG, "Usuario no autenticado. No se puede obtener la preferencia de notificación.");
        }
    }

    public interface NotificationCallback {
        void onPreferenceLoaded(boolean isEnabled);
    }
}