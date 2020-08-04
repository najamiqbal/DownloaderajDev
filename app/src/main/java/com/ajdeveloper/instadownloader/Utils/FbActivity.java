//package videodownloader.hdvideos.downloadvideos.Utils;
//
//import android.annotation.SuppressLint;
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.media.MediaScannerConnection;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.PowerManager;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.webkit.CookieManager;
//import android.webkit.CookieSyncManager;
//import android.webkit.JavascriptInterface;
//import android.webkit.WebChromeClient;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.Button;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import com.daimajia.numberprogressbar.NumberProgressBar;
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.InterstitialAd;
//import com.techx.socio.Utils.Constant;
//import com.techx.socio.Utils.Constants;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.UUID;
//
//public class FbActivity extends AppCompatActivity {
//
//    public static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 123;
//    private static final String LOG_TAG = "EXAMPLE";
//    private static String URL = "https://www.facebook.com/login/";
//    Toolbar mToolbar;
//    ProgressDialog mProgressDialog;
//    String fileN = null;
//    boolean result;
//    String urlString;
//    Dialog main_dialog, downloadDialog;
//    @SuppressLint("StaticFieldLeak")
//    private WebView webo;
//    private AlertDialog.Builder dialogBuilder;
//    private AlertDialog dialog;
//    private Button streamButton, downloadButton, cancelButton;
//    private ProgressBar progress;
//    private SwipeRefreshLayout recyclerLayout;
//    private AdView mAdView;
//    InterstitialAd mInterstitialAd;
//    AdRequest adRequestint;
//
//    public static boolean isConnectingToInternet(Context context) {
//
//        ConnectivityManager cm =
//                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        return activeNetwork != null &&
//                activeNetwork.isConnectedOrConnecting();
//    }
//
//    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
//        setContentView(R.layout.activity_fb);
//        mToolbar =  findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        progress =  findViewById(R.id.progressBar);
//        progress.setVisibility(View.GONE);
//        if (!isConnectingToInternet(this)) {
//            Toast.makeText(this, "Please Connect to Internet", Toast.LENGTH_LONG).show();
//        }
//        loadInterstitialAd();
//    }
//
//    private void loadInterstitialAd() {
//        adRequestint = new AdRequest.Builder().build();
//        mInterstitialAd = new InterstitialAd(getApplicationContext());
//        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_id));
//        mInterstitialAd.loadAd(adRequestint);
//    }
//    private void showInterstitial() {
//        if (mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//        }
//    }
//
//    @JavascriptInterface
//    public void processVideo(String str, String str2) {
//        Log.e("WEBVIEWJS", "RUN");
//        Log.e("WEBVIEWJS", str);
//        Bundle args = new Bundle();
//        args.putString("vid_data", str);
//        urlString = str;
//        runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                // put your code to show dialog here.
//                LayoutInflater dialogLayout = LayoutInflater.from(FbActivity.this);
//                View DialogView = dialogLayout.inflate(R.layout.dialog_download, null);
//                main_dialog = new Dialog(FbActivity.this, R.style.MyDialogTheme);
//                main_dialog.setContentView(DialogView);
//                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//                lp.copyFrom(main_dialog.getWindow().getAttributes());
//                lp.width = (getResources().getDisplayMetrics().widthPixels);
//                lp.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.65);
//                main_dialog.getWindow().setAttributes(lp);
//                streamButton =  DialogView.findViewById(R.id.streamButton);
//                downloadButton =  DialogView.findViewById(R.id.downloadButton);
//                cancelButton =  DialogView.findViewById(R.id.cancelButton);
//                mAdView = DialogView.findViewById(R.id.adView);
//                AdRequest adRequest = new AdRequest.Builder().addTestDevice("0224C93FFD644350DCD7F3D7557C6A5C").build();
//                mAdView.loadAd(adRequest);
//                main_dialog.setCancelable(false);
//                main_dialog.setCanceledOnTouchOutside(false);
//                main_dialog.show();
//
////                dialogBuilder = new AlertDialog.Builder(MainActivity.this , R.style.MyDialogTheme);
////                View view = getLayoutInflater().inflate(R.layout.dialog_download, null);
////                streamButton =  view.findViewById(R.id.streamButton);
////                downloadButton = view.findViewById(R.id.downloadButton);
////                cancelButton =  view.findViewById(R.id.cancelButton);
////                adNativeView = (NativeExpressAdView)view.findViewById(R.id.nativeAD);
////                dialogBuilder.setView(view);
////                dialog = dialogBuilder.create();
////                adNativeView = (NativeExpressAdView)view.findViewById(R.id.nativeAD);
////                AdRequest request = new AdRequest.Builder()
////                        .addTestDevice("C69095F3C24675F5F8C9B5031B0E6EEB")
////                        .build();
////                adNativeView.loadAd(request);
////                dialog.show();
//                streamButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(FbActivity.this, "Streaming", Toast.LENGTH_SHORT).show();
//
//                        Intent intent = new Intent(FbActivity.this, VideoPlayer.class);
//                        intent.putExtra("video_url", urlString);
//                        startActivity(intent);
//                        main_dialog.dismiss();
//                    }
//                });
//                downloadButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(FbActivity.this, "Downloading", Toast.LENGTH_SHORT).show();
//                        newDownload(urlString);
//                        main_dialog.dismiss();
//                    }
//                });
//                cancelButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        main_dialog.dismiss();
//                    }
//                });
//            }
//        });
//    }
//
//    public void setValue(int progress) {
//        this.progress.setProgress(progress);
//        if (progress >= 100) // code to be added
//            this.progress.setVisibility(View.GONE);
//    }
//
//    private void createPopupDialog() {
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_fb, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_go_to_fb:
//                gotoFB();
//                break;
//            case R.id.action_refresh:
//                if (webo != null) {
//                    webo.reload();
//                }
//                break;
//            case android.R.id.home:
//                onBackPressed();
//                break;
//            default:
//                break;
//        }
//        return true;
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            switch (keyCode) {
//                case KeyEvent.KEYCODE_BACK:
//                    if (webo.canGoBack()) {
//                        webo.goBack();
//                    } else {
//                        finish();
//                    }
//                    return true;
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    public void gotoFB() {
//        webo = findViewById(R.id.webViewFb);
//        webo.getSettings().setJavaScriptEnabled(true);
//        webo.addJavascriptInterface(this, "FBDownloader");
//        webo.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                progress.setVisibility(View.VISIBLE);
//                FbActivity.this.setValue(newProgress);
//                super.onProgressChanged(view, newProgress);
//            }
//        });
//        webo.setWebViewClient(new WebViewClient() {
//
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
////                progress.setVisibility(View.VISIBLE);
////                MainActivity.this.progress.setProgress(0);
//                super.onPageStarted(view, url, favicon);
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                FbActivity.this.webo.loadUrl("javascript:(function() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\");');}}})()");
//                Log.e("WEBVIEWFIN", url);
//                progress.setVisibility(View.GONE);
////                MainActivity.this.progress.setProgress(100);
//                super.onPageFinished(view, url);
//            }
//
//            @Override
//            public void onLoadResource(WebView view, String url) {
//                FbActivity.this.webo.loadUrl("javascript:(function prepareVideo() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;console.log(i);var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\",\"'+jsonData['videoID']+'\");');}}})()");
//                FbActivity.this.webo.loadUrl("javascript:( window.onload=prepareVideo;)()");
//            }
//        });
//        CookieManager cookieManager = CookieManager.getInstance();
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            CookieSyncManager.createInstance(this);
//        }
//        cookieManager.setAcceptCookie(true);
//        webo.loadUrl(URL);
//    }
//
//    public void newDownload(String url) {
//        final DownloadTask downloadTask = new DownloadTask(FbActivity.this);
//        downloadTask.execute(url);
//    }
//
//    private class MyBrowser extends WebViewClient {
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);
//            return true;
//        }
//    }
//
//    public class DownloadTask extends AsyncTask<String, Integer, String> {
//
//        private Context context;
//        private PowerManager.WakeLock mWakeLock;
//        private NumberProgressBar bnp;
//
//        public DownloadTask(Context context) {
//            this.context = context;
//        }
//
//        @Override
//        protected String doInBackground(String... sUrl) {
//            InputStream input = null;
//            OutputStream output = null;
//            HttpURLConnection connection = null;
//            try {
//                java.net.URL url = new URL(sUrl[0]);
//                connection = (HttpURLConnection) url.openConnection();
//                connection.connect();
//
//                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                    return "Server returned HTTP " + connection.getResponseCode()
//                            + " " + connection.getResponseMessage();
//                }
//
//                int fileLength = connection.getContentLength();
//
//                input = connection.getInputStream();
//                fileN = "FbDownloader+" + UUID.randomUUID().toString().substring(0, 10) + ".mp4";
//                File filename = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+
//                        getString(R.string.app_name), fileN);
//                output = new FileOutputStream(filename);
//
//                byte data[] = new byte[4096];
//                long total = 0;
//                int count;
//                while ((count = input.read(data)) != -1) {
//                    if (isCancelled()) {
//                        input.close();
//                        return null;
//                    }
//                    total += count;
//                    if (fileLength > 0)
//                        publishProgress((int) (total * 100 / fileLength));
//                    output.write(data, 0, count);
//                }
//            } catch (Exception e) {
//                return e.toString();
//            } finally {
//                try {
//                    if (output != null)
//                        output.close();
//                    if (input != null)
//                        input.close();
//                } catch (IOException ignored) {
//                }
//
//                if (connection != null)
//                    connection.disconnect();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//                    getClass().getName());
//            mWakeLock.acquire();
//            LayoutInflater dialogLayout = LayoutInflater.from(FbActivity.this);
//            View DialogView = dialogLayout.inflate(R.layout.progress_dialog, null);
//            downloadDialog = new Dialog(FbActivity.this, R.style.CustomAlertDialog);
//            downloadDialog.setContentView(DialogView);
//            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//            lp.copyFrom(downloadDialog.getWindow().getAttributes());
//            lp.width = (getResources().getDisplayMetrics().widthPixels);
//            lp.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.65);
//            downloadDialog.getWindow().setAttributes(lp);
//            final Button cancel =  DialogView.findViewById(R.id.cancel_btn);
//            cancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //stopping videodownloader Asynctask
//                    cancel(true);
//                    downloadDialog.dismiss();
//
//                }
//            });
//            downloadDialog.setCancelable(false);
//            downloadDialog.setCanceledOnTouchOutside(false);
//            bnp = DialogView.findViewById(R.id.number_progress_bar);
//            bnp.setProgress(0);
//            bnp.setMax(100);
//            downloadDialog.show();
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... progress) {
//            super.onProgressUpdate(progress);
//            bnp.setProgress(progress[0]);
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            mWakeLock.release();
//            downloadDialog.dismiss();
//            showInterstitial();
//            if (result != null)
//                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
//            else
//                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
//            MediaScannerConnection.scanFile(FbActivity.this,
//                    new String[]{Environment.getExternalStorageDirectory().getAbsolutePath() +
//                            getString(R.string.app_name) + fileN}, null,
//                    new MediaScannerConnection.OnScanCompletedListener() {
//                        public void onScanCompleted(String newpath, Uri newuri) {
//                            Log.i("ExternalStorage", "Scanned " + newpath + ":");
//                            Log.i("ExternalStorage", "-> uri=" + newuri);
//                        }
//                    });
//
//        }
//    }
//}
