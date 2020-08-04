package com.ajdeveloper.instadownloader.Utils;

import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import com.ajdeveloper.instadownloader.R;
import com.ajdeveloper.instadownloader.database.SharedPref;

public class AdsUtil {
    private static AdsUtil sInstance;
    /* access modifiers changed from: private */
    public InterstitialAd interstitialAd;
    private Context mContext;
    /* access modifiers changed from: private */
    public boolean mIsInterAdRequested = false;

    public interface AdsCallBack {
        void onAdFailedToLoad(String str, String str2);
    }

    private AdsUtil(Context context) {
        this.mContext = context;
        Context context2 = this.mContext;
        MobileAds.initialize(context2, context2.getResources().getString(R.string.admob_app_id));
    }

    public static AdsUtil getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AdsUtil(context);
        }
        return sInstance;
    }

    public void loadBannerAd(final AdView adView, final String str, final AdsCallBack adsCallBack) {
        try {
            if (!SharedPref.getInstance(this.mContext).getSpBoolean("remove_ads", false)) {
                adView.loadAd(new Builder().build());
                adView.setAdListener(new AdListener() {
                    public void onAdClosed() {
                    }

                    public void onAdLeftApplication() {
                    }

                    public void onAdOpened() {
                    }

                    public void onAdLoaded() {
                        adView.setVisibility(0);
                    }

                    public void onAdFailedToLoad(int i) {
                        switch (i) {
                            case 0:
                                adsCallBack.onAdFailedToLoad(str, "error_internal");
                                return;
                            case 1:
                                adsCallBack.onAdFailedToLoad(str, "error_internal");
                                return;
                            case 2:
                                adsCallBack.onAdFailedToLoad(str, "error_internal");
                                return;
                            case 3:
                                adsCallBack.onAdFailedToLoad(str, "error_internal");
                                return;
                            default:
                                return;
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadInterstitialAd(String str, final String str2, final AdsCallBack adsCallBack) {
        try {
            if (!SharedPref.getInstance(this.mContext).getSpBoolean("remove_ads", false) && !this.mIsInterAdRequested) {
                this.mIsInterAdRequested = true;
                if (!isAdLoaded()) {
                    this.mIsInterAdRequested = false;
                    loadAd();
                } else {
                    this.interstitialAd.show();
                }
                this.interstitialAd.setAdListener(new AdListener() {
                    public void onAdLoaded() {
                        AdsUtil.this.interstitialAd.show();
                    }

                    public void onAdFailedToLoad(int i) {
                        switch (i) {
                            case 0:
                                adsCallBack.onAdFailedToLoad(str2, "error_internal");
                                break;
                            case 1:
                                adsCallBack.onAdFailedToLoad(str2, "error_internal");
                                break;
                            case 2:
                                adsCallBack.onAdFailedToLoad(str2, "error_internal");
                                break;
                            case 3:
                                adsCallBack.onAdFailedToLoad(str2, "error_internal");
                                break;
                        }
                        AdsUtil.this.mIsInterAdRequested = false;
                        AdsUtil.this.loadAd();
                    }

                    public void onAdOpened() {
                        AdsUtil.this.mIsInterAdRequested = false;
                    }

                    public void onAdLeftApplication() {
                        AdsUtil.this.mIsInterAdRequested = false;
                    }

                    public void onAdClosed() {
                        AdsUtil.this.mIsInterAdRequested = false;
                        AdsUtil.this.loadAd();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isAdLoaded() {
        InterstitialAd interstitialAd2 = this.interstitialAd;
        return interstitialAd2 != null && interstitialAd2.isLoaded();
    }

    public void loadAd() {
        if (this.interstitialAd == null) {
            this.interstitialAd = new InterstitialAd(this.mContext);
            this.interstitialAd.setAdUnitId(this.mContext.getResources().getString(R.string.admob_interstitial_id));
        }
        this.interstitialAd.setAdListener(null);
        this.interstitialAd.loadAd(new Builder().build());
    }
}
