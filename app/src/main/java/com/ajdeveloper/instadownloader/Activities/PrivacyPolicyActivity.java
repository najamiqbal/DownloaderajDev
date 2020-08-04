package com.ajdeveloper.instadownloader.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.ajdeveloper.instadownloader.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        WebView wv;
        wv = (WebView) findViewById(R.id.web_view);
        wv.loadUrl("file:///android_asset/pp.html");
    }
}
