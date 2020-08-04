package com.ajdeveloper.instadownloader.FragmentsN;

import android.content.SharedPreferences;

import javax.inject.Provider;

import dagger.MembersInjector;
import com.ajdeveloper.instadownloader.presenter.VimeoPresenter;

public final class BrowserActivityFragment_MembersInjector implements MembersInjector<BrowserActivityFragment> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final Provider<SharedPreferences> sharedPreferencesProvider;
    private final Provider<VimeoPresenter> vimeoPresenterProvider;

    public BrowserActivityFragment_MembersInjector(Provider<VimeoPresenter> provider, Provider<SharedPreferences> provider2) {
        this.vimeoPresenterProvider = provider;
        this.sharedPreferencesProvider = provider2;
    }

    public static MembersInjector<BrowserActivityFragment> create(Provider<VimeoPresenter> provider, Provider<SharedPreferences> provider2) {
        return new BrowserActivityFragment_MembersInjector(provider, provider2);
    }

    public void injectMembers(BrowserActivityFragment browserActivityFragment) {
        if (browserActivityFragment != null) {
            browserActivityFragment.vimeoPresenter = (VimeoPresenter) this.vimeoPresenterProvider.get();
            browserActivityFragment.sharedPreferences = (SharedPreferences) this.sharedPreferencesProvider.get();
            return;
        }
        throw new NullPointerException("Cannot inject members into a null reference");
    }
}
