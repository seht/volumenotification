package net.hyx.app.volumenotification.controller;

import android.app.Application;

public class ApplicationController extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //NotificationController.newInstance(getApplicationContext()).create();
    }
}
