package com.example.v4n0v.parking;

import android.app.Application;

public class App extends Application {

    private App instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public App getInstance(){
        return instance;
    }
}
