package com.ajdeveloper.instadownloader.Components.module;


import dagger.internal.Factory;
import dagger.internal.Preconditions;
import com.ajdeveloper.instadownloader.model.DatabaseModel;

/* renamed from: com.downloadvideos.videodownloader.hdvideo.anyvideodownloader.di.module.AppModule_ProvideDatabaseModelFactory */
public final class AppModule_ProvideDatabaseModelFactory implements Factory<DatabaseModel> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final AppModule module;

    public AppModule_ProvideDatabaseModelFactory(AppModule appModule) {
        this.module = appModule;
    }

    public DatabaseModel get() {
        return (DatabaseModel) Preconditions.checkNotNull(this.module.provideDatabaseModel(), "Cannot return null from a non-@Nullable @Provides method");
    }

    public static Factory<DatabaseModel> create(AppModule appModule) {
        return new AppModule_ProvideDatabaseModelFactory(appModule);
    }
}
