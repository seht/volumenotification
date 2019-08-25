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

package net.hyx.app.volumenotification.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import net.hyx.app.volumenotification.factory.NotificationFactory;
import net.hyx.app.volumenotification.model.SettingsModel;
import net.hyx.app.volumenotification.receiver.StartServiceReceiver;
import net.hyx.app.volumenotification.service.NotificationBackgroundService;
import net.hyx.app.volumenotification.service.NotificationForegroundService;

/**
 * @see {https://developer.android.com/training/scheduling/alarms.html#boot}
 */
public class NotificationServiceController {

    private final Context context;
    private final SettingsModel settings;

    public NotificationServiceController(Context context) {
        this.context = context;
        settings = new SettingsModel(context);
    }

    public static NotificationServiceController newInstance(Context context) {
        return new NotificationServiceController(context);
    }

    public void startService() {
        NotificationBackgroundService.enqueueWork(context, new Intent(context, NotificationBackgroundService.class));
    }

    public void checkEnableStartAtBoot() {
        PackageManager pm = context.getPackageManager();
        ComponentName receiver = new ComponentName(context.getApplicationContext(), StartServiceReceiver.class);

        if (settings.startsAtBoot()) {
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        } else {
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }

    public void checkStartNotificationService() {
        if (settings.isEnabled()) {
            if (settings.hasForegroundService()) {
                startForegroundService();
            } else {
                stopForegroundService();
            }
            NotificationFactory factory = new NotificationFactory(context);
            factory.startNotification();
        } else {
            stopForegroundService();
            NotificationFactory factory = new NotificationFactory(context);
            factory.cancelNotification();
        }
    }

    private void startForegroundService() {
        ContextCompat.startForegroundService(context, new Intent(context, NotificationForegroundService.class));
    }

    private void stopForegroundService() {
        context.stopService(new Intent(context, NotificationForegroundService.class));
    }

}
