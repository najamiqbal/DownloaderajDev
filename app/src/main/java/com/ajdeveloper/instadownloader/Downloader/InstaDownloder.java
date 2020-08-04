package com.ajdeveloper.instadownloader.Downloader;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import com.ajdeveloper.instadownloader.Activities.SplashActivity;
import com.ajdeveloper.instadownloader.Interfaces.CustomCallbackListener;
import com.ajdeveloper.instadownloader.R;

public class InstaDownloder {

    private RequestQueue queue;
    private String htmlSource = "";
    private String videoUrl = "";
    private String imageUrl = "";
    private String description = "";
    private String resultDesc;
    //    private ProgressDialog mProgressDialog;
    private String fileN = null, folderName = null;
    private String urlDesc, htmlResponse;
    private static String URL = "";
    private String path;
    private Uri uri;
    private String desc, resultz;
    private Context context;

    InstaDownloder(Context context) {
        createDirs();
        this.context = context;
        final ClipboardManager clipboard = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null) {
            try {
                ClipData.Item item = clip.getItemAt(0);
                URL = Source.getURL(item.getText().toString(), "(http(s)?:\\/\\/(.+?\\.)?[^\\s\\.]+\\.[^\\s\\/]{1,9}(\\/[^\\s]+)?)");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("InstaSaver URL: ", URL);
        } else {
            Toast.makeText(context, "Empty ClipBoard!", Toast.LENGTH_SHORT).show();
        }

        queue = Volley.newRequestQueue(context);

        checkFolder();
        if (checkForEmptyUrl(URL)) {
            viewPageSource();
        } else {
            Toast.makeText(context, "Please Copy Instagram Share URL", Toast.LENGTH_SHORT).show();
        }
    }

    private CustomCallbackListener customCallbackListener = null;

    private void sendResponse(boolean isSuccess, String msg) {
        if (customCallbackListener != null)
            customCallbackListener.onItemClicked(0);
    }

    public InstaDownloder(Context context, String url, CustomCallbackListener customCallbackListener) {
        createDirs();
        this.context = context;
        this.customCallbackListener = customCallbackListener;
        URL = Source.getURL(url, "(http(s)?:\\/\\/(.+?\\.)?[^\\s\\.]+\\.[^\\s\\/]{1,9}(\\/[^\\s]+)?)");
        Log.e("InstaSaver URL: ", URL);

        queue = Volley.newRequestQueue(context);

        checkFolder();
        if (checkForEmptyUrl(URL)) {
            viewPageSource();
        } else {
            Toast.makeText(context, "Please Copy Instagram Share URL", Toast.LENGTH_SHORT).show();
        }
    }

    private void createDirs() {
        new File(Constants.Parent + Constants.IMAGES).mkdirs();
        new File(Constants.Parent + Constants.VIDEOS).mkdirs();
    }

    private void viewPageSource() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.e("InstaSaver Response: ", response.toString());
                if (response != null) {

                    htmlSource = response.toString();
                    videoUrl = Source.getURL(htmlSource, "property=\"og:video\" content=\"([^\"]+)\"");
                    imageUrl = Source.getURL(htmlSource, "property=\"og:image\" content=\"([^\"]+)\"");
                    description = Source.getURL(htmlSource, "property=\"og:description\" content=\"([^\"]+)\"");

                    Log.e("InstaSaver Image URl: ", imageUrl);
                    Log.e("InstaSaver Description:", description);
                    Log.e("InstaSaver Video URL:", videoUrl);

                    try {
                        resultDesc = description.substring(description.indexOf("@") + 1, description.indexOf("Instagram") - 5);
                        Log.e("InstaSaverProfileName:", resultDesc);
                        if (resultDesc == null) {
                            resultDesc = "Profile Name";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (videoUrl.isEmpty()) {
                        String prefsImage = getPreference(context, "link");
//                        if (!prefsImage.equals(imageUrl)) {
//                            Glide.with(context)
//                                    .load(imageUrl)
//                                    .into(mainImageView);
//                            dwndImage(imageUrl);
                        newDownload(imageUrl);
                        getpostText(URL);
                        setPreference(context, "link", imageUrl);
//                        } else {
//                            //Already Downloaded
//                            sendResponse(false, "Already Downloaded");
//
//                        }
                    } else if (videoUrl.contains(".mp4")) {
                        String prefsVal = getPreference(context, "link");
//                        if (!prefsVal.equals(videoUrl)) {
//                            Glide.with(context)
//                                    .load(imageUrl)
//                                    .into(mainImageView);
//                            dwndImage(videoUrl);
                        newDownload(videoUrl);
                        getpostText(URL);
                        setPreference(context, "link", videoUrl);
//                        } else {
//                            //Already Downloaded
//                            sendResponse(false, "Already Downloaded");
//                        }
                    }

                } else {
                    Toast.makeText(context, "Failed to Fetch Data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Please Check If Your Url is Correct!", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }


    @SuppressLint("StaticFieldLeak")
    public class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                java.net.URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();

                input = connection.getInputStream();
                if (url.toString().contains(".mp4")) {
                    folderName = Constants.VIDEOS;
                    fileN = resultDesc + "InstaMsgSaver_" + UUID.randomUUID().toString().substring(0, 5) + ".mp4";
                    File filename = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                            folderName, fileN);
                    output = new FileOutputStream(filename);

                } else {
                    folderName = Constants.IMAGES;
                    fileN = resultDesc + "InstaMsgSaver_" + UUID.randomUUID().toString().substring(0, 5) + ".jpg";
                    File filename = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                            folderName, fileN);
                    output = new FileOutputStream(filename);
                }
                byte[] data = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        NotificationManager mNotifyManager;
        NotificationCompat.Builder mBuilder;

        @SuppressLint({"WakelockTimeout", "NewApi"})
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
//            mProgressDialog.show();
            mNotifyManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = (Build.VERSION.SDK_INT >= 26) ? getNotiBuilder() : new NotificationCompat.Builder(context);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
//            mProgressDialog.setIndeterminate(false);
//            mProgressDialog.setMax(100);
//            mProgressDialog.setProgress(progress[0]);
            if (progress[0] >= 100) {
                mBuilder.setContentIntent(setPendingIntent());
                // When videodownloader loop is finished, updates videodownloader notification
                mBuilder.setContentText("Download complete")
                        // Removes videodownloader progress bar
                        .setProgress(0, 0, false);
                mNotifyManager.notify(123, mBuilder.build());
                Log.e("DownloadProgress", progress[0] + "");
            } else {
                Log.e("DownloadProgress", progress[0] + "");
                mBuilder.setContentTitle("InstaDownloader")
                        .setContentText("Downloading...")
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo))
                        .setSmallIcon(R.mipmap.ico_logo);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
                    mBuilder.setSmallIcon(R.mipmap.ico_logo);

                // Do videodownloader "lengthy" operation 20 times
//                        for (incr = 0; incr <= 100; incr+=5) {
                // Sets videodownloader progress indicator to a max value, videodownloader
                // current completion percentage, and "determinate"
                // state

                mBuilder.setProgress(100, progress[0], false);
                // Displays videodownloader progress bar for videodownloader first time.
//                            mNotifyManager.notify(id, mBuilder.build());
                mNotifyManager.notify(123, mBuilder.build());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
//            mProgressDialog.dismiss();
            if (result != null) {
                sendResponse(false, "Download error: " + result);
            } else {
                sendResponse(true, fileN + " downloaded");
                MediaScannerConnection.scanFile(context,
                        new String[]{Environment.getExternalStorageDirectory().getAbsolutePath() +
                                folderName + fileN}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String newpath, Uri newuri) {
                                Log.i("ExternalStorage", "Scanned " + newpath + ":");
                                Log.i("ExternalStorage", "-> uri=" + newuri);
                                path = newpath;
                                uri = newuri;
                            }
                        });
            }

        }
    }

    private void newDownload(String url) {
//        mProgressDialog = new ProgressDialog(context);
//        mProgressDialog.setMessage("Downloading..");
//        mProgressDialog.setIndeterminate(true);
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        mProgressDialog.setCancelable(false);

//        final DownloadTask downloadTask = new DownloadTask(context);
//        downloadTask.execute(url);
        downloadFile(url);
    }

    private void getpostText(String textField) {
        if (Source.getURL(textField, "(http(s)?:\\/\\/(.+?\\.)?[^\\s\\.]+\\.[^\\s\\/]{1,9}(\\/[^\\s]+)?)").isEmpty()) {
            Log.e("InstaSaver incorrect", "Url is not Correct");
        } else {
            urlDesc = Source.getURL(textField, "(http(s)?:\\/\\/(.+?\\.)?[^\\s\\.]+\\.[^\\s\\/]{1,9}(\\/[^\\s]+)?)");
            AsyncHttpClient client = new AsyncHttpClient();
            client.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36");
            client.get(textField, new TextHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    htmlResponse = responseString;
                    Log.e("InstaSaver HtML: ", htmlResponse);
                    try {
                        int start = htmlResponse.indexOf("\"text\":") + 9;
                        String starting = htmlResponse.substring(start);
                        int end = starting.indexOf("\"}}]}");
                        desc = starting.substring(0, end);
                        resultz = StringEscapeUtils.unescapeJava(desc);
                        Log.e("InstaSaver End", resultz);
                        if (resultz.contains("created_at") || resultz.contains("href")) {
                            resultz = "";
                        }
                        if (!resultz.contains("#")) {
//                            btn_caption.setVisibitlity(View.GONE);
                        }
                        try {
//                            captionText.setText(resultz);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        desc = "";
                    }
                }
            });
        }
    }

    private static final String PREFS_NAME = "INSTA_MESSAGE_SAVER";

    private static void setPreference(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static String getPreference(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, "defaultValue");
    }

    private boolean checkForEmptyUrl(String url) {
        Pattern p = Pattern.compile("instagram.com");
        Matcher m = p.matcher(url);//replace with string to compare
        return m.find();
    }

    private void checkFolder() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/instasaver";
        File dir = new File(path);
        boolean isDirectoryCreated = dir.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = dir.mkdir();
        }
        if (isDirectoryCreated) {
            // do something\
            Log.e("InstaSaver Folder", "Already Created");
        }
    }

    private void downloadFile(String url) {

        if (url.contains(".mp4")) {
            folderName = Constants.VIDEOS;
            fileN = resultDesc + "InstaSaver_" + UUID.randomUUID().toString().substring(0, 5) + ".mp4";
        } else {
            folderName = Constants.IMAGES;
            fileN = resultDesc + "InstaSaver_" + UUID.randomUUID().toString().substring(0, 5) + ".jpg";
        }
//        String fileName = url.substring(url.lastIndexOf('/') + 1);
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);
        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("InstaSaver")
                .setDescription("Downloading " + fileN)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/FreeDownloader/" + fileN)
                .setVisibleInDownloadsUi(true)
                .allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        Toast.makeText(context, "Download Started", Toast.LENGTH_SHORT).show();
        dm.enqueue(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationCompat.Builder getNotiBuilder() {
        String NOTIFICATION_CHANNEL_ID = "InstaSaverChannel";
        String channelName = "InstaDownloader";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        return new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
    }

    private PendingIntent setPendingIntent() {
        Intent intent = new Intent(context, SplashActivity.class);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    private String getDownloadSpeed(long bytesPerSec) {
        double kb = (double) bytesPerSec / 1024;
        double mb = kb / 1024;
        if (kb >= 1)
            return (int) kb + " kbps";
        else if (mb >= 1)
            return (int) mb + " mbps";
        return bytesPerSec + " bps";
    }

}
