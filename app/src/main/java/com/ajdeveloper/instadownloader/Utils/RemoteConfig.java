package com.ajdeveloper.instadownloader.Utils;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class RemoteConfig {
    private Context mContext;

    public Map<String, Object> getDefaultAdsConfig() {
        HashMap hashMap = new HashMap();
        hashMap.put("ad_gift_icon_main", Boolean.valueOf(true));
        hashMap.put("ad_home", Boolean.valueOf(true));
        hashMap.put("ad_videos", Boolean.valueOf(true));
        hashMap.put("ad_download_url", Boolean.valueOf(false));
        hashMap.put("ad_saved", Boolean.valueOf(false));
        hashMap.put("ad_facebook", Boolean.valueOf(false));
        hashMap.put("ad_vimeo", Boolean.valueOf(false));
        hashMap.put("ad_whatsapp", Boolean.valueOf(false));
        hashMap.put("ad_dailymotion", Boolean.valueOf(false));
        hashMap.put("ad_twitter", Boolean.valueOf(false));
        hashMap.put("ad_instagram", Boolean.valueOf(false));
        hashMap.put("ad_download_button", Boolean.valueOf(true));
        hashMap.put("fb_banner_trending_home", Boolean.valueOf(true));
        hashMap.put("banner_videos_list", Boolean.valueOf(true));
        hashMap.put("fb_banner_quality_list", Boolean.valueOf(true));
        hashMap.put("admob_banner_quality_list", Boolean.valueOf(false));
        hashMap.put("ad_position_trending", Integer.valueOf(5));
        hashMap.put("ad_position_videos", Integer.valueOf(5));
        hashMap.put("ad_position_show_downloads", Integer.valueOf(10));
        hashMap.put("rate_us_dialog_count", Integer.valueOf(10));
        hashMap.put("ad_search_button", Boolean.valueOf(false));
        hashMap.put("ad_back_from_search", Boolean.valueOf(false));
        return hashMap;
    }

    public RemoteConfig(Context context) {
        this.mContext = context;
    }

    public void fetchAdsConfigFromServer() {
    }
}
