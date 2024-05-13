package net.hyx.app.volumenotification.service;

import android.content.Context;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class NotificationBackgroundService {

    public static void enqueueWork(Context context) {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationBackgroundWorker.class).build();
        WorkManager.getInstance(context).enqueue(workRequest);
    }
}
