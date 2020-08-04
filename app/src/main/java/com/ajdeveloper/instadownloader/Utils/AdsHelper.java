package com.ajdeveloper.instadownloader.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import com.ajdeveloper.instadownloader.Activities.SplashActivity;
import com.ajdeveloper.instadownloader.NaviDrawer;
import com.ajdeveloper.instadownloader.R;

public class AdsHelper {

    public static AdsHelper getInstance() {
        return new AdsHelper();
    }

    public void loadBanner(Activity activity) {
        if (getPurchasedValue(activity).equals("Purchased"))
            return;
        final AdView avBanner = activity.findViewById(R.id.av_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        avBanner.loadAd(adRequest);
        avBanner.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                AdRequest adRequest = new AdRequest.Builder().build();
                avBanner.loadAd(adRequest);
            }
        });
    }

    private InterstitialAd interstitialAd;
    public void loadInterstitial(final Context context) {
        if (getPurchasedValue(context).equals("Purchased"))
            return;
        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(context.getString(R.string.admob_interstitial_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int i) {
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdClosed() {
                // MoveToNext
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    public int showInterstitialAd() {
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
            return 1;
        }
        return 0;
    }

    public void loadInterstitial(final SplashActivity activity) {
        if (getPurchasedValue(activity).equals("Purchased"))
            return;
        final InterstitialAd interstitialAd = new InterstitialAd(activity);
        interstitialAd.setAdUnitId(activity.getString(R.string.admob_interstitial_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int i) {
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                isLoaded = true;
                if (activity.isResumed)
                    interstitialAd.show();
            }

            @Override
            public void onAdClosed() {
                // MoveToNext
                activity.startActivity(new Intent(activity, NaviDrawer.class));
                activity.finish();
            }
        });
        moveToNext(activity);
    }

    private boolean isLoaded = false;

    private void moveToNext(final Activity activity) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // MoveToNext
                if (!isLoaded) {
                    activity.startActivity(new Intent(activity, NaviDrawer.class));
                    activity.finish();
                }
            }
        }, 6000);
    }

    public void loadAdaptiveBanner(Activity context) {
        if (getPurchasedValue(context).equals("Purchased"))
            return;
        RelativeLayout adContainerView = context.findViewById(R.id.av_banner);
        // Create an ad request. Check your logcat output for videodownloader hashed device ID
        // to get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this
        // device."
        // Step 1 - Create an AdView and set videodownloader ad unit ID on it.
        AdView adView = new AdView(context);
        adView.setAdUnitId(context.getString(R.string.admob_banner_id));
        adContainerView.addView(adView);

        AdSize adSize = getAdSize(context);
        // Step 4 - Set videodownloader adaptive ad size on videodownloader ad view.
        adView.setAdSize(adSize);

        // Step 5 - Start loading videodownloader ad in videodownloader background.
        adView.loadAd(new AdRequest.Builder().build());
    }

    private AdSize getAdSize(Activity context) {
        // Step 2 - Determine videodownloader screen width (less decorations) to use for videodownloader ad width.
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on videodownloader ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }

    private String getPurchasedValue(Context context) {
        SharedPreferences getPurchaseValue = context.getSharedPreferences("check_Purchased", Context.MODE_PRIVATE);
        return getPurchaseValue.getString("Value", "");
    }

}
