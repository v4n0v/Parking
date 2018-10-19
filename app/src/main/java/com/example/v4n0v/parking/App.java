package com.example.v4n0v.parking;

import android.app.Application;

import timber.log.Timber;

public class App extends Application {

    private App instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Timber.plant(new Timber.DebugTree());
    }

    public App getInstance(){
        return instance;
    }
}
