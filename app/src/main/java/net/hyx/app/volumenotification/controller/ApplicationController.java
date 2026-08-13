package net.hyx.app.volumenotification.controller;

import android.app.Application;
import android.content.Intent;

import androidx.appcompat.app.AppCompatDelegate;

import net.hyx.app.volumenotification.model.SettingsModel;
import net.hyx.app.volumenotification.receiver.StartServiceReceiver;


public class ApplicationController extends Application {

    public static final String ACTION_APPLICATION_STARTED = "net.hyx.app.volumenotification.broadcast.APPLICATION_STARTED";

    @Override
    public void onCreate() {
        super.onCreate();
        SettingsModel settings = SettingsModel.getInstance(getApplicationContext());
        settings.initDarkThemeIfUnset();
        AppCompatDelegate.setDefaultNightMode(settings.getAppNightMode());
        new Thread(() -> NotificationServiceController.newInstance(getApplicationContext()).checkEnableStartAtBoot()).start();

        Intent intent = new Intent(getApplicationContext(), StartServiceReceiver.class);
        intent.setAction(ACTION_APPLICATION_STARTED);
        sendBroadcast(intent);
    }
}
