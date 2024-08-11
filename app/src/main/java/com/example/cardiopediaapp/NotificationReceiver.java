package com.example.cardiopediaapp;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String medicineName = intent.getStringExtra("medicineName");
        String dosage = intent.getStringExtra("dosage");

        Log.d("NotificationReceiver", "Received reminder: " + medicineName + " - " + dosage);

        // Check if the app has permission to post notifications
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("NotificationReceiver", "Notification permission not granted.");
            return;
        }

        NotificationUtils.createNotificationChannel(context);

        Notification notification = NotificationUtils.createNotification(context, medicineName, dosage);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(medicineName.hashCode(), notification);
    }
}
