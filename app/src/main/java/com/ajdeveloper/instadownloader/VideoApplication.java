package com.ajdeveloper.instadownloader;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build.VERSION;
import com.google.android.gms.ads.MobileAds;

import com.ajdeveloper.instadownloader.Components.component.AppComponent;
import com.ajdeveloper.instadownloader.Components.component.DaggerAppComponent;
import com.ajdeveloper.instadownloader.Components.module.AppModule;

public class VideoApplication extends Application {
    private AppComponent appComponent;

    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));
        initAppComponent();
        createNotificationChannel();
    }

    private void initAppComponent() {
        this.appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public AppComponent getAppComponent() {
        return this.appComponent;
    }

    private void createNotificationChannel() {
        if (VERSION.SDK_INT >= 26) {
            ((NotificationManager) getSystemService(NotificationManager.class)).createNotificationChannel(new NotificationChannel("ServiceChannel", "Service Channel", 3));
        }
    }
}
