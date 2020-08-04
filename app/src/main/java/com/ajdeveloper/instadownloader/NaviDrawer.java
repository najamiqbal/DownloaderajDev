package com.ajdeveloper.instadownloader;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.ajdeveloper.instadownloader.Activities.SplashActivity;
import com.ajdeveloper.instadownloader.CustomViews.CustomDialog;
import com.anjlab.android.iab.v3.BillingProcessor;

import androidx.core.app.ShareCompat;

import android.util.Log;
import android.view.View;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;

import com.ajdeveloper.instadownloader.Activities.BaseActivity;
import com.ajdeveloper.instadownloader.Activities.BrowserActivity;
import com.ajdeveloper.instadownloader.Activities.PrivacyPolicyActivity;
import com.ajdeveloper.instadownloader.Adapters.SectionsPagerAdapter;
import com.ajdeveloper.instadownloader.CustomViews.CustomViewPager;
import com.ajdeveloper.instadownloader.CustomViews.ExitDialog;
import com.ajdeveloper.instadownloader.Fragments.BrowserFragment;
import com.ajdeveloper.instadownloader.Fragments.SocialFragment;
import com.ajdeveloper.instadownloader.Interfaces.CustomCallbackListener;
import com.ajdeveloper.instadownloader.Utils.AdsHelper;
import com.ajdeveloper.instadownloader.Utils.Constants;
import com.ajdeveloper.instadownloader.Utils.IAPHelper;
import com.ajdeveloper.instadownloader.Utils.Utilities;

import static android.content.ContentValues.TAG;

public class NaviDrawer extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, CustomDialog.OnClickListener {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private CustomViewPager viewPager;
    private IAPHelper iapHelper;
    private ExitDialog exitDialog;
    private int adCount = 0;
    private InterstitialAd interstitialAd;
    private AdView adViewbannerfb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadBannerAds();
        LoadinterstitialAd();
        iapHelper = new IAPHelper(this);
        //MobileAds.initialize(this, getString(R.string.admob_app_id));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
//        drawer.openDrawer(GravityCompat.START);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        socialFragment = new SocialFragment(this);
        Utilities.checkPermission(this);
        exitDialog = new ExitDialog(this);
        initViewPager();
        manageTopTabs();
        manageBottomTabs();
        setAnim(true, 1);
        findViewById(R.id.cv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrowserActivity.start(NaviDrawer.this, "https://www.google.com/");
            }
        });
    }

    private void loadBannerAds() {

        // Instantiate an AdView object.
        // NOTE: The placement ID from the Facebook Monetization Manager identifies your App.
        // To get test ads, add IMG_16_9_APP_INSTALL# to your placement id. Remove this when your app is ready to serve real ads.

        adViewbannerfb = new AdView(this, getString(R.string.fb_banner_id), AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adViewbannerfb);

        // Request an ad
        adViewbannerfb.loadAd();
    }

    private void LoadinterstitialAd() {
        interstitialAd = new InterstitialAd(NaviDrawer.this, getString(R.string.fb_interstitial_id));
        interstitialAd.loadAd();
    }

    private void initViewPager() {
        //New
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), new CustomCallbackListener() {
            @Override
            public void onItemClicked(int position) {
                if (viewPager == null)
                    return;
                viewPager.setCurrentItem(position, false);
            }
        });
//        TabLayout tabLayout = findViewById(R.id.tabs_bottom);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
//        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
//        TabLayout tabs = findViewById(R.id.tabs);
//        tabs.setupWithViewPager(viewPager);
        //New
    }

    private SocialFragment socialFragment;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int curretPosition = viewPager.getCurrentItem();
            if (viewPager.getCurrentItem() == 0) {
                //exitDialog.show();
                CustomDialog cdd = new CustomDialog(NaviDrawer.this, this);
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
            } else if (sectionsPagerAdapter.canGoBack(curretPosition))
                sectionsPagerAdapter.goBack(curretPosition);
            else
                changeFragment(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate videodownloader menu; this adds items to videodownloader action bar if it is present.
        getMenuInflater().inflate(R.menu.navi_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on videodownloader Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share_app) {
            shareApp();
            return true;
        } else if (id == R.id.action_rate_us) {
            rateApp();
            return true;
        } else if (id == R.id.action_remove_ads) {
            iapHelper.showDialog();
            return true;
        } else if (id == R.id.action_policy) {
            startActivity(new Intent(this, PrivacyPolicyActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareApp() {
        ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setChooserTitle("Share Free Video Downloader")
                .setText("http://play.google.com/store/apps/details?id=" + getPackageName())
                .startChooser();
    }

    private void rateApp() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
    }

    private BrowserFragment gbFragment = new BrowserFragment(Constants.GOOGLE), fbFragment = new BrowserFragment(Constants.FB);

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            changeFragment(0);
        } else if (id == R.id.nav_videos) {
            changeFragment(0);
        } else if (id == R.id.nav_url) {
            changeFragment(1);
        } else if (id == R.id.nav_social) {
            changeFragment(2);
        } else if (id == R.id.nav_insta) {
            changeFragment(3);
        } else if (id == R.id.nav_wa) {
            //WA Frag
            changeFragment(4);
        } else if (id == R.id.nav_dm) {
            changeFragment(5);
        } else if (id == R.id.nav_vimeo) {
            changeFragment(6);
        } else if (id == R.id.nav_twitter) {
            changeFragment(7);
        } else if (id == R.id.nav_saved) {
            changeFragment(8);
        } else if (id == R.id.nav_share) {
            shareApp();
        } else if (id == R.id.nav_rate) {
            rateApp();
        } else if (id == R.id.nav_exit) {
            //exitDialog.show();
            CustomDialog cdd = new CustomDialog(NaviDrawer.this, this);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeFragment(int position) {
        viewPager.setCurrentItem(position, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        BillingProcessor bp = iapHelper.getBp();
        if (bp == null)
            return;
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private int mCount = 0;

    public void setAnim(boolean z, int i) {
        ImageView ivAd = findViewById(R.id.iv_ad);
        ivAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitialAd != null && interstitialAd.isAdLoaded()) {
                    interstitialAd.show();
                    // Set listeners for the Interstitial Ad
                    interstitialAd.setAdListener(new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(Ad ad) {
                            // Interstitial ad displayed callback
                            Log.e(TAG, "Interstitial ad displayed.");
                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {
                            // Interstitial dismissed callback
                            Log.e(TAG, "Interstitial ad dismissed.");

                        }

                        @Override
                        public void onError(Ad ad, AdError adError) {
                            // Ad error callback
                            Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                        }

                        @Override
                        public void onAdLoaded(Ad ad) {
                            // Interstitial ad is loaded and ready to be displayed
                            Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                            // Show the ad

                        }

                        @Override
                        public void onAdClicked(Ad ad) {
                            // Ad clicked callback
                            Log.d(TAG, "Interstitial ad clicked!");
                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {
                            // Ad impression logged callback
                            Log.d(TAG, "Interstitial ad impression logged!");
                        }
                    });
                }
            }
        });
        RotateAnimation rotateAnimation;
        final boolean z2 = z;
        this.mCount = i;
        if (z2) {
            rotateAnimation = new RotateAnimation(0.0f, 360.0f, 1, 0.6f, 1, 0.5f);
        } else {
            rotateAnimation = new RotateAnimation(360.0f, 0.0f, 1, 0.6f, 1, 0.5f);
        }
        rotateAnimation.setDuration(1400);
        rotateAnimation.setRepeatCount(0);
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                if (NaviDrawer.this.mCount < 20) {
                    NaviDrawer mainActivity = NaviDrawer.this;
                    mainActivity.setAnim(!z2, mainActivity.mCount);
                    return;
                }
                ivAd.clearAnimation();
            }
        });
        ivAd.startAnimation(rotateAnimation);
    }

//    private int currentPosition = 0;

    private void manageTopTabs() {
        int[] iconsArr = {R.drawable.ic_facebook
                , R.drawable.ic_insta
                , R.drawable.ic_whats
                , R.drawable.dailymotion
                , R.drawable.vimeo
                , R.drawable.ic_twit};
        String[] titlesArr = {"FB", "Insta", "WhatsApp", "DailyMotion", "Vimeo", "Twitter"};
        TabLayout tabsTop = findViewById(R.id.tabs_top);
        for (int i = 0; i < iconsArr.length; i++) {
            tabsTop.addTab(getTab(tabsTop, iconsArr[i], titlesArr[i]));
        }
        tabsTop.getTabAt(1).select();
        tabsTop.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tempPos = tab.getPosition();
//                if (tempPos == currentPosition)
//                    return;
                if (tempPos == 0)
                    changeFragment(2);
                else if (tempPos == 1)
                    changeFragment(3);
                else if (tempPos == 2) {
                    changeFragment(4);
                } else if (tempPos == 3) {
                    changeFragment(5);
                } else if (tempPos == 4) {
                    changeFragment(6);
                } else if (tempPos == 5) {
                    changeFragment(7);
                }
//                currentPosition = tempPos;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

//    private int currentBottomPosition = 0;

    private void manageBottomTabs() {
        int[] iconsArr = {R.drawable.ic_videos
                , R.drawable.ic_browse
                , R.drawable.ic_saved};
        String[] titlesArr = {"Videos", "URL", "Saved"};
        TabLayout tabsTop = findViewById(R.id.tabs_bottom);
        for (int i = 0; i < iconsArr.length; i++) {
            tabsTop.addTab(getTab(tabsTop, iconsArr[i], titlesArr[i]));
        }
        tabsTop.getTabAt(1).select();
        tabsTop.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tempPos = tab.getPosition();
//                if (tempPos == currentBottomPosition)
//                    return;
                if (tempPos == 0)
                    if (interstitialAd != null && interstitialAd.isAdLoaded()) {
                        interstitialAd.show();
                        // Set listeners for the Interstitial Ad
                        interstitialAd.setAdListener(new InterstitialAdListener() {
                            @Override
                            public void onInterstitialDisplayed(Ad ad) {
                                // Interstitial ad displayed callback
                                Log.e(TAG, "Interstitial ad displayed.");
                            }

                            @Override
                            public void onInterstitialDismissed(Ad ad) {
                                // Interstitial dismissed callback
                                Log.e(TAG, "Interstitial ad dismissed.");

                                changeFragment(0);
                            }

                            @Override
                            public void onError(Ad ad, AdError adError) {
                                // Ad error callback
                                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                // Interstitial ad is loaded and ready to be displayed
                                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                                // Show the ad

                            }

                            @Override
                            public void onAdClicked(Ad ad) {
                                // Ad clicked callback
                                Log.d(TAG, "Interstitial ad clicked!");
                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {
                                // Ad impression logged callback
                                Log.d(TAG, "Interstitial ad impression logged!");
                            }
                        });
                    } else {

                        changeFragment(0);
                    }

                else if (tempPos == 1)
                    if (interstitialAd != null && interstitialAd.isAdLoaded()) {
                        interstitialAd.show();
                        // Set listeners for the Interstitial Ad
                        interstitialAd.setAdListener(new InterstitialAdListener() {
                            @Override
                            public void onInterstitialDisplayed(Ad ad) {
                                // Interstitial ad displayed callback
                                Log.e(TAG, "Interstitial ad displayed.");
                            }

                            @Override
                            public void onInterstitialDismissed(Ad ad) {
                                // Interstitial dismissed callback
                                Log.e(TAG, "Interstitial ad dismissed.");
                                changeFragment(1);
                            }

                            @Override
                            public void onError(Ad ad, AdError adError) {
                                // Ad error callback
                                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                // Interstitial ad is loaded and ready to be displayed
                                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                                // Show the ad

                            }

                            @Override
                            public void onAdClicked(Ad ad) {
                                // Ad clicked callback
                                Log.d(TAG, "Interstitial ad clicked!");
                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {
                                // Ad impression logged callback
                                Log.d(TAG, "Interstitial ad impression logged!");
                            }
                        });
                    } else {

                        changeFragment(1);
                    }

                else if (tempPos == 2) {
                    if (interstitialAd != null && interstitialAd.isAdLoaded()) {
                        interstitialAd.show();
                        // Set listeners for the Interstitial Ad
                        interstitialAd.setAdListener(new InterstitialAdListener() {
                            @Override
                            public void onInterstitialDisplayed(Ad ad) {
                                // Interstitial ad displayed callback
                                Log.e(TAG, "Interstitial ad displayed.");
                            }

                            @Override
                            public void onInterstitialDismissed(Ad ad) {
                                // Interstitial dismissed callback
                                Log.e(TAG, "Interstitial ad dismissed.");

                                changeFragment(8);
                            }

                            @Override
                            public void onError(Ad ad, AdError adError) {
                                // Ad error callback
                                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                // Interstitial ad is loaded and ready to be displayed
                                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                                // Show the ad

                            }

                            @Override
                            public void onAdClicked(Ad ad) {
                                // Ad clicked callback
                                Log.d(TAG, "Interstitial ad clicked!");
                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {
                                // Ad impression logged callback
                                Log.d(TAG, "Interstitial ad impression logged!");
                            }
                        });
                    } else {
                        changeFragment(8);
                    }

                }
//                currentBottomPosition = tempPos;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private TabLayout.Tab getTab(TabLayout tabLayout, int iconID, String title) {
        TabLayout.Tab tab = tabLayout.newTab();
        tab.setIcon(iconID);
        tab.setText(title);
        return tab;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadinterstitialAd();
    }

    @Override
    public void onRateClick() {
        rateApp();
    }

    @Override
    public void onExitClick() {
        finish();
    }
}
