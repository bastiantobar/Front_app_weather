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
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId);

            ref.child("notifications_enabled").setValue(isEnabled)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Preferencia guardada correctamente en Firebase"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error al guardar preferencia en Firebase", e));
        } else {
            Log.e(TAG, "Usuario no autenticado.");
        }
    }

    public static void getNotificationPreference(NotificationCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId);

            ref.child("notifications_enabled").get().addOnSuccessListener(dataSnapshot -> {
                boolean isEnabled = dataSnapshot.exists() && Boolean.TRUE.equals(dataSnapshot.getValue(Boolean.class));
                callback.onPreferenceLoaded(isEnabled);
            }).addOnFailureListener(e -> Log.e(TAG, "Error al obtener la preferencia de notificaciones", e));
        } else {
            Log.e(TAG, "Usuario no autenticado.");
        }
    }

    public interface NotificationCallback {
        void onPreferenceLoaded(boolean isEnabled);
    }
}
