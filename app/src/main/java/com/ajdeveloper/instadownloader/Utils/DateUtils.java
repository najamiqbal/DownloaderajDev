package com.ajdeveloper.instadownloader.Utils;

public class DateUtils {
    public static String getTimeAgo(long j) {
        return android.text.format.DateUtils.getRelativeTimeSpanString(j, System.currentTimeMillis(), 60000).toString();
    }
}
