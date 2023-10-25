package net.hyx.app.volumenotification.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import net.hyx.app.volumenotification.R;

public class ScreenshotForegroundService extends Service {

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "net.hyx.app.volumenotification.channel.DEFAULT";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_START_FOREGROUND_SERVICE.equals(intent.getAction())) {
            // Create the notification
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Foreground Service")
                    .setContentText("Media Projection")
                    .setSmallIcon(R.drawable.ic_app_icon)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .build();

            // Start the foreground service
            startForeground(NOTIFICATION_ID, notification);
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Stop the foreground service
        stopForeground(true);

        // Stop the Service
        stopSelf();
    }

}
