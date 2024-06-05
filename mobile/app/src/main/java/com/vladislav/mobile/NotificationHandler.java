package com.vladislav.mobile;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.core.app.NotificationCompat;
public class NotificationHandler {
    private final int NOTIFY_ID = 1;
    private final String CHANNEL_ID = "CHANNEL_ID";
    SharedPreferences sharedPreferences;
    Activity activity;
    NotificationManager notificationManager;

    public NotificationHandler(Activity activity) {
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences("MODE", Context.MODE_PRIVATE);
        notificationManager = (android.app.NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        //NotificationHandler notificationHandler = new NotificationHandler(activity);
        //notificationHandler.sendNotification("Hello", "World!");
    }

    private void createChannelIfNeeded(android.app.NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "My Channel", android.app.NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(notificationChannel);
        }
    }

    public void sendNotification(String title, String message) {
        if(sharedPreferences.getBoolean("notification", false)){
            Intent intent = new Intent(activity, DrawerLayoutActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            notificationManager = (android.app.NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            createChannelIfNeeded(notificationManager);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(activity, CHANNEL_ID)
                            .setAutoCancel(false)
                            .setSmallIcon(R.drawable.icon_launcher)
                            .setWhen(System.currentTimeMillis())
                            .setContentIntent(pendingIntent)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setPriority(PRIORITY_HIGH);
            notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
        }
    }
}
