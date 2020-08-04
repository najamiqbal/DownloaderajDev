package com.ajdeveloper.instadownloader.Components.module;

import dagger.internal.Factory;
import dagger.internal.Preconditions;
import okhttp3.OkHttpClient;

/* renamed from: com.downloadvideos.videodownloader.hdvideo.anyvideodownloader.di.module.AppModule_ProvideOkHttpClientFactory */
public final class AppModule_ProvideOkHttpClientFactory implements Factory<OkHttpClient> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final AppModule module;

    public AppModule_ProvideOkHttpClientFactory(AppModule appModule) {
        this.module = appModule;
    }

    public OkHttpClient get() {
        return (OkHttpClient) Preconditions.checkNotNull(this.module.provideOkHttpClient(), "Cannot return null from a non-@Nullable @Provides method");
    }

    public static Factory<OkHttpClient> create(AppModule appModule) {
        return new AppModule_ProvideOkHttpClientFactory(appModule);
    }
}
