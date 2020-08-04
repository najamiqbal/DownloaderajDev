package com.ajdeveloper.instadownloader.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.webkit.URLUtil;
import android.widget.Toast;
import com.google.android.gms.ads.InterstitialAd;
import java.net.URL;

public class iUtils implements iConstants {
    private InterstitialAd interstitialAd;

    public static boolean isSameDomain(String url, String url1) {
        return getRootDomainUrl(url.toLowerCase()).equals(getRootDomainUrl(url1.toLowerCase()));
    }

    private static String getRootDomainUrl(String url) {
        int dummy = 0;
        String[] domainKeys = url.split("/")[2].split("\\.");
        int length = domainKeys.length;
        if (domainKeys[0].equals("www")) {
            dummy = 1;
        }
        if (length - dummy == 2) {
            return domainKeys[length - 2] + "." + domainKeys[length - 1];
        }
        if (domainKeys[length - 1].length() == 2) {
            return domainKeys[length - 3] + "." + domainKeys[length - 2] + "." + domainKeys[length - 1];
        }
        return domainKeys[length - 2] + "." + domainKeys[length - 1];
    }

    public static void tintMenuIcon(Context context, MenuItem item, int color) {
        Drawable drawable = item.getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(ContextCompat.getColor(context, color), Mode.SRC_ATOP);
        }
    }

    public static void bookmarkUrl(Context context, String url) {
        SharedPreferences pref = context.getSharedPreferences(iConstants.PREF_APPNAME, 0);
        Editor editor = pref.edit();
        if (pref.getBoolean(url, false)) {
            editor.remove(url).commit();
        } else {
            editor.putBoolean(url, true);
        }
        editor.commit();
    }

    public static boolean isBookmarked(Context context, String url) {
        return context.getSharedPreferences(iConstants.PREF_APPNAME, 0).getBoolean(url, false);
    }

    public static void ShowToast(Context context, String str) {
        Toast.makeText(context, str, 0).show();
    }

    public static boolean checkURL(CharSequence input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        boolean isURL = Patterns.WEB_URL.matcher(input).matches();
        if (isURL) {
            return isURL;
        }
        String urlString = input + "";
        if (!URLUtil.isNetworkUrl(urlString)) {
            return isURL;
        }
        try {
            URL url = new URL(urlString);
            return true;
        } catch (Exception e) {
            return isURL;
        }
    }
}
