package net.hyx.app.volumenotification.controller;

import android.app.Application;
import android.content.Intent;

import net.hyx.app.volumenotification.receiver.StartServiceReceiver;


public class ApplicationController extends Application {

    public static final String APPLICATION_STARTED = "net.hyx.app.volumenotification.broadcast.APPLICATION_STARTED";

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationServiceController.newInstance(getApplicationContext()).checkEnableStartAtBoot();

        Intent intent = new Intent(getApplicationContext(), StartServiceReceiver.class);
        intent.setAction(APPLICATION_STARTED);
        sendBroadcast(intent);
    }
}
