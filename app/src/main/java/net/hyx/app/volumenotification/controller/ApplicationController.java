package net.hyx.app.volumenotification.controller;

import android.app.Application;

public class ApplicationController extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        NotificationServiceController.newInstance(getApplicationContext()).startService();
//        TileServiceController.newInstance(getApplicationContext()).requestListening();
    }
}
