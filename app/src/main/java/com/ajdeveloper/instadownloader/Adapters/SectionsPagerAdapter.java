package com.ajdeveloper.instadownloader.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ajdeveloper.instadownloader.Fragments.BrowserFragment;
import com.ajdeveloper.instadownloader.Fragments.DownloaderFragment;
import com.ajdeveloper.instadownloader.Fragments.HomeFragment;
import com.ajdeveloper.instadownloader.Fragments.SavedFragment;
import com.ajdeveloper.instadownloader.Fragments.SocialFragment;
import com.ajdeveloper.instadownloader.Fragments.WAFragment;
import com.ajdeveloper.instadownloader.FragmentsN.BrowserActivityFragment;
import com.ajdeveloper.instadownloader.FragmentsN.VideosActivityFragment;
import com.ajdeveloper.instadownloader.FragmentsN.download_url;
import com.ajdeveloper.instadownloader.Interfaces.CustomCallbackListener;
import com.ajdeveloper.instadownloader.Utils.Constants;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of videodownloader sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private HomeFragment homeFragment;
    private SocialFragment socialFragment;
    private BrowserFragment gbFragment, fbFragment;
    private DownloaderFragment downloaderFragment, dailymotionFragment;
    private WAFragment waFragment;
    private SavedFragment savedFragment;

    private download_url downloadUrlFragment;
    private VideosActivityFragment videosActivityFragment;
    private BrowserActivityFragment dmFragment, vimeoFragment, twitterFragment, instaFragment;

    public SectionsPagerAdapter(Context context, FragmentManager fm, CustomCallbackListener callbackListener) {
        super(fm);
        homeFragment = new HomeFragment(callbackListener);
        socialFragment = new SocialFragment();
        gbFragment = new BrowserFragment(Constants.GOOGLE);
        fbFragment = new BrowserFragment(Constants.FB);
        downloaderFragment = new DownloaderFragment("Enter URL to download from:");
        dailymotionFragment = new DownloaderFragment("Enter DailyMotion URL to download from:");
//        instaFragment = new DownloaderFragment("Enter Instagram URL to download from:");
        waFragment = new WAFragment();
        savedFragment = new SavedFragment();

        downloadUrlFragment = new download_url();
        videosActivityFragment = new VideosActivityFragment();
        dmFragment = new BrowserActivityFragment("https://www.dailymotion.com");
        vimeoFragment = new BrowserActivityFragment("https://vimeo.com/watch");
        twitterFragment = new BrowserActivityFragment("https://www.twitter.com");
        instaFragment = new BrowserActivityFragment("https://www.instagram.com");
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate videodownloader fragment for videodownloader given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
//        if (position == 0)
//            return homeFragment;
        if (position == 0)
            return videosActivityFragment;
        if (position == 1)
            return downloadUrlFragment;
        if (position == 2)
            return fbFragment;
        if (position == 3)
            return instaFragment;
        if (position == 4)
            return waFragment;
        if (position == 5)
            return dmFragment;
        if (position == 6)
            return vimeoFragment;
        if (position == 7)
            return twitterFragment;
        return savedFragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
//        if (position == 0)
//            return "Home";
        if (position == 0)
            return "Browse Videos";
        if (position == 1)
            return "URL";
        if (position == 2)
            return "Facebook";
        if (position == 3)
            return "Instagram";
        if (position == 4)
            return "WA Status";
        if (position == 5)
            return "Daily Motion";
        if (position == 6)
            return "Vimeo";
        if (position == 7)
            return "Twitter";
        return "Saved";
    }

    public boolean canGoBack(int index) {
        if (index == 3 && fbFragment.canGoBack())
            return true;
        if (index == 6 && dmFragment.canGoBack())
            return true;
        if (index == 7 && vimeoFragment.canGoBack())
            return true;
        return index == 8 && twitterFragment.canGoBack();
//        return (index == 1) ? gbFragment.canGoBack() : fbFragment.canGoBack();
    }

    public void goBack(int index) {
        if (index == 3 && fbFragment.canGoBack())
            fbFragment.goBack();
        if (index == 6 && dmFragment.canGoBack())
            dmFragment.goBack();
        if (index == 7 && vimeoFragment.canGoBack())
            vimeoFragment.goBack();
        if (index == 8 && twitterFragment.canGoBack())
            twitterFragment.goBack();
    }

    @Override
    public int getCount() {
        return 9;
    }
}