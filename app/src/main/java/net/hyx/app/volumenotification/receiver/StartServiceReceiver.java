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

package net.hyx.app.volumenotification.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.hyx.app.volumenotification.controller.ApplicationController;
import net.hyx.app.volumenotification.controller.NotificationServiceController;


public class StartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            return;
        }
        //Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();
        switch (intent.getAction()) {
            case Intent.ACTION_MY_PACKAGE_REPLACED:
            case Intent.ACTION_BOOT_COMPLETED:
            case Intent.ACTION_LOCKED_BOOT_COMPLETED:
            case ApplicationController.APPLICATION_STARTED:
                NotificationServiceController.newInstance(context).startService();
                break;
        }

    }


}