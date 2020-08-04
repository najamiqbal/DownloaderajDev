package com.ajdeveloper.instadownloader.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;
import com.ajdeveloper.instadownloader.Adapters.SuggestionListAdapter;
import com.ajdeveloper.instadownloader.Download_HLS.DownloadService;
import com.ajdeveloper.instadownloader.Downloader.Constants;
import com.ajdeveloper.instadownloader.Interface.RemoteApiService;
import com.ajdeveloper.instadownloader.Interface.VideoApiInterface;
import com.ajdeveloper.instadownloader.Network.RetrofitClientInstance;
import com.ajdeveloper.instadownloader.ObservableWebView;
import com.ajdeveloper.instadownloader.R;
import com.ajdeveloper.instadownloader.Utils.AdsUtil;
import com.ajdeveloper.instadownloader.Utils.BSUtils;
import com.ajdeveloper.instadownloader.Utils.BSUtils_D;
import com.ajdeveloper.instadownloader.Utils.DialogUtils;
import com.ajdeveloper.instadownloader.Utils.FileSizes;
import com.ajdeveloper.instadownloader.Utils.ScriptUtil;
import com.ajdeveloper.instadownloader.Utils.UtilMethods;
import com.ajdeveloper.instadownloader.Utils.ViewUtil;
import com.ajdeveloper.instadownloader.VimeoView;
import com.ajdeveloper.instadownloader.entity.VideoEntityJson;
import com.ajdeveloper.instadownloader.model.HLS.Example;
import com.ajdeveloper.instadownloader.model.HLS.Format;
import com.ajdeveloper.instadownloader.presenter.VimeoPresenter;
import com.ajdeveloper.instadownloader.searchview.DataHelper;
import com.ajdeveloper.instadownloader.searchview.SearchSugg;

@SuppressLint("WrongConstant")
public class BrowserActivity extends BaseActivity implements VimeoView {
    public static final String TAG = "BrowserActivity";
    int TIME = 15000;
    VideoApiInterface apiInterface;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    int counter;
    int delay = 5000;
    private boolean downloading = false;
    /* access modifiers changed from: private */
    public boolean isHasFocus = false;
    boolean iscomplete = true;
    boolean isfirst = false;
    SuggestionListAdapter mAdapter;
    private Uri mCapturedImageUri;
    @BindView(R.id.cvDownloadBrowser)
    CardView mCvDownload;
    @BindView(R.id.cvSuggestionsContainerBrowser)
    CardView mCvSuggestionContainer;
    /* access modifiers changed from: private */
    public String mDownloadUrl = "";
    @BindView(R.id.etSearchBrowser)
    EditText mEtSearch;
    /* access modifiers changed from: private */
    public ValueCallback<Uri[]> mFilePathCallback;
    /* access modifiers changed from: private */
    public String mFileSize = "";
    @BindView(R.id.imgIcCancelBrowser)
    ImageView mImgIcClear;
    @BindView(R.id.imgIcDownloadBrowser)
    ImageView mImgIcDownload;
    @BindView(R.id.imgIcHomeBrowserActivity)
    ImageView mImgIcHome;
    @BindView(R.id.imgIcRefreshBrowser)
    ImageView mImgIcRefresh;
    private boolean mIsDownloadClicked = false;
    /* access modifiers changed from: private */
    public Boolean mIsFullScreenOpened = Boolean.valueOf(false);
    /* access modifiers changed from: private */
    public String mLastLoadedUrl = "";
    @BindView(R.id.layoutVideoView)
    RelativeLayout mLayoutVideoView;

    @BindView(R.id.webViewContainerBrowser)
    RelativeLayout mLayoutWebViewContainer;

    @BindView(R.id.recyclerSuggestionListBrowser)
    RecyclerView mRecyclerSuggestion;

    @BindView(R.id.etSearchContainerBrowserActivity)
    LinearLayout mSearchContainer;

    WebChromeClient mWebChromeClient;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    String replaced;
    Handler setvid = new Handler();
    SharedPreferences sharedPreferences;
    String url;
    String videoUrl = "";
    VimeoPresenter vimeoPresenter;
    @BindView(R.id.webViewBrowser)
    ObservableWebView webView;

    public static void start(Context context, String str) {
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra("url_to_load", str);
        ((Activity) context).startActivityForResult(intent, 116);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_brower);
        ButterKnife.bind(this);
        this.apiInterface = (VideoApiInterface) RetrofitClientInstance.getRetrofitInstance().create(VideoApiInterface.class);
        new DialogUtils(this).showNoVideoFound(null, true);
        getWindow().addFlags(128);
        getActivityComponent().inject(this);
        this.vimeoPresenter.setView(this);
        configWebView();
        configDefSettings();
        this.url = getIntent().getStringExtra("url_to_load");
        String str = this.url;
        if (str == null || TextUtils.isEmpty(str)) {
            focusSearchView(true);
            this.url = "";
        } else {
            this.webView.loadUrl(this.url);
            focusSearchView(false);
        }
        //loadInterstitialAd(this.url);
        this.webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                if (str.startsWith("mailto:")) {
                    BrowserActivity.this.shareVideoLink(str);
                    return false;
                }
                webView.loadUrl(str);
                return super.shouldOverrideUrlLoading(webView, str);
            }

            public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
                BrowserActivity.this.progressBar.setVisibility(0);
                BrowserActivity.this.disableDownloadbtn();
            }

            public void onPageFinished(WebView webView, String str) {
                BrowserActivity.this.progressBar.setVisibility(8);
                BrowserActivity.this.mEtSearch.setText(webView.getUrl());
                BrowserActivity.this.mEtSearch.setSelection(BrowserActivity.this.mEtSearch.getText().toString().trim().length());
                BrowserActivity.this.checkUrlBelongsTo(webView, str, false);
                DataHelper.saveSearch(BrowserActivity.this, str);
                super.onPageFinished(webView, str);
            }

            @SuppressLint({"JavascriptInterface"})
            public void onLoadResource(WebView webView, String str) {
                try {
                    if (!BrowserActivity.this.isHasFocus) {
                        BrowserActivity.this.mEtSearch.setText(webView.getUrl());
                    }
                    BrowserActivity.this.checkUrlBelongsToOnLoadResource(webView, str, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onLoadResource(webView, str);
            }
        });
        this.mWebChromeClient = new WebChromeClient() {
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                BrowserActivity.this.progressBar.setProgress(i);
            }

            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, final FileChooserParams fileChooserParams) {
                BrowserActivity.this.mFilePathCallback = valueCallback;
                new DialogUtils(BrowserActivity.this)
                        .showInfoDialog("Take/Pick photo",
                                "Please choose one to take picture",
                                "Camera",
                                "Gallery",
                                "Flag_insta_camera", new DialogUtils.DialogCallBack() {
                                    public void onPosButtonClicked(Bundle bundle) {
                                        if (BrowserActivity.this.storageAllowed()) {
                                            BrowserActivity.this.requesCameraPermission();
                                        } else {
                                            BrowserActivity.this.showNoPermissionDialog("Please allow storage and camera access in settings to use it.");
                                        }
                                    }

                                    public void onNegButtonClicked(Bundle bundle) {
                                        BrowserActivity.this.startActivityForResult(fileChooserParams.createIntent(), 113);
                                    }
                                });
                return true;
            }

            public void onShowCustomView(View view, CustomViewCallback customViewCallback) {
                super.onShowCustomView(view, customViewCallback);
                BrowserActivity.this.webView.setVisibility(8);
                BrowserActivity.this.mLayoutVideoView.setVisibility(0);
                BrowserActivity.this.mLayoutVideoView.addView(view);
                BrowserActivity.this.mIsFullScreenOpened = Boolean.valueOf(true);
            }

            public void onHideCustomView() {
                super.onHideCustomView();
                BrowserActivity.this.webView.setVisibility(0);
                BrowserActivity.this.mLayoutVideoView.setVisibility(8);
                BrowserActivity.this.mIsFullScreenOpened = Boolean.valueOf(false);
            }
        };
        this.webView.setWebChromeClient(this.mWebChromeClient);
        this.mEtSearch.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                BrowserActivity.this.focusSearchView(true);
            }
        });
    }

    private void loadInterstitialAd(String str) {
        if (str.contains("facebook")) {
            loadAd("ad_facebook");
        } else if (str.contains("dailymotion")) {
            loadAd("ad_dailymotion");
        } else if (str.contains("instagram")) {
            loadAd("ad_instagram");
        } else if (str.contains("vimeo")) {
            loadAd("ad_vimeo");
        } else if (str.contains("twitter")) {
            loadAd("ad_twitter");
        } else {
            loadAd("ad_search_button");
        }
    }

    private void loadAd(String str) {
        AdsUtil.getInstance(this).loadInterstitialAd(TAG, str, new AdsUtil.AdsCallBack() {
            public void onAdFailedToLoad(String str, String str2) {
            }
        });
    }

    @OnClick(R.id.imgIcHomeBrowserActivity)
    public void onClickImgIcHome() {
        onBackPressed();
    }

    /* access modifiers changed from: private */
    public void shareVideoLink(String str) {
        this.videoUrl = "";
        if (str.contains("dailymotion")) {
            this.videoUrl = "https://www.dailymotion.com";
            try {
                this.videoUrl = str.substring(str.indexOf("https"), str.indexOf("&subject"));
                this.webView.loadUrl("http://www.dailymotion.com");
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else if (str.contains("vimeo")) {
            this.videoUrl = "https://vimeo.com";
            try {
                this.videoUrl = UtilMethods.getValidUrlVimeo(str.substring(str.indexOf("https")));
            } catch (IndexOutOfBoundsException e2) {
                e2.printStackTrace();
            }
        }
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.TEXT", this.videoUrl);
        startActivity(Intent.createChooser(intent, "Share video via"));
    }

    /* access modifiers changed from: private */
    @SuppressLint({"JavascriptInterface"})
    public void checkUrlBelongsToOnLoadResource(WebView webView2, String str, boolean z) {
        if ((str.contains("facebook.com") || str.contains("video")) && str != null) {
            this.webView.loadUrl(ScriptUtil.FACEBOOK_SCRIPT);
        }
        if (isInstaUrl(str)) {
            getFileSize(str);
            this.mDownloadUrl = str;
            enableDownloadbtn();
        }
        if (isTwitterUrl(str)) {
            getFileSize(str);
            this.mDownloadUrl = str;
            enableDownloadbtn();
        }
        if (this.webView != null && !str.startsWith("blob:http")) {
            reset();
            String str2 = this.replaced;
            if (str2 == null || str2.isEmpty() || this.replaced.equals("null") || this.webView != null) {
                webload();
            }
        }
    }

    private boolean isTwitterUrl(String str) {
        return (str.startsWith("https://video") || str.startsWith("video.twimg.com")) && (str.contains(".mp4?tag") || str.endsWith(".mp4"));
    }

    private boolean isInstaUrl(String str) {
        return str != null && (str.startsWith("https://instagram.flhe7-1.fna.fbcdn.net/vp/") || str.startsWith("https://instagram.flhe3-1.fna.fbcdn.net/vp/") || str.contains("fna.fbcdn.net/vp/") || str.contains("https://instagram.flhe") || str.contains("scontent.cdninstagram.com")) && str.contains(".mp4");
    }

    /* access modifiers changed from: private */
    public void checkUrlBelongsTo(WebView webView2, String str, boolean z) {
        this.mIsDownloadClicked = z;
        if ((str == null || TextUtils.isEmpty(str)) && z) {
            showNoVideoDialog();
            return;
        }
        if (str.contains("dailymotion")) {
            downloadFromDM(webView2, str, z);
        } else if (str.contains("vimeo")) {
            downloadfromVimeo(webView2, str, z);
        } else if (z) {
            if (this.webView.getUrl().contains("m.facebook.com") || this.webView.getUrl().contains("mobile.facebook.com")) {
                String str2 = this.mDownloadUrl;
                if (str2 == null || TextUtils.isEmpty(str2)) {
                    showFbHelp();
                    return;
                }
            }
            if (str.toLowerCase().contains("youtu.be") || str.toLowerCase().contains("youtube.com")) {
                showYoutubeDialog();
            } else if (str.contains("www.google.com/search?")) {
                showNoVideoDialog();
            } else {
                String str3 = this.mDownloadUrl;
                if (str3 == null || TextUtils.isEmpty(str3)) {
                    showNoVideoDialog();
                } else {
                    showDownloadSheet(this.mDownloadUrl, this.mFileSize);
                }
            }
        }
    }

    private void downloadfromVimeo(WebView webView2, String str, boolean z) {
        if (!this.mLastLoadedUrl.equalsIgnoreCase(str) || z) {
            ObservableWebView observableWebView = this.webView;
            if (observableWebView != null) {
                this.mLastLoadedUrl = str;
                if (observableWebView.getUrl().substring(18) != null && !this.webView.getUrl().substring(18).isEmpty() && !str.equalsIgnoreCase("https://vimeo.com/watch")) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("https://player.vimeo.com/video/");
                    sb.append(this.webView.getUrl().substring(18));
                    this.vimeoPresenter.getVideoList(sb.toString());
                } else if (z) {
                    showNoVideoDialog();
                } else {
                    disableDownloadbtn();
                }
            } else {
                Toast.makeText(this, "Check Connectivity", 0).show();
            }
        }
    }

    public void setVideoList(List<VideoEntityJson> list) {
        if (list == null || list.size() != 0) {
            enableDownloadbtn();
            if (this.mIsDownloadClicked) {
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < list.size(); i++) {
                    arrayList.add(((VideoEntityJson) list.get(i)).quality);
                }
                showVimeo_DownloadSheet(arrayList, list);
                return;
            }
            return;
        }
        disableDownloadbtn();
    }

    private void showVimeo_DownloadSheet(final ArrayList<String> arrayList, final List<VideoEntityJson> list) {
        new BSUtils(this).showListSheet("Vimeo_video", "", arrayList, "tag_vimeo", new BSUtils.BSCallBack() {
            public void onBsHidden() {
            }

            public void onDownloadClicked(String str) {
                if (str != null) {
                    String str2 = (String) arrayList.get(Integer.parseInt(str));
                    for (final VideoEntityJson videoEntityJson : list) {
                        if (str2.equals(videoEntityJson.quality)) {
                            BrowserActivity.this.getStoragePermission(new PermissionCallBack() {
                                public void permissionGranted() {
                                    BrowserActivity.this.vimeoPresenter.downloadVideo(videoEntityJson.url, BrowserActivity.this.sharedPreferences.getString(BrowserActivity.this.getString(R.string.video_title_key), ""), BrowserActivity.this.sharedPreferences.getString(BrowserActivity.this.getString(R.string.video_thumbnail_key), ""));
                                    new UtilMethods(BrowserActivity.this).showCustomToast("Download Started...", 1);
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void manageResponse(Example example) {
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
        StringBuilder sb12 = new StringBuilder();
        sb12.append("Video_");
        sb12.append(simpleDateFormat.format(new Date()));
        final String sb123 = sb12.toString();
        new BSUtils_D(this).showListSheet(str, "", list, TAG, new BSUtils_D.BSCallBack() {
            public void onBsHidden() {
            }

            public void onDownloadClicked(Format format) {
                StringBuilder sb = new StringBuilder();
                sb.append(Environment.getExternalStorageDirectory());
                sb.append(File.separator);
                sb.append("Download/FreeDownloader");
                sb.append(File.separator);
                String sb2 = sb.toString();
                Intent intent = new Intent(BrowserActivity.this, DownloadService.class);
                new UtilMethods(BrowserActivity.this).showCustomToast("Download Started...", 1);
                intent.putExtra("URL", format.getUrl());
                intent.putExtra("TITLE", sb123);
                intent.putExtra("PATH", Constants.Parent + Constants.VIDEOS);
                if (VERSION.SDK_INT >= 26) {
                    BrowserActivity.this.startForegroundService(intent);
                } else {
                    BrowserActivity.this.startService(intent);
                }
            }
        });
    }

    private void downloadFromDM(WebView webView2, String str, boolean z) {
        String str2 = this.mLastLoadedUrl;
        if (str2 == null || !str2.equalsIgnoreCase(str) || z) {
            this.mLastLoadedUrl = str;
            enableDownloadbtn();
            if (str != null && str.contains("video")) {
                DialogUtils.showSimpleProgressDialog(this);
                this.apiInterface.getUrl(str).enqueue(new Callback<Example>() {
                    public void onResponse(Call<Example> call, Response<Example> response) {
                        DialogUtils.closeProgressDialog();
                        if (response != null) {
                            Example example = (Example) response.body();
                            if (example != null) {
                                BrowserActivity.this.manageResponse(example);
                            } else {
                                Toast.makeText(BrowserActivity.this, "Something Went Wrong", 0).show();
                            }
                        } else {
                            Toast.makeText(BrowserActivity.this, "Check Internet Connection ", 0).show();
                        }
                    }

                    public void onFailure(Call<Example> call, Throwable th) {
                        DialogUtils.closeProgressDialog();
                        Log.i("Video ", th.getMessage());
                    }
                });
            } else if (z) {
                showNoVideoDialog();
            } else {
                disableDownloadbtn();
            }
            return;
        }
        enableDownloadbtn();
    }

    @OnClick(R.id.imgIcDownloadBrowser)
    public void onClickCvDownload() {
        ObservableWebView observableWebView = this.webView;
        checkUrlBelongsTo(observableWebView, observableWebView.getUrl(), true);
    }

    private void showDownloadSheet(final String str, String str2) {
        new BSUtils(this).showSimpleSheet("Video", str2, new BSUtils.BSCallBack() {
            @Override
            public void onBsHidden() {
                BrowserActivity.this.mCvDownload.setVisibility(0);
            }

            @Override
            public void onDownloadClicked(String str31) {
                try {
                    BrowserActivity.this.mCvDownload.setVisibility(0);
                    BrowserActivity.this.downloadVideo(str);
                } catch (Exception e) {
                    e.printStackTrace();
                    BrowserActivity.this.mCvDownload.setVisibility(0);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void downloadVideo(final String str) {
        getStoragePermission(new PermissionCallBack() {
            public void permissionGranted() {
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
                if (str != null && !TextUtils.isEmpty(str)) {
                    String str2 = str;
                    if (str2 != null) {
                        Request request = new Request(Uri.parse(str2));
                        request.setDestinationUri(Uri.parse(sb4));
                        request.setNotificationVisibility(1);
                        BrowserActivity browserActivity = BrowserActivity.this;
                        browserActivity.getApplicationContext();
                        ((DownloadManager) browserActivity.getSystemService("download")).enqueue(request);
                    }
                    new UtilMethods(BrowserActivity.this).showCustomToast("Download Started...", 1);
                }
            }
        });
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() != 0 || i != 4) {
            return super.onKeyDown(i, keyEvent);
        }
        if (!this.webView.canGoBack()) {
            finish();
        } else if (!this.mIsFullScreenOpened.booleanValue()) {
            this.webView.goBack();
            disableDownloadbtn();
            focusSearchView(false);
        }
        return true;
    }

    @OnClick(R.id.imgIcCancelBrowser)
    public void onClickClear() {
        this.mEtSearch.setText("");
    }

    @OnClick(R.id.imgIcDownloadBrowser)
    public void onClickRefresh() {
        this.webView.reload();
    }

    private void configDefSettings() {
        this.progressBar.getProgressDrawable().setColorFilter(-1, Mode.SRC_IN);
        disableDownloadbtn();
        this.mEtSearch.setOnKeyListener(new OnKeyListener() {
            public final boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i != 66) {
                    return false;
                }
                BrowserActivity.this.loadWebView("");
                return true;

            }
        });
        this.mEtSearch.setOnFocusChangeListener(new OnFocusChangeListener() {
            public final void onFocusChange(View view, boolean z) {
                BrowserActivity.this.isHasFocus = z;
                if (z) {
                    BrowserActivity.this.mImgIcClear.setVisibility(0);
                    BrowserActivity.this.mImgIcRefresh.setVisibility(8);
                    return;
                }
                BrowserActivity.this.mImgIcClear.setVisibility(8);
                BrowserActivity.this.mImgIcRefresh.setVisibility(0);
            }
        });
        this.mEtSearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (BrowserActivity.this.mEtSearch.hasFocus()) {
                    BrowserActivity.this.setSuggestionAdapter(charSequence.toString());
                }
            }
        });
    }

    private void setSuggestionAdapter(String str) {
        this.mCvSuggestionContainer.setVisibility(0);
        DataHelper.findSuggestions(this, str, 5, 250, new DataHelper.OnFindSuggestionsListener() {
            public void onResults(List<SearchSugg> list) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                BrowserActivity browserActivity = BrowserActivity.this;
                browserActivity.mAdapter = new SuggestionListAdapter(browserActivity, list, new SuggestionListAdapter.RecyclerCallBack() {
                    public void onItemClicked(String str) {
                        BrowserActivity.this.loadWebView(str);
                    }

                    public void onMoveUpClicked(String str) {
                        BrowserActivity.this.mEtSearch.setText(str);
                        BrowserActivity.this.mEtSearch.setSelection(str.length());
                    }
                });
                BrowserActivity.this.mRecyclerSuggestion.setLayoutManager(new LinearLayoutManager(BrowserActivity.this));
                BrowserActivity.this.mRecyclerSuggestion.setAdapter(BrowserActivity.this.mAdapter);
            }
        });
    }

    /* access modifiers changed from: private */
    public void loadWebView(String str) {
        focusSearchView(false);
        if (str == null || TextUtils.isEmpty(str)) {
            str = this.mEtSearch.getText().toString().trim();
        }
        if (str.length() <= 0) {
            return;
        }
        if (str.startsWith("http://") || str.startsWith("https://")) {
            this.webView.loadUrl(str);
            saveHistory(str, "url");
        } else if (Patterns.WEB_URL.matcher(str).matches()) {
            ObservableWebView observableWebView = this.webView;
            StringBuilder sb = new StringBuilder();
            sb.append("http://");
            sb.append(str);
            observableWebView.loadUrl(sb.toString());
            EditText editText = this.mEtSearch;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("http://");
            sb2.append(str);
            editText.setText(sb2.toString());
            saveHistory(str, "url");
        } else {
            this.webView.loadUrl(String.format("https://www.google.com/search?q=%s", new Object[]{str}));
            this.mEtSearch.setText(String.format("https://www.google.com/search?q=%s", new Object[]{str}));
            saveHistory(str, "search");
        }
    }

    private void saveHistory(String str, String str2) {
        try {
            ((RemoteApiService) new Builder().baseUrl("http://clicksolapps.com/avd/").addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build().create(RemoteApiService.class)).saveHistory(str, str2).enqueue(new Callback<ResponseBody>() {
                public void onFailure(Call<ResponseBody> call, Throwable th) {
                }

                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showYoutubeDialog() {
        final Dialog dialog = new Dialog(this, R.style.FullWidthDialog);
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

    /* access modifiers changed from: private */
    public void enableDownloadbtn() {
        if (this.replaced != null) {
            this.mCvDownload.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    /* access modifiers changed from: private */
    public void disableDownloadbtn() {
        this.mCvDownload.setCardBackgroundColor(getResources().getColor(R.color.colorGray));
        this.mDownloadUrl = "";
    }

    private void configWebView() {
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setPluginState(PluginState.ON);
        this.webView.getSettings().setDisplayZoomControls(true);
        this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        this.webView.getSettings().setUseWideViewPort(true);
        this.webView.getSettings().setLoadWithOverviewMode(true);
        this.webView.getSettings().setSupportMultipleWindows(true);
        this.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        this.webView.addJavascriptInterface(this, "browser");
        this.webView.getSettings().setDomStorageEnabled(true);
        this.webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        this.webView.getSettings().setAppCacheEnabled(true);
        this.webView.getSettings().setSaveFormData(true);
    }

    @JavascriptInterface
    public void getVideoData(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public final void run() {
                if (str != null) {
                    try {
                        if (!str.contains("undefined")) {
                            BrowserActivity.this.mDownloadUrl = str;
                            BrowserActivity.this.getFileSize(str);
                        }
                    } catch (Exception e) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Download Failed: ");
                        sb.append(e.toString());
                        Toast.makeText(BrowserActivity.this, sb.toString(), 1).show();
                    }
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void focusSearchView(boolean z) {
        this.mEtSearch.setFocusable(z);
        this.mEtSearch.setFocusableInTouchMode(z);
        if (z) {
            ViewUtil.showSoftKeyboard(this, this.mEtSearch);
            this.mCvSuggestionContainer.setVisibility(0);
            return;
        }
        ViewUtil.hideSoftKeyboard(this, this.mEtSearch);
        this.mCvSuggestionContainer.setVisibility(8);
    }

    public void showFbHelp() {
        new DialogUtils(this).showFbHelp(new DialogUtils.DialogCallBack() {
            public void onNegButtonClicked(Bundle bundle) {
            }

            public void onPosButtonClicked(Bundle bundle) {
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void getFileSize(final String str) {
        new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... voidArr) {
                try {
                    return Long.valueOf((long) new URL(str).openConnection().getContentLength());
                } catch (IOException e) {
                    e.printStackTrace();
                    return Long.valueOf(0);
                }
            }

            protected void onPostExecute(Long l) {
                super.onPostExecute(l);
                BrowserActivity.this.mCvDownload.setCardBackgroundColor(BrowserActivity.this.getResources().getColor(R.color.colorPrimaryDark));
                BrowserActivity.this.mFileSize = FileSizes.getHumanReadableSize(l.longValue(), BrowserActivity.this);
                BrowserActivity.this.animateDownloadIcon();
            }
        }.execute(new Void[0]);
    }

    private void animateDownloadIcon() {
        Animation loadAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        this.mImgIcDownload.setAnimation(loadAnimation);
        this.mImgIcDownload.startAnimation(loadAnimation);
        this.mImgIcDownload.startAnimation(loadAnimation);
    }

    private void requesCameraPermission() {
        if (VERSION.SDK_INT < 23) {
            openCamera();
        } else if (checkSelfPermission("android.permission.CAMERA") == 0) {
            openCamera();
        } else if (shouldShowRequestPermissionRationale("android.permission.CAMERA")) {
            showNoPermissionDialog("Please allow access to camera to use it. Go to settings and allow it.");
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.CAMERA"}, 114);
        }
    }

    private boolean storageAllowed() {
        boolean z = true;
        if (VERSION.SDK_INT < 23) {
            return true;
        }
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            z = false;
        }
        return z;
    }

    private void showNoVideoDialog() {
        downloadVideo(this.mDownloadUrl);
        disableDownloadbtn();
        new DialogUtils(this).showNoVideoFound(new DialogUtils.DialogCallBack() {
            public void onNegButtonClicked(Bundle bundle) {
            }

            public void onPosButtonClicked(Bundle bundle) {
            }
        }, false);
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i == 114) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                requesCameraPermission();
            } else {
                openCamera();
            }
        }
    }

    private void openCamera() {
        try {
            File createImageFile = createImageFile();
            StringBuilder sb = new StringBuilder();
            sb.append(getPackageName());
            sb.append(".provider");
            this.mCapturedImageUri = FileProvider.getUriForFile(this, sb.toString(), createImageFile);
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.setFlags(1);
            intent.putExtra("output", this.mCapturedImageUri);
            startActivityForResult(intent, 112);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append("AVD-");
        sb.append(format);
        sb.append(".jpeg");
        String sb2 = sb.toString();
        File file = new File(Environment.getExternalStorageDirectory(), "Download/FreeDownloader");
        StringBuilder sb3 = new StringBuilder();
        sb3.append(file);
        sb3.append("/Images");
        File file2 = new File(sb3.toString());
        if (!file2.exists()) {
            file2.mkdirs();
        }
        return new File(file2, sb2);
    }

    private void showNoPermissionDialog(String str) {
        new DialogUtils(this).showInfoDialog("Permission Required!", str, "Settings", "", "", new DialogUtils.DialogCallBack() {
            public void onNegButtonClicked(Bundle bundle) {
            }

            public void onPosButtonClicked(Bundle bundle) {
                BrowserActivity.this.goToAppSettings();
            }
        });
    }

    public void goToAppSettings() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(intent);
    }

    protected void onDestroy() {
        this.webView.destroy();
        this.webView = null;
        super.onDestroy();
    }

    protected void onPause() {
        ObservableWebView observableWebView = this.webView;
        if (observableWebView != null) {
            observableWebView.onPause();
        }
        super.onPause();
    }

    protected void onResume() {
        ObservableWebView observableWebView = this.webView;
        if (observableWebView != null) {
            observableWebView.onResume();
        }
        super.onResume();
    }

    public void onBackPressed() {
        setResult(-1);
        finish();
    }

    @SuppressLint({"JavascriptInterface"})
    public void webload() {
        this.webView.evaluateJavascript("document.querySelector('video').currentSrc", new ValueCallback<String>() {
            public void onReceiveValue(String str) {
                Log.d("LogName", BrowserActivity.this.mLastLoadedUrl);
                BrowserActivity.this.replaced = str.replace("\"", "");
                if (BrowserActivity.this.replaced.length() > 0 && BrowserActivity.this.replaced != null) {
                    if (!BrowserActivity.this.replaced.startsWith("http://") && !BrowserActivity.this.replaced.startsWith("https://")) {
                        if (!BrowserActivity.this.replaced.startsWith("blob:http://")) {
                            return;
                        }
                        if (!str.contains(".mp4?tag") && !str.endsWith(".mp4") && !str.endsWith(".m3u8")) {
                            return;
                        }
                    }
                    if (BrowserActivity.this.iscomplete) {
                        if (BrowserActivity.this.replaced != null && !BrowserActivity.this.replaced.contains("youtube.com") && !BrowserActivity.this.replaced.contains("encrypted")) {
                            BrowserActivity browserActivity = BrowserActivity.this;
                            browserActivity.mDownloadUrl = browserActivity.replaced;
                            BrowserActivity.this.enableDownloadbtn();
                            BrowserActivity.this.getFileSize(str);
                        }
                        BrowserActivity.this.iscomplete = false;
                    }
                }
            }
        });
        this.webView.addJavascriptInterface(this, "browser");
        this.webView.loadUrl("javascript:android.onData(functionThatReturnsSomething)");
        this.webView.getUrl();
    }

    public void reset() {
        this.replaced = "";
        this.counter = 1;
    }
}
