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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import net.hyx.app.volumenotification.controller.NotificationServiceController;
import net.hyx.app.volumenotification.factory.NotificationFactory;

public class NotificationForegroundService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationFactory factory = new NotificationFactory(getApplicationContext());
        startForeground(factory.getNotificationId(), factory.getNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationFactory factory = new NotificationFactory(getApplicationContext());
        factory.updateNotification();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        NotificationServiceController.newInstance(getApplicationContext()).startService();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
