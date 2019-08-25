package net.hyx.app.volumenotification.controller;

import android.app.Application;
import android.content.Intent;

import net.hyx.app.volumenotification.receiver.StartServiceReceiver;

/**
 *
 */
public class ApplicationController extends Application {

    private static final String APPLICATION_START_BROADCAST = "net.hyx.app.broadcast.APPLICATION_START_BROADCAST";

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationServiceController.newInstance(getApplicationContext()).checkEnableStartAtBoot();

        Intent intent = new Intent(getApplicationContext(), StartServiceReceiver.class);
        intent.setAction(APPLICATION_START_BROADCAST);
        sendBroadcast(intent);
    }
}
