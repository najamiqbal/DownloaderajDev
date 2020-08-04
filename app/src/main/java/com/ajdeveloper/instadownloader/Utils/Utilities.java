package com.ajdeveloper.instadownloader.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ajdeveloper.instadownloader.NaviDrawer;

public class Utilities {

    public static void startDownload(Context context, String url) {
        if (isPermissionGranted(context)) {
            DownloadManager.Request request = new DownloadManager.Request(
                    Uri.parse(url));
            Log.e("DownloadURL", url);
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
            request.setDestinationInExternalPublicDir(iConstants.DOWNLOAD_DIRECTORY + "/", URLUtil.guessFileName(url, null, null));
            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            if (dm != null) {
                dm.enqueue(request);
                Toast.makeText(context, "Downloading video", //To notify videodownloader Client that videodownloader file is being downloaded
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "Grant storage access to download anything", Toast.LENGTH_SHORT).show();
            askPermission(context);
        }
    }

    public static void prepVideo(WebView webView) {
        Log.e("VIDEO:", "PrepVideo");
//        webView.loadUrl("javascript:(function prepareVideo1() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;}}})()");
//        webView.loadUrl("javascript:( window.onload=prepareVideo1;)()");
        webView.loadUrl("javascript:(function prepareVideo() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;console.log(i);var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\",\"'+jsonData['videoID']+'\");');}}})()");
        webView.loadUrl("javascript:( window.onload=prepareVideo;)()");
    }

//    public static void prepVideo(WebView webView) {
//        Log.e("VIDEO:", "PrepVideo");
//        webView.loadUrl("javascript:(function prepareVideo() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;console.log(i);var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'Downloader.processVideo(\"'+jsonData['src']+'\");');}}})()");
//        webView.loadUrl("javascript:( window.onload=prepareVideo;)()");
//    }


    public static void checkPermission(Context context) {
        if (!isPermissionGranted(context))
            askPermission(context);
    }

    private static boolean isPermissionGranted(Context context) {
        return Build.VERSION.SDK_INT >= 23 && context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private static void askPermission(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    public static void showDialog(Context context, String title, String msg,
                                  String positive, String negative,
                                  boolean setNegative,
                                  DialogInterface.OnClickListener dialogClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg).setPositiveButton(positive, dialogClickListener);
        if (setNegative)
            builder.setNegativeButton(negative, dialogClickListener);
        builder.setCancelable(false);
        builder.show();
    }

    public static String getRegexBack(String html, String regex) {
        Matcher match = Pattern.compile(regex).matcher(html);
        if (match.find()) {
            return match.group(1);
        }
        return "";
    }

    public static void changeFragment(NaviDrawer naviDrawer, Fragment fragment) {
//        naviDrawer.getSupportFragmentManager().beginTransaction()
//                .replace(R.id.container, fragment)
//                .commitNow();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
