package com.ajdeveloper.instadownloader.FragmentsN;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ListCallbackSingleChoice;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import org.jsoup.Jsoup;

import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.ajdeveloper.instadownloader.Download_HLS.DownloadService;
import com.ajdeveloper.instadownloader.Downloader.Constants;
import com.ajdeveloper.instadownloader.Downloader.InstaDownloder;
import com.ajdeveloper.instadownloader.Interface.VideoApiInterface;
import com.ajdeveloper.instadownloader.Interfaces.CustomCallbackListener;
import com.ajdeveloper.instadownloader.NaviDrawer;
import com.ajdeveloper.instadownloader.Network.RetrofitClientInstance;
import com.ajdeveloper.instadownloader.R;
import com.ajdeveloper.instadownloader.Utils.AdsUtil;
import com.ajdeveloper.instadownloader.Utils.BSUtils_D;
import com.ajdeveloper.instadownloader.Utils.Constant;
import com.ajdeveloper.instadownloader.Utils.DialogUtils;
import com.ajdeveloper.instadownloader.Utils.UtilMethods;
import com.ajdeveloper.instadownloader.VimeoView;
import com.ajdeveloper.instadownloader.entity.VideoEntityJson;
import com.ajdeveloper.instadownloader.model.HLS.Example;
import com.ajdeveloper.instadownloader.model.HLS.Format;
import com.ajdeveloper.instadownloader.presenter.VimeoPresenter;

@SuppressLint("WrongConstant")
public class download_url extends Fragment implements VimeoView {
    private static final String TAG = "download_url";
    AlertDialog alertDialog;
    VideoApiInterface apiInterface;
    String currentUrl;
    Builder dialogBuilder;
    ImageView downloadBtn;
    private String downloadUrl = "";
    private Context mContext;
    private final BroadcastReceiver mYourBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
        }
    };
    String patternInsta = "https://www.instagram.com/p/.";
    ProgressBar progressBarDialoge;
    private ProgressDialog progressDialog;
    View rootView;
    SharedPreferences sharedPreferences;
    private boolean type;
    ArrayList<String> vidList = new ArrayList<>();
    EditText videoUrl;
    VimeoPresenter vimeoPresenter;
    WebView webView;

    public download_url() {}

    @SuppressLint("StaticFieldLeak")
    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        ProgressDialog mProgressDialog;

        DownloadFileFromURL() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.mProgressDialog = new ProgressDialog(download_url.this.getContext());
            this.mProgressDialog.setMessage("Downloading Video.....");
            this.mProgressDialog.setCancelable(false);
            this.mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strArr) {
            URL url;
            download_url.this.type = false;
            try {
                String document = Jsoup.connect(strArr[0]).get().toString();
                int indexOf = document.indexOf("\"", document.indexOf("\"video_url\"") + 11) + 1;
                String substring = document.substring(indexOf, document.indexOf("\"", indexOf));
                if (substring.equalsIgnoreCase("en")) {
                    int indexOf2 = document.indexOf("\"", document.indexOf("display_url") + 13) + 1;
                    String substring2 = document.substring(indexOf2, document.indexOf("\"", indexOf2));
                    download_url.this.type = false;
                    url = new URL(substring2);
                } else {
                    url = new URL(substring);
                    download_url.this.type = true;
                }
                return url.toString();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                return null;
            }
        }

        protected void onPostExecute(String str) {
            ProgressDialog progressDialog = this.mProgressDialog;
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            download_url.this.downloadVideo(str);
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public Context getContext() {
        return this.mContext;
    }

    public static download_url newInstance() {
        return new download_url();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.apiInterface = (VideoApiInterface) RetrofitClientInstance.getRetrofitInstance().create(VideoApiInterface.class);
    }

    private Context context;
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.rootView = layoutInflater.inflate(R.layout.fragment_download_url, viewGroup, false);
        ButterKnife.bind(getActivity());
        ((NaviDrawer) getContext()).getActivityComponent().inject(this);
        this.vimeoPresenter.setView(this);
        frameLayout = this.rootView.findViewById(R.id.fl_ad_place_holder);
        this.videoUrl = this.rootView.findViewById(R.id.et_url);
        this.downloadBtn = this.rootView.findViewById(R.id.btn_url);
        this.downloadBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String url = videoUrl.getText().toString();
                if (url.contains("instagram.com")) {
                    new InstaDownloder(context, url, new CustomCallbackListener() {

                        @Override
                        public void onItemClicked(int position) {
                        }
                    });
                    videoUrl.setText("");
                    return;
                }
                download_url.this.downloadBtnClicked();
            }
        });
        context = getContext();
        refreshAd();
        return this.rootView;
    }

    @JavascriptInterface
    public void processVideo(String str, String str2) {
        try {
            showDownloadDialog(str2, str);
        } catch (Exception unused) {
            showToast("Download Failed");
        }
    }

    private void showDownloadDialog(final String str, final String str2) throws Exception {
        if (!storageAllowed()) {
            ActivityCompat.requestPermissions(getActivity(), Constant.PERMISSIONS_STORAGE, 1);
            showToast("Kindly grant videodownloader request and try again");
            return;
        }
        Builder builder = new Builder(getActivity());
        builder.setTitle("Download Video?");
        builder.setMessage("Click download to start downloading.");
        builder.setCancelable(false).setPositiveButton("Download", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                download_url.this.alertDialog.cancel();
                StringBuilder sb = new StringBuilder();
                sb.append(Environment.getExternalStorageDirectory());
                sb.append(File.separator);
                sb.append("Download/FreeDownloader");
                sb.append(File.separator);
                String sb2 = sb.toString();
                if (!new File(sb2).exists()) {
                    new File(sb2).mkdir();
                }
                StringBuilder sb3 = new StringBuilder();
                sb3.append("file://");
                sb3.append(sb2);
                sb3.append("/");
                sb3.append(str);
                sb3.append(".mp4");
                String sb4 = sb3.toString();
                Request request = new Request(Uri.parse(str2));
                request.setDestinationUri(Uri.parse(sb4));
                request.setNotificationVisibility(1);
                FragmentActivity activity = download_url.this.getActivity();
                download_url.this.getActivity().getApplicationContext();
                ((DownloadManager) activity.getSystemService("download")).enqueue(request);
                dialogInterface.cancel();
                download_url.this.loadInterstitialAd();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    private void downloadBtnClicked() {
        if (!storageAllowed()) {
            ActivityCompat.requestPermissions(getActivity(), Constant.PERMISSIONS_STORAGE, 1);
            showToast("Kindly grant videodownloader request and try again");
        } else {
            String trim = this.videoUrl.getText().toString().trim();
            if (trim.equalsIgnoreCase("")) {
                showToast("Paste video url here.");
            } else if (trim.contains("facebook.com")) {
                DOwnloadFromFb(trim);
            } else if (trim.contains("vimeo.com")) {
                if (trim.startsWith("https") || trim.startsWith("http")) {
                    DOwnloadFromVimeo(trim);
                } else {
                    showToast("Paste Valid Video Url");
                }
            } else if (trim.contains("dailymotion.com")) {
                DownloadFronDM(trim);
            } else if (trim.contains("twitter.com")) {
                DownloadFromTwitter(trim);
            } else if (trim.contains("https://www.instagram.com")) {
                DownloadFronInsta(trim);
            } else if (trim.contains("youtu.be")) {
                showYoutubeDialog();
            } else {
                showToast("Invalid url.");
            }
        }
    }

    private void showYoutubeDialog() {
        @SuppressLint("ResourceType") final Dialog dialog = new Dialog(getContext(), 2131820891);
        dialog.setContentView(R.layout.dialog_youtube);
        ((Button) dialog.findViewById(R.id.btnOkDialogYoutube)).setOnClickListener(new OnClickListener() {
//            private final /* synthetic */ Dialog f$0;
//
//            {
//                this.f$0 = r1;
//            }

            public final void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void DOwnloadFromFb(String str) {
        this.dialogBuilder = new Builder(getActivity());
        View inflate = getLayoutInflater().inflate(R.layout.fb_preview_layout, null);
        this.dialogBuilder.setView(inflate);
        this.webView = (WebView) inflate.findViewById(R.id.web_view);
        ((ImageView) inflate.findViewById(R.id.cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                download_url.this.alertDialog.cancel();
            }
        });
        this.progressBarDialoge = (ProgressBar) inflate.findViewById(R.id.progressBar);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setPluginState(PluginState.ON);
        this.webView.getSettings().setBuiltInZoomControls(true);
        this.webView.getSettings().setDisplayZoomControls(true);
        this.webView.getSettings().setUseWideViewPort(true);
        this.webView.getSettings().setLoadWithOverviewMode(true);
        this.webView.addJavascriptInterface(this, "FBDownloader");
        this.webView.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
                download_url.this.progressBarDialoge.setVisibility(0);
            }

            public void onPageFinished(WebView webView, String str) {
                download_url.this.progressBarDialoge.setVisibility(4);
                download_url.this.webView.loadUrl("javascript:(function() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\");');}}})()");
            }

            public void onLoadResource(WebView webView, String str) {
                download_url.this.progressBarDialoge.setProgress(download_url.this.webView.getProgress());
                download_url.this.webView.loadUrl("javascript:(function prepareVideo() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;console.log(i);var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\",\"'+jsonData['videoID']+'\");');}}})()");
                download_url.this.webView.loadUrl("javascript:( window.onload=prepareVideo;)()");
            }
        });
        CookieSyncManager.createInstance(getActivity());
        CookieManager.getInstance().setAcceptCookie(true);
        CookieSyncManager.getInstance().startSync();
        this.webView.loadUrl(str);
        this.dialogBuilder.setCancelable(false);
        this.dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        this.alertDialog = this.dialogBuilder.create();
        this.alertDialog.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
                download_url.this.webView.destroy();
            }
        });
        this.alertDialog.show();
    }

    private void DOwnloadFromVimeo(String str) {
        if (str.substring(18) != null && !str.substring(18).isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("https://player.vimeo.com/video/");
            sb.append(str.substring(18));
            this.currentUrl = sb.toString();
            showToast("Loading...");
            this.vimeoPresenter.getVideoList(this.currentUrl);
        }
        if (str.startsWith("www.vimeo.com")) {
            showToast("Paste Valid URL here ");
        } else {
            showToast("Invalid url!");
        }
    }

    public void DownloadFronDM(String str) {
        if (!str.contains("video")) {
            showToast("invalid url.");
            return;
        }
        if (str.contains("playlist")) {
            str = str.substring(0, str.indexOf("?"));
        }
        DialogUtils.showSimpleProgressDialog(getActivity());
        this.apiInterface.getUrl(str).enqueue(new Callback<Example>() {
            public void onResponse(Call<Example> call, Response<Example> response) {
                DialogUtils.closeProgressDialog();
                if (response != null) {
                    Example example = (Example) response.body();
                    if (example != null) {
                        download_url.this.manageResponse(example);
                    } else {
                        download_url.this.showToast("Something went wrong");
                    }
                } else {
                    download_url.this.showToast("Check Internet Connection");
                }
            }

            public void onFailure(Call<Example> call, Throwable th) {
                DialogUtils.closeProgressDialog();
                Log.i("Video ", th.getMessage());
            }
        });
    }

    public void loadInterstitialAd() {
        AdsUtil.getInstance(getContext()).loadInterstitialAd("", "ad_download_button", new AdsUtil.AdsCallBack() {
            public void onAdFailedToLoad(String str, String str2) {
            }
        });
    }

    public void setVideoList(final List<VideoEntityJson> list) {
        final String[] strArr = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            strArr[i] = ((VideoEntityJson) list.get(i)).quality;
        }
        new MaterialDialog.Builder(getActivity()).title(this.sharedPreferences.getString(getString(R.string.video_title_key), "")).items(strArr).itemsCallbackSingleChoice(-1, new ListCallbackSingleChoice() {
            public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                if (strArr.length == 0) {
                    download_url.this.showToast("can't download.");
                    return false;
                }
                String str = (String) Arrays.asList(strArr).get(i);
                for (VideoEntityJson videoEntityJson : list) {
                    if (str.equals(videoEntityJson.quality)) {
                        VimeoPresenter vimeoPresenter = download_url.this.vimeoPresenter;
                        String str2 = videoEntityJson.url;
                        StringBuilder sb = new StringBuilder();
                        sb.append(download_url.this.sharedPreferences.getString(download_url.this.getString(R.string.video_title_key), ""));
                        sb.append(".mp4");
                        vimeoPresenter.downloadVideo(str2, sb.toString(), download_url.this.sharedPreferences.getString(download_url.this.getString(R.string.video_thumbnail_key), ""));
                        download_url.this.loadInterstitialAd();
                    }
                }
                return true;
            }
        }).positiveText((int) R.string.download_action).negativeText((int) R.string.cancel_action).show();
    }

    private void DownloadFromTwitter(String str) {
        this.progressDialog = new ProgressDialog(getActivity());
        this.progressDialog.setCancelable(false);
        this.progressDialog.setIndeterminate(true);
        this.progressDialog.setMessage("Fetching tweet...");
        if (!storageAllowed()) {
            ActivityCompat.requestPermissions(getActivity(), Constant.PERMISSIONS_STORAGE, 1);
            showToast("Kindly grant videodownloader request and try again");
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss");
        StringBuilder sb = new StringBuilder();
        sb.append("Twitter-");
        sb.append(simpleDateFormat.format(new Date()));
        sb.append(".mp4");
        downloadVideo(str, sb.toString());
    }

    private void downloadVideo(String str, String str2) {
        this.progressDialog = new ProgressDialog(getActivity());
        this.progressDialog.setCancelable(false);
        this.progressDialog.setIndeterminate(true);
        if (str2.endsWith(".mp4")) {
            this.progressDialog.setMessage("Fetching video...");
        } else {
            this.progressDialog.setMessage("Fetching gif...");
        }
        if (!storageAllowed()) {
            ActivityCompat.requestPermissions(getActivity(), Constant.PERMISSIONS_STORAGE, 1);
            this.progressDialog.dismiss();
            showToast("Kindly grant videodownloader request and try again");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(Environment.getExternalStorageDirectory());
            sb.append(File.separator);
            sb.append("Download/FreeDownloader");
            sb.append(File.separator);
            String sb2 = sb.toString();
            if (!new File(sb2).exists()) {
                new File(sb2).mkdir();
            }
            String str3 = this.downloadUrl;
            if (str3 == null || TextUtils.isEmpty(str3)) {
                showToast("Download url not found!");
                return;
            }
            Uri parse = Uri.parse(str.trim());
            if (parse == null) {
                showToast("Download url not found!");
                return;
            }
            StringBuilder sb3 = new StringBuilder();
            sb3.append("file://");
            sb3.append(sb2);
            sb3.append("/");
            sb3.append(str2);
            String sb4 = sb3.toString();
            Request request = new Request(parse);
            request.setDestinationUri(Uri.parse(sb4));
            request.setNotificationVisibility(1);
            FragmentActivity activity = getActivity();
            getActivity();
            ((DownloadManager) activity.getSystemService("download")).enqueue(request);
            this.progressDialog.dismiss();
            loadInterstitialAd();
        }
    }

    private void DownloadFronInsta(String str) {
        if (str.length() <= 27 || !str.contains("https://www.instagram.com/p/")) {
            showToast("Invalid url.");
            return;
        }
        if (!storageAllowed()) {
            ActivityCompat.requestPermissions(getActivity(), Constant.PERMISSIONS_STORAGE, 1);
            showToast("Kindly grant videodownloader request and try again");
        } else if (checkURL(str)) {
            new DownloadFileFromURL().execute(new String[]{str});
        } else {
            showToast("Wrong URL");
        }
    }

    private boolean storageAllowed() {
        boolean z = true;
        if (VERSION.SDK_INT < 23) {
            return true;
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            z = false;
        }
        return z;
    }

    private void downloadVideo(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory());
        sb.append(File.separator);
        sb.append("Download/FreeDownloader");
        sb.append(File.separator);
        String sb2 = sb.toString();
        if (!new File(sb2).exists()) {
            new File(sb2).mkdir();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        StringBuilder sb3 = new StringBuilder();
        sb3.append("file://");
        sb3.append(sb2);
        sb3.append("/Video_");
        sb3.append(simpleDateFormat.format(new Date()));
        sb3.append(".mp4");
        String sb4 = sb3.toString();
        if (str == null || TextUtils.isEmpty(str)) {
            showToast("Download url not found!");
            return;
        }
        Uri parse = Uri.parse(str.trim());
        if (parse == null) {
            showToast("Download url not found!");
            return;
        }
        Request request = new Request(parse);
        request.setDestinationUri(Uri.parse(sb4));
        request.setNotificationVisibility(1);
        Context context = getContext();
        getContext().getApplicationContext();
        ((DownloadManager) context.getSystemService("download")).enqueue(request);
        new UtilMethods(getContext()).showCustomToast("Download Started...", 1);
    }

    public boolean checkURL(String str) {
        Matcher matcher = Pattern.compile(this.patternInsta).matcher(str);
        if (matcher.find()) {
            PrintStream printStream = System.out;
            StringBuilder sb = new StringBuilder();
            sb.append("Found value: ");
            sb.append(matcher.group(0));
            printStream.println(sb.toString());
            return true;
        }
        System.out.println("NO MATCH");
        return false;
    }

    private void showToast(String str) {
        try {
            if (getContext() != null) {
                Toast.makeText(getContext(), str, 0).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void manageResponse(Example example) {
        if (example != null) {
            List<Format> formats = example.getInfo().getFormats();
            ArrayList arrayList = new ArrayList();
            for (Format format : formats) {
                String formatId = format.getFormatId();
                if (formatId.contains("hls-144-0")) {
                    format.setFormatId("144");
                    arrayList.add(format);
                } else if (formatId.contains("hls-240-0")) {
                    format.setFormatId("240");
                    arrayList.add(format);
                } else if (formatId.contains("hls-380-0")) {
                    format.setFormatId("380");
                    arrayList.add(format);
                } else if (formatId.contains("hls-480-0")) {
                    format.setFormatId("480");
                    arrayList.add(format);
                } else if (formatId.contains("hls-720-0")) {
                    format.setFormatId("720");
                    arrayList.add(format);
                }
            }
            showDM_DownloadSheet(arrayList, example.getInfo().getDescription());
        }
    }

    private void showDM_DownloadSheet(List<Format> list, String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        StringBuilder sb123 = new StringBuilder();
        sb123.append("Video_");
        sb123.append(simpleDateFormat.format(new Date()));
        final String sb12 = sb123.toString();
        new BSUtils_D(getContext()).showListSheet(str, "", list, TAG, new BSUtils_D.BSCallBack() {
            public void onBsHidden() {
            }

            public void onDownloadClicked(Format format) {
                StringBuilder sb = new StringBuilder();
                sb.append(Environment.getExternalStorageDirectory());
                sb.append(File.separator);
                sb.append("Download/FreeDownloader");
                sb.append(File.separator);
                String sb2 = sb.toString();
                Intent intent = new Intent(download_url.this.getContext(), DownloadService.class);
                intent.putExtra("URL", format.getUrl());
                intent.putExtra("TITLE", sb12);
                intent.putExtra("PATH", Constants.Parent + Constants.VIDEOS);
                if (VERSION.SDK_INT >= 26) {
                    download_url.this.mContext.startForegroundService(intent);
                } else {
                    download_url.this.mContext.startService(intent);
                }
            }
        });
    }



    /**
     * Populates a {@link UnifiedNativeAdView} object with data from a given
     * {@link UnifiedNativeAd}.
     *
     * @param nativeAd videodownloader object containing videodownloader ad's assets
     * @param adView   videodownloader view to be populated
     */
    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set videodownloader media view. Media content will be automatically populated in videodownloader media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells videodownloader Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate videodownloader adView's MediaView
        // with videodownloader media content from this native ad.
        adView.setNativeAd(nativeAd);

        // Get videodownloader video controller for videodownloader ad. One will always be provided, even if videodownloader ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getVideoController();

        // Updates videodownloader UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {

            // Create a new VideoLifecycleCallbacks object and pass it to videodownloader VideoController. The
            // VideoController will call methods on this object when events occur in videodownloader video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in videodownloader same UI location.
                    super.onVideoEnd();
                    refreshAd();
                }
            });
        } else {
            //No video in nativeAd
        }
    }

    /**
     * Creates a request for a new native ad based on videodownloader boolean parameters and calls videodownloader
     * corresponding "populate" method when one is successfully returned.
     */
    private FrameLayout frameLayout;
    private UnifiedNativeAd nativeAd;

    private void refreshAd() {

        AdLoader.Builder builder = new AdLoader.Builder(context, getString(R.string.native_advanced_ad_keys));

        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            // OnUnifiedNativeAdLoadedListener implementation.
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                // You must call destroy on old ads when you are done with them,
                // otherwise you will have a memory leak.
                if (nativeAd != null) {
                    nativeAd.destroy();
                }
                nativeAd = unifiedNativeAd;
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
                        .inflate(R.layout.ad_unified, null);
                populateUnifiedNativeAdView(unifiedNativeAd, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);
            }

        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                refreshAd();
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

}
