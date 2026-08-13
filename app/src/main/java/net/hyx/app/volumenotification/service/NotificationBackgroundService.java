package net.hyx.app.volumenotification.service;

import android.content.Context;

import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class NotificationBackgroundService {

    private static final String UNIQUE_WORK_NAME = "notification-background-refresh";

    public static void enqueueWork(Context context) {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationBackgroundWorker.class).build();
        WorkManager.getInstance(context).enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.KEEP, workRequest);
    }
}
