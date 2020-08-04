package com.ajdeveloper.instadownloader.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.HashMap;

import retrofit2.Callback;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;
import com.ajdeveloper.instadownloader.Interface.DailymotionApiInterface;
import com.ajdeveloper.instadownloader.VideoApplication;
import com.ajdeveloper.instadownloader.model.dailymotion.ChannelsList;
import com.ajdeveloper.instadownloader.Components.component.ActivityComponent;
import com.ajdeveloper.instadownloader.Components.component.AppComponent;
import com.ajdeveloper.instadownloader.Components.component.DaggerActivityComponent;
import com.ajdeveloper.instadownloader.Components.module.ActivityModule;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    private ActivityComponent activityComponent;
    private PermissionCallBack mPermissionCallBack;

    public interface PermissionCallBack {
        void permissionGranted();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initActivityComponent();
    }

    public void getDmChannelsList(Callback<ChannelsList> callback) {
        DailymotionApiInterface dailymotionApiInterface = (DailymotionApiInterface) new Builder().baseUrl("https://api.dailymotion.com/").addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build().create(DailymotionApiInterface.class);
        HashMap hashMap = new HashMap();
        hashMap.put("fields", "created_time,id,name");
        hashMap.put("sort", "popular");
        hashMap.put("page", "1");
        hashMap.put("limit", "20");
        dailymotionApiInterface.getChannels(hashMap).enqueue(callback);
    }

    protected AppComponent getAppComponent() {
        return ((VideoApplication) getApplication()).getAppComponent();
    }

    public void initActivityComponent() {
        this.activityComponent = DaggerActivityComponent.builder().appComponent(getAppComponent()).activityModule(new ActivityModule(this)).build();
    }

    public ActivityComponent getActivityComponent() {
        return this.activityComponent;
    }

    @SuppressLint("WrongConstant")
    public void getStoragePermission(PermissionCallBack permissionCallBack) {
        this.mPermissionCallBack = permissionCallBack;
        if (VERSION.SDK_INT < 23) {
            permissionCallBack.permissionGranted();
        } else if (checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            permissionCallBack.permissionGranted();
        } else if (shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Write external storage permission is required.");
            builder.setTitle("Please grant permission");
            builder.setPositiveButton("OK", new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    BaseActivity.this.goToAppSettings();
                }
            });
            builder.setNeutralButton("Cancel", null);
            builder.create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 123);
        }
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i == 123) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                PermissionCallBack permissionCallBack = this.mPermissionCallBack;
                if (permissionCallBack != null) {
                    getStoragePermission(permissionCallBack);
                }
            } else {
                PermissionCallBack permissionCallBack2 = this.mPermissionCallBack;
                if (permissionCallBack2 != null) {
                    permissionCallBack2.permissionGranted();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void goToAppSettings() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(intent);
    }
}
