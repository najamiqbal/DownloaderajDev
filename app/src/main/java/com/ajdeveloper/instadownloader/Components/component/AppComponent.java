package com.ajdeveloper.instadownloader.Components.component;

import android.content.Context;
import android.content.SharedPreferences;

import retrofit2.Retrofit;

/* renamed from: com.downloadvideos.videodownloader.hdvideo.anyvideodownloader.di.component.AppComponent */
public interface AppComponent {
    Context context();

    Retrofit retrofit();

    SharedPreferences sharedPreferences();
}
