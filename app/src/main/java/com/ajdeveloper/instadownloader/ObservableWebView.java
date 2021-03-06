package com.ajdeveloper.instadownloader;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class ObservableWebView extends WebView {
    private OnScrollChangedCallback mOnScrollChangedCallback;

    public interface OnScrollChangedCallback {
        void onScroll(int i, int i2, int i3, int i4);
    }

    public ObservableWebView(Context context) {
        super(context);
    }

    public ObservableWebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ObservableWebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        super.setOnScrollChangeListener(onScrollChangeListener);
    }

    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        OnScrollChangedCallback onScrollChangedCallback = this.mOnScrollChangedCallback;
        if (onScrollChangedCallback != null) {
            onScrollChangedCallback.onScroll(i, i2, i3, i4);
        }
    }

    public OnScrollChangedCallback getOnScrollChangedCallback() {
        return this.mOnScrollChangedCallback;
    }

    public void setOnScrollChangedCallback(OnScrollChangedCallback onScrollChangedCallback) {
        this.mOnScrollChangedCallback = onScrollChangedCallback;
    }
}
