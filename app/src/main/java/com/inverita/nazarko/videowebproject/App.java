package com.inverita.nazarko.videowebproject;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by nazarko on 11/8/16.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
