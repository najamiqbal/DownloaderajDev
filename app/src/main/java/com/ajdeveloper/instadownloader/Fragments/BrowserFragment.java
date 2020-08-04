package com.ajdeveloper.instadownloader.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ajdeveloper.instadownloader.R;
import com.ajdeveloper.instadownloader.Utils.Utilities;

import static com.ajdeveloper.instadownloader.Utils.iConstants.DISABLE_DOWNLOADING;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class BrowserFragment extends Fragment {

    String url;

    public BrowserFragment() {}

    @SuppressLint("ValidFragment")
    public BrowserFragment(String url) {
        this.url = url;
    }

    private Context context;
    private WebView myWebView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate videodownloader layout for this fragment
        return inflater.inflate(R.layout.fragment_browser, container, false);
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        myWebView = view.findViewById(R.id.web_view);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setSupportZoom(false);
        myWebView.setVerticalScrollBarEnabled(false);
        myWebView.addJavascriptInterface(this, "FBDownloader");
//        myWebView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
        myWebView.setWebChromeClient(new WebChromeClient());
        myWebView.loadUrl(url);
        myWebView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                myWebView.loadUrl("javascript:(function() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\");');}}})()");
            }

            @Override
            public void onLoadResource(WebView webView, String url) {
                super.onLoadResource(webView, url);
                for (CharSequence contains : DISABLE_DOWNLOADING) {
                    if (url.contains(contains)) {
                        myWebView.loadUrl(BrowserFragment.this.url);
                        break;
                    }
                }
                Utilities.prepVideo(myWebView);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(!(url.startsWith("intent")) && URLUtil.isValidUrl(url)){
                    view.loadUrl(url);
                }
                return true;
            }
        });
        myWebView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Log.e("DOWNLOAD_URL", url);
                Utilities.startDownload(context, url);
            }
        });
    }
    public boolean canGoBack() {
        return myWebView != null && myWebView.canGoBack();
    }

    public void goBack() {
        myWebView.goBack();
    }

    @JavascriptInterface
    public void processVideo(final String url, String str2) {
        Log.e("VIDEO:", url);
        Utilities.showDialog(context, "Download Video", "Download this video?",
                "Yes", "No", true, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE)
                            Utilities.startDownload(context, url);
                    }
                });

    }
}
