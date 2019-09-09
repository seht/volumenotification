/*
 * Copyright 2017 https://github.com/seht
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.hyx.app.volumenotification.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
//import android.os.IBinder;
//import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import net.hyx.app.volumenotification.controller.NotificationServiceController;
import net.hyx.app.volumenotification.controller.TileServiceController;

/**
 * @see {https://developer.android.com/reference/androidx/core/app/JobIntentService}
 */
public class NotificationBackgroundService extends JobIntentService {

    private static final int JOB_ID = 1000;
    
    public static void enqueueWork(Context context, Intent work) {
        JobIntentService.enqueueWork(context, NotificationBackgroundService.class, JOB_ID, work);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        NotificationServiceController.newInstance(getApplicationContext()).checkStartNotificationService();
        TileServiceController.newInstance(getApplicationContext()).requestListening();
    }

    @Override
    public boolean onStopCurrentWork() {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
