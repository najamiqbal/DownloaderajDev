package com.ajdeveloper.instadownloader.Utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.gms.ads.AdView;

public class DialogUtils {
    private static Dialog simpleProgressDialog;
    private Context mContext;
    private AdView mNoVideoAdView;

    public interface DialogCallBack {
        void onNegButtonClicked(Bundle bundle);

        void onPosButtonClicked(Bundle bundle);
    }

    public DialogUtils(Context context) {
        this.mContext = context;
    }

    public void showInfoDialog(String str, String str2, String str3, String str4, String str5, final DialogCallBack dialogCallBack) {
        AlertDialog create = new Builder(this.mContext).create();
        create.setTitle(str);
        create.setMessage(str2);
        create.setCancelable(false);
        create.setButton(-1, str3, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                dialogCallBack.onPosButtonClicked(new Bundle());
            }
        });
        create.setButton(-2, str4, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                dialogCallBack.onNegButtonClicked(new Bundle());
            }
        });
        create.show();
    }

    public void showNoVideoFound(final DialogCallBack dialogCallBack, boolean z) {
        Utilities.showDialog(mContext, "Sorry, Try Again!", "No video found. Let videodownloader page load completely, " +
                "so that 'Video Downloader' can detect videodownloader video for downloading!", "OK", "", false, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    public void showFbHelp(final DialogCallBack dialogCallBack) {
        Utilities.showDialog(mContext, "Help!", "Tap on videodownloader video to download it.", "OK", "", false, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    private static ProgressDialog progressDialog;

    public static void showSimpleProgressDialog(Context context) {
        if (simpleProgressDialog != null) {
            closeProgressDialog();
        }
        if (context != null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
    }

    public static void closeProgressDialog() {
        if (progressDialog != null)
            progressDialog.cancel();
    }

    public static void setDialogOpacity(Dialog dialog, int i, int i2) {
        ColorDrawable colorDrawable = new ColorDrawable(i);
        colorDrawable.setAlpha(i2);
        dialog.getWindow().setBackgroundDrawable(colorDrawable);
    }
}
