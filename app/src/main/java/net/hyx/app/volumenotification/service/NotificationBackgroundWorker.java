package net.hyx.app.volumenotification.service;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import net.hyx.app.volumenotification.controller.NotificationServiceController;
import net.hyx.app.volumenotification.controller.TileServiceController;

public class NotificationBackgroundWorker extends Worker {

    private final Context context;

    public NotificationBackgroundWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        NotificationServiceController.newInstance(this.context).checkStartNotificationService();
        TileServiceController.newInstance(this.context).requestListening();
        return Result.success();
    }
}
