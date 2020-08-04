package com.ajdeveloper.instadownloader;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;

import com.ajdeveloper.instadownloader.Activities.BaseActivity;
import com.ajdeveloper.instadownloader.database.SharedPref;

public class MainActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, getString(R.string.admob_app_id));
    }

    public void rateUs() {
        new AlertDialog.Builder(this).setMessage("If you like this app, please review and rate. This means a lot for us.").setNegativeButton("Cancel", null).setPositiveButton("Rate it", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPref.getInstance(MainActivity.this).setSpBoolean("is_rate_us_clicked", true);
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                MainActivity.this.startActivity(intent);
            }
        }).create().show();
    }

    private void shareApp() {
        try {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name));
            StringBuilder sb = new StringBuilder();
            sb.append("\nLet me recommend you this application\n\n");
            sb.append("https://play.google.com/store/apps/details?id=" + getPackageName());
            intent.putExtra("android.intent.extra.TEXT", sb.toString());
            startActivity(Intent.createChooser(intent, "Choose one"));
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), 0).show();
        }
    }
}
