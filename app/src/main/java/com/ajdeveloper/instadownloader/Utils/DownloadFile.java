package com.ajdeveloper.instadownloader.Utils;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

public class DownloadFile implements iConstants {
    public static long downloadID;
    public static DownloadManager downloadManager;
    private static String mBaseFolderPath;

    public static void Downloading(Context context, String url, String title, String ext) {
        String cutTitle = title.replace(" ", "-").replace(".", "-") + ext;
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Request request = new Request(Uri.parse(url));
        request.setTitle(title);
        request.setDescription("Downloading");
        request.setNotificationVisibility(1);
        mBaseFolderPath = Environment.getExternalStorageDirectory() + File.separator + iConstants.DOWNLOAD_DIRECTORY;
        String[] bits = mBaseFolderPath.split("/");
        request.setDestinationInExternalPublicDir(bits[bits.length - 1], cutTitle.replaceAll("-", " "));
        request.allowScanningByMediaScanner();
        downloadID = downloadManager.enqueue(request);
        iUtils.ShowToast(context, "Downloading Start!");
    }
}
