package com.example.frontweatherapp.api.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.frontweatherapp.R;
import com.example.frontweatherapp.ui.main.MenuActivity; // Cambia a la actividad que deseas abrir
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "weather_alerts_channel";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // Log para depuración
        Log.d(TAG, "Mensaje recibido de: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");

            Log.d(TAG, "Mensaje recibido de title: " + title);
            Log.d(TAG, "Mensaje recibido de body: " + body);

            // Verificar si el usuario está logueado
            if (isUserLoggedIn()) {
                Log.d(TAG, "Usuario logueado. enviando notificacion.");
                // Mostrar la notificación
                sendNotification(title, body);
            } else {
                Log.d(TAG, "Usuario no logueado. Ignorando notificación.");
            }
        }
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        return prefs.getBoolean("LOGGED_IN", false);
    }


    private void sendNotification(String title, String body) {
        Log.d("NotificationService", "sendNotification() llamado con título: " + title + " y cuerpo: " + body);

        // Revisar permisos de notificación (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.e("NotificationService", "Permiso POST_NOTIFICATIONS no concedido.");
                return;
            }
        }

        // Crear el canal de notificación (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alertas Meteorológicas",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Canal para notificaciones de alertas meteorológicas");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                Log.d("NotificationService", "Creando el canal de notificación...");
                manager.createNotificationChannel(channel);
            }
        }

        // Crear el Intent para abrir MainMenuActivity
        Intent intent = new Intent(this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        // Construir la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Icono válido
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Mostrar la notificación
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
        Log.d("NotificationService", "Notificación enviada correctamente.");
    }



}
