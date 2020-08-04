package com.ajdeveloper.instadownloader.Utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class ViewUtil {
    public static void showSoftKeyboard(Context context, View view) {
        if (view.requestFocus()) {
            ((InputMethodManager) context.getSystemService("input_method")).showSoftInput(view, 1);
        }
    }

    public static void hideSoftKeyboard(Context context, View view) {
        ((InputMethodManager) context.getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 1);
    }
}
