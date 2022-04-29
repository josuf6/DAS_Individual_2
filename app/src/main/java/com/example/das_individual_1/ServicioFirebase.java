package com.example.das_individual_1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ServicioFirebase extends FirebaseMessagingService {
    public ServicioFirebase() {}

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {

        //Mostrar notificacion de inicio de sesion al recibir mensaje FCM
        if (message.getNotification() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //Versiones Android 8.0 y superiores
                NotificationChannel channel = new NotificationChannel("canal", "nombre", NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.createNotificationChannel(channel);

            }

            //editar apariencia y configuración de la notificación
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "canal")
                    .setSmallIcon(R.drawable.icono)
                    .setContentTitle(message.getNotification().getTitle())
                    .setContentText(message.getNotification().getBody())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
            managerCompat.notify(1, builder.build());
        }
    }
}
