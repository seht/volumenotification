package net.hyx.app.volumenotification.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import net.hyx.app.volumenotification.controller.NotificationServiceController;
import net.hyx.app.volumenotification.controller.TileServiceController;

public class NotificationBackgroundWorker extends Worker {

    public NotificationBackgroundWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Context context = getApplicationContext();
            NotificationServiceController.newInstance(context).checkStartNotification();
            TileServiceController.newInstance(context).requestListening();
            return Result.success();
        } catch (Exception e) {
            return Result.retry();
        }
    }
}
