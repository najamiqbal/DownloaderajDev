package com.ajdeveloper.instadownloader.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.ajdeveloper.instadownloader.NaviDrawer;
import com.ajdeveloper.instadownloader.R;
import com.ajdeveloper.instadownloader.Utils.AdsHelper;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import static android.content.ContentValues.TAG;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startActivity(new Intent(SplashActivity.this, NaviDrawer.class));
        finish();
        //AdsHelper.getInstance().loadInterstitial(this);

    }


    public boolean isResumed = false;

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
    }
}
