package com.ajdeveloper.instadownloader.Components.component;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Provider;

import dagger.MembersInjector;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import retrofit2.Retrofit;
import com.ajdeveloper.instadownloader.Activities.BrowserActivity;
import com.ajdeveloper.instadownloader.Activities.BrowserActivity_MembersInjector;
import com.ajdeveloper.instadownloader.FragmentsN.BrowserActivityFragment;
import com.ajdeveloper.instadownloader.FragmentsN.BrowserActivityFragment_MembersInjector;
import com.ajdeveloper.instadownloader.FragmentsN.download_url;
import com.ajdeveloper.instadownloader.FragmentsN.download_url_MembersInjector;
import com.ajdeveloper.instadownloader.model.DownloadModel;
import com.ajdeveloper.instadownloader.model.DownloadModel_Factory;
import com.ajdeveloper.instadownloader.model.DownloadModel_MembersInjector;
import com.ajdeveloper.instadownloader.Components.module.ActivityModule;
import com.ajdeveloper.instadownloader.presenter.VimeoPresenter;
import com.ajdeveloper.instadownloader.presenter.VimeoPresenter_Factory;
import com.ajdeveloper.instadownloader.presenter.VimeoPresenter_MembersInjector;

/* renamed from: com.downloadvideos.videodownloader.hdvideo.anyvideodownloader.di.component.DaggerActivityComponent */
public final class DaggerActivityComponent implements ActivityComponent {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private MembersInjector<BrowserActivity> browserActivityMembersInjector;
    private MembersInjector<BrowserActivityFragment> browserActivityFragmentMembersInjector;
    private Provider<Context> contextProvider;
    private MembersInjector<DownloadModel> downloadModelMembersInjector;
    private Provider<DownloadModel> downloadModelProvider;
    private MembersInjector<download_url> download_urlMembersInjector;
    private Provider<Retrofit> retrofitProvider;
    private Provider<SharedPreferences> sharedPreferencesProvider;
    private MembersInjector<VimeoPresenter> vimeoPresenterMembersInjector;
    private Provider<VimeoPresenter> vimeoPresenterProvider;

    /* renamed from: com.downloadvideos.videodownloader.hdvideo.anyvideodownloader.di.component.DaggerActivityComponent$Builder */
    public static final class Builder {
        /* access modifiers changed from: private */
        public AppComponent appComponent;

        private Builder() {
        }

        public ActivityComponent build() {
            if (this.appComponent != null) {
                return new DaggerActivityComponent(this);
            }
            StringBuilder sb = new StringBuilder();
            sb.append(AppComponent.class.getCanonicalName());
            sb.append(" must be set");
            throw new IllegalStateException(sb.toString());
        }

        @Deprecated
        public Builder activityModule(ActivityModule activityModule) {
            Preconditions.checkNotNull(activityModule);
            return this;
        }

        public Builder appComponent(AppComponent appComponent2) {
            this.appComponent = (AppComponent) Preconditions.checkNotNull(appComponent2);
            return this;
        }
    }

    private DaggerActivityComponent(Builder builder) {
        initialize(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    private void initialize(final Builder builder) {
        this.sharedPreferencesProvider = new Factory<SharedPreferences>() {
            private final AppComponent appComponent = builder.appComponent;

            public SharedPreferences get() {
                return (SharedPreferences) Preconditions.checkNotNull(this.appComponent.sharedPreferences(), "Cannot return null from a non-@Nullable component method");
            }
        };
        this.retrofitProvider = new Factory<Retrofit>() {
            private final AppComponent appComponent = builder.appComponent;

            public Retrofit get() {
                return (Retrofit) Preconditions.checkNotNull(this.appComponent.retrofit(), "Cannot return null from a non-@Nullable component method");
            }
        };
        this.downloadModelMembersInjector = DownloadModel_MembersInjector.create(this.sharedPreferencesProvider, this.retrofitProvider);
        this.contextProvider = new Factory<Context>() {
            private final AppComponent appComponent = builder.appComponent;

            public Context get() {
                return (Context) Preconditions.checkNotNull(this.appComponent.context(), "Cannot return null from a non-@Nullable component method");
            }
        };
        this.downloadModelProvider = DownloadModel_Factory.create(this.downloadModelMembersInjector, this.contextProvider);
        this.vimeoPresenterMembersInjector = VimeoPresenter_MembersInjector.create(this.downloadModelProvider);
        this.vimeoPresenterProvider = VimeoPresenter_Factory.create(this.vimeoPresenterMembersInjector, this.contextProvider);
        this.download_urlMembersInjector = download_url_MembersInjector.create(this.sharedPreferencesProvider, this.vimeoPresenterProvider);
        this.browserActivityMembersInjector = BrowserActivity_MembersInjector.create(this.vimeoPresenterProvider, this.sharedPreferencesProvider);
        this.browserActivityFragmentMembersInjector = BrowserActivityFragment_MembersInjector.create(this.vimeoPresenterProvider, this.sharedPreferencesProvider);
    }

    public void inject(download_url download_url) {
        this.download_urlMembersInjector.injectMembers(download_url);
    }

    public void inject(BrowserActivity browserActivity) {
        this.browserActivityMembersInjector.injectMembers(browserActivity);
    }

    @Override
    public void inject(BrowserActivityFragment browserActivityFragment) {
        this.browserActivityFragmentMembersInjector.injectMembers(browserActivityFragment);
    }
}
