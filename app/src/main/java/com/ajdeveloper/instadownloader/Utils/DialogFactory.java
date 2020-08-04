package com.ajdeveloper.instadownloader.Utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

public class DialogFactory {
    public static AlertDialog showDialog(Context context, String str, String str2, String str3, OnClickListener onClickListener, OnClickListener onClickListener2) {
        Builder builder = new Builder(context);
        builder.setMessage(str).setCancelable(false).setPositiveButton(str2, onClickListener);
        if (!(str3 == null || onClickListener2 == null)) {
            builder.setNegativeButton(str3, onClickListener2);
        }
        AlertDialog create = builder.create();
        create.show();
        return create;
    }
}
