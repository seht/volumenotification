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

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import net.hyx.app.volumenotification.service.ForegroundNotificationService;

public class NotificationServiceController {

    private final Context context;

    public NotificationServiceController(Context context) {
        this.context = context;
    }

    public static NotificationServiceController newInstance(Context context) {
        return new NotificationServiceController(context);
    }

    public void startService() {
        ContextCompat.startForegroundService(context, new Intent(context, ForegroundNotificationService.class));
    }

    public void stopService() {
        context.stopService(new Intent(context, ForegroundNotificationService.class));
    }

}
