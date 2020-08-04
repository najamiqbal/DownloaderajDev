package com.ajdeveloper.instadownloader.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import com.ajdeveloper.instadownloader.Downloader.InstaDownloder;
import com.ajdeveloper.instadownloader.Interfaces.CustomCallbackListener;
import com.ajdeveloper.instadownloader.R;
import com.ajdeveloper.instadownloader.Utils.GeneratingDownloadLinks;
import com.ajdeveloper.instadownloader.Utils.Utilities;
import com.ajdeveloper.instadownloader.Utils.WebTools;
import com.ajdeveloper.instadownloader.Utils.iConstants;
import com.ajdeveloper.instadownloader.Utils.iUtils;

import static com.ajdeveloper.instadownloader.Utils.iConstants.DISABLE_DOWNLOADING;

@SuppressLint("ValidFragment")
public class DownloaderFragment extends Fragment {

    private String text;

    public DownloaderFragment(String text) {
        this.text = text;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate videodownloader layout for this fragment
        return inflater.inflate(R.layout.fragment_downloader, container, false);
    }

    private Context context;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = view.getContext();
        TextView tvURL = view.findViewById(R.id.tv_url);
        tvURL.setText(text);
        final EditText etDownload = view.findViewById(R.id.et_url);
        ImageView btnDownload = view.findViewById(R.id.btn_url);
        frameLayout = view.findViewById(R.id.fl_ad_place_holder);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = etDownload.getText().toString();
                for (CharSequence contains : DISABLE_DOWNLOADING) {
                    if (url.contains(contains)) {
                        etDownload.setError("Please enter a valid URL!");
                        return;
                    }
                }
                if (text.contains("Instagram")) {
                    new InstaDownloder(context, url, new CustomCallbackListener() {

                        @Override
                        public void onItemClicked(int position) {
                        }
                    });
                    etDownload.setText("");
                    return;
                }
                if (url.contains("dailymotion.com"))
                    dailyMotion(url);
                else if (URLUtil.isValidUrl(url) && !text.contains("DailyMotion URL"))
                    Utilities.startDownload(context, url);
                else
                    etDownload.setError("Please enter a valid URL!");
            }
        });
        refreshAd();
    }

    private void dailyMotion(String url) {
        url = WebTools.builderURL(url);
        if (iUtils.checkURL(url)) {
            GeneratingDownloadLinks.Start(context, url, "");
        } else {
            iUtils.ShowToast(context, iConstants.URL_NOT_SUPPORTED);
        }
    }

    /**
     * Populates a {@link UnifiedNativeAdView} object with data from a given
     * {@link UnifiedNativeAd}.
     *
     * @param nativeAd videodownloader object containing videodownloader ad's assets
     * @param adView   videodownloader view to be populated
     */
    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set videodownloader media view. Media content will be automatically populated in videodownloader media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells videodownloader Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate videodownloader adView's MediaView
        // with videodownloader media content from this native ad.
        adView.setNativeAd(nativeAd);

        // Get videodownloader video controller for videodownloader ad. One will always be provided, even if videodownloader ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getVideoController();

        // Updates videodownloader UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {

            // Create a new VideoLifecycleCallbacks object and pass it to videodownloader VideoController. The
            // VideoController will call methods on this object when events occur in videodownloader video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in videodownloader same UI location.
                    super.onVideoEnd();
                    refreshAd();
                }
            });
        } else {
            //No video in nativeAd
        }
    }

    /**
     * Creates a request for a new native ad based on videodownloader boolean parameters and calls videodownloader
     * corresponding "populate" method when one is successfully returned.
     */
    private FrameLayout frameLayout;
    private UnifiedNativeAd nativeAd;

    private void refreshAd() {

        AdLoader.Builder builder = new AdLoader.Builder(context, getString(R.string.native_advanced_ad_keys));

        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            // OnUnifiedNativeAdLoadedListener implementation.
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                // You must call destroy on old ads when you are done with them,
                // otherwise you will have a memory leak.
                if (nativeAd != null) {
                    nativeAd.destroy();
                }
                nativeAd = unifiedNativeAd;
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
                        .inflate(R.layout.ad_unified, null);
                populateUnifiedNativeAdView(unifiedNativeAd, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);
            }

        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                refreshAd();
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

}
