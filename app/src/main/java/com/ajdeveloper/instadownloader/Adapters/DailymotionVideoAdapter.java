package com.ajdeveloper.instadownloader.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dailymotion.android.player.sdk.PlayerWebView;
import com.dailymotion.android.player.sdk.PlayerWebView.PlayerEventListener;
import com.dailymotion.android.player.sdk.events.FullScreenToggleRequestedEvent;
import com.dailymotion.android.player.sdk.events.PlayerEvent;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.formats.NativeAd;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import butterknife.ButterKnife;
import com.ajdeveloper.instadownloader.Activities.BrowserActivity;
import com.ajdeveloper.instadownloader.Activities.FullScreenVideoActivity;
import com.ajdeveloper.instadownloader.FragmentsN.home;
import com.ajdeveloper.instadownloader.Interface.CustomItemClickListener;
import com.ajdeveloper.instadownloader.MainActivity;
import com.ajdeveloper.instadownloader.R;
import com.ajdeveloper.instadownloader.Utils.AdsUtil;
import com.ajdeveloper.instadownloader.Utils.NumberUtils;
import com.ajdeveloper.instadownloader.Utils.UtilMethods;
import com.ajdeveloper.instadownloader.database.SharedPref;
import com.ajdeveloper.instadownloader.model.dailymotion.DmVideo;

@SuppressLint("WrongConstant")
public class DailymotionVideoAdapter extends RecyclerView.Adapter<DailymotionVideoAdapter.DMViewHolder> {
    public static final String TAG = "DailymotionVideoAdapter";
    public static RefreshData refreshData;
    private int adPositioninterval;
    CustomItemClickListener listener;
    private Context mContext;
    private int mCurrentLimit = 10;
    private PlayerWebView mCurrentPlayer = null;
    private LinkedList<DmVideo> mDmVideosList;
    private boolean mFullscreen = false;
    private boolean mIsLoading;
    private ProgressBar mLoadMoreProgress;
    private int mVideoPlayerPosition = -1;
    private String mWhoIs = "";

    public class DMViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout adView;
        private LinearLayout dailymotionLayout;
        private LinearLayout fbLayout;
        private LinearLayout instaLayout;
        private AdView mAdView;
        private Button mBtnSearch;
        ImageView mImgIcDownload;
        ImageView mImgIcPlay;
        ImageView mImgIcShare;
        ImageView mImgThumbnail;
        PlayerWebView mPlayerWebView;
        TextView mTitle;
        TextView mTvTotalLikes;
        TextView mTvTotalViews;
        private LinearLayout twitterLayout;
        private LinearLayout vimeoLayout;
        private LinearLayout whatsLayout;

        public DMViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bindVideo(final DmVideo dmVideo) {
            if (dmVideo != null) {
                init(this.itemView);
                this.mTitle.setText(dmVideo.getTitle());
                this.mTvTotalLikes.setText(NumberUtils.coolFormat(dmVideo.getLikesTotal(), 0));
                this.mTvTotalViews.setText(NumberUtils.coolFormat(dmVideo.getViewsTotal(), 0));
                try {
                    Glide.with(DailymotionVideoAdapter.this.mContext).load(dmVideo.getThumbnail_480_url()).diskCacheStrategy(DiskCacheStrategy.RESULT).into(this.mImgThumbnail);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                this.mImgIcPlay.setOnClickListener(new OnClickListener() {
//                    private final /* synthetic */ DmVideo f$1;
//
//                    {
//                        this.f$1 = r2;
//                    }

                    public final void onClick(View view) {
                        DMViewHolder.this.playVideo(dmVideo);
                    }
                });
                this.mImgThumbnail.setOnClickListener(new OnClickListener() {
//                    private final /* synthetic */ DmVideo f$1;
//
//                    {
//                        this.f$1 = r2;
//                    }

                    public final void onClick(View view) {
                        DMViewHolder.this.playVideo(dmVideo);
                    }
                });
                this.mImgIcDownload.setOnClickListener(new OnClickListener() {
                    public final void onClick(View view) {
                        DailymotionVideoAdapter.this.listener.onItemClick(null, DMViewHolder.this.getAdapterPosition());
                    }
                });
                this.mImgIcShare.setOnClickListener(new OnClickListener() {
                    public final void onClick(View view) {
                        DailymotionVideoAdapter.this.listener.onShareClick(view, DMViewHolder.this.getAdapterPosition());
                    }
                });
                this.mPlayerWebView.setPlayerEventListener(new PlayerEventListener() {
                    public void onEvent(PlayerEvent playerEvent) {
                        if (playerEvent instanceof FullScreenToggleRequestedEvent) {
                            FullScreenVideoActivity.start(DailymotionVideoAdapter.this.mContext, dmVideo);
                            DMViewHolder.this.mPlayerWebView.setFullscreenButton(false);
                        }
                    }
                });
            }
        }

        @SuppressLint("WrongConstant")
        private void init(View view) {
            this.mImgThumbnail = (ImageView) view.findViewById(R.id.imgIcThumbnailItemVideo);
            this.mPlayerWebView = (PlayerWebView) view.findViewById(R.id.playerViewItemVideo);
            this.mTitle = (TextView) view.findViewById(R.id.tvVideoTitleItemVideo);
            this.mImgIcShare = (ImageView) view.findViewById(R.id.imgIcShareItemVideo);
            this.mImgIcDownload = (ImageView) view.findViewById(R.id.imgIcDownloadItemVideo);
            this.mTvTotalViews = (TextView) view.findViewById(R.id.tvTotalViewsItemVideo);
            this.mTvTotalLikes = (TextView) view.findViewById(R.id.tvTotalLikesItemVideo);
            this.mImgIcPlay = (ImageView) view.findViewById(R.id.imgIcPlayItemVideo);
            this.mImgIcPlay.setVisibility(0);
            this.mImgThumbnail.setVisibility(0);
            this.mPlayerWebView.setVisibility(8);
            this.mPlayerWebView.stopLoading();
            this.mPlayerWebView.pause();
            this.mPlayerWebView.getPosition();
        }

        @SuppressLint("WrongConstant")
        private void playVideo(DmVideo dmVideo) {
            DailymotionVideoAdapter.this.mCurrentPlayer = this.mPlayerWebView;
            DailymotionVideoAdapter.this.mVideoPlayerPosition = getAdapterPosition();
            this.mImgIcPlay.setVisibility(8);
            this.mImgThumbnail.setVisibility(8);
            this.mPlayerWebView.setVisibility(0);
            this.mPlayerWebView.load(dmVideo.getId(), new HashMap());
            this.mPlayerWebView.setFullscreenButton(true);
            this.mPlayerWebView.getPosition();
            if (DailymotionVideoAdapter.refreshData != null) {
                DailymotionVideoAdapter.refreshData.onRefreshData(this.mPlayerWebView);
            }
        }

        public void bindAd() {
            this.mAdView = (AdView) this.itemView.findViewById(R.id.adViewRecBanner);
            AdsUtil.getInstance(DailymotionVideoAdapter.this.mContext).loadBannerAd(this.mAdView, DailymotionVideoAdapter.TAG, new AdsUtil.AdsCallBack() {
                public void onAdFailedToLoad(String str, String str2) {
                }
            });
        }

        public void bindSearchButton() {
            this.mBtnSearch = (Button) this.itemView.findViewById(R.id.btnSearchHome);
            this.mBtnSearch.setOnClickListener(new OnClickListener() {
                public final void onClick(View view) {
                    BrowserActivity.start(DailymotionVideoAdapter.this.mContext, "");
                }
            });
        }

        public void bindTopContainer() {
            this.fbLayout = (LinearLayout) this.itemView.findViewById(R.id.fb_layout);
            this.vimeoLayout = (LinearLayout) this.itemView.findViewById(R.id.vimeo_layout);
            this.dailymotionLayout = (LinearLayout) this.itemView.findViewById(R.id.dailymotion_layout);
            this.twitterLayout = (LinearLayout) this.itemView.findViewById(R.id.twitter_layout);
            this.fbLayout.setOnClickListener(new OnClickListener() {
                public final void onClick(View view) {
                    DailymotionVideoAdapter.this.showRateUsIfHaveTo();
                    BrowserActivity.start(DailymotionVideoAdapter.this.mContext, "https://m.facebook.com");
                }
            });
            this.vimeoLayout.setOnClickListener(new OnClickListener() {
                public final void onClick(View view) {
                    DailymotionVideoAdapter.this.showRateUsIfHaveTo();
                    BrowserActivity.start(DailymotionVideoAdapter.this.mContext, "https://vimeo.com/watch");
                }
            });
            this.dailymotionLayout.setOnClickListener(new OnClickListener() {
                public final void onClick(View view) {
                    DailymotionVideoAdapter.this.showRateUsIfHaveTo();
                    BrowserActivity.start(DailymotionVideoAdapter.this.mContext, "https://www.dailymotion.com");
                }
            });
            this.twitterLayout.setOnClickListener(new OnClickListener() {
                public final void onClick(View view) {
                    DailymotionVideoAdapter.this.showRateUsIfHaveTo();
                    BrowserActivity.start(DailymotionVideoAdapter.this.mContext, "https://www.twitter.com");
                }
            });
        }

        private void bindNativeAd() {
            if (!SharedPref.getInstance(DailymotionVideoAdapter.this.mContext).getSpBoolean("remove_ads", false)) {

            }
        }

        private void inflateAd(NativeAd nativeAd2) {
        }
    }

    public interface RefreshData {
        void onRefreshData(PlayerWebView playerWebView);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public static void setOnRefreshData(RefreshData refreshData2) {
        refreshData = refreshData2;
    }

    public DailymotionVideoAdapter(Context context, LinkedList<DmVideo> linkedList, CustomItemClickListener customItemClickListener, String str, ProgressBar progressBar) {
        this.mContext = context;
        this.listener = customItemClickListener;
        this.mDmVideosList = linkedList;
        this.mWhoIs = str;
        setHasStableIds(true);
        if (this.mWhoIs.equalsIgnoreCase(home.TAG)) {
            this.adPositioninterval = 2;
        } else {
            this.adPositioninterval = 4;
        }
        this.mLoadMoreProgress = progressBar;
    }

    public void onViewDetachedFromWindow(DMViewHolder dMViewHolder) {
        if (dMViewHolder != null && dMViewHolder.getItemViewType() == 4 && this.mVideoPlayerPosition != -1 && dMViewHolder.getAdapterPosition() == this.mVideoPlayerPosition) {
            PlayerWebView playerWebView = this.mCurrentPlayer;
            if (playerWebView != null) {
                playerWebView.stopLoading();
                this.mCurrentPlayer.pause();
                this.mCurrentPlayer.getPosition();
                this.mCurrentPlayer = null;
                this.mVideoPlayerPosition = -1;
            }
        }
        super.onViewDetachedFromWindow(dMViewHolder);
    }

    public DMViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        @SuppressLint("WrongConstant") LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService("layout_inflater");
        if (this.mWhoIs.equalsIgnoreCase(home.TAG)) {
            if (i == 2) {
                view = layoutInflater.inflate(R.layout.layout_btns_home_top, viewGroup, false);
            } else if (i == 3) {
                view = layoutInflater.inflate(R.layout.layout_btn_search_home, viewGroup, false);
            } else if (i == 1) {
                view = layoutInflater.inflate(R.layout.rec_banner_adview, viewGroup, false);
//                view = layoutInflater.inflate(R.layout.fb_native_ad_layout, viewGroup, false);
            } else {
                view = layoutInflater.inflate(R.layout.item_video, viewGroup, false);
            }
        } else if (i == 1) {
            view = layoutInflater.inflate(R.layout.rec_banner_adview, viewGroup, false);
        } else {
            view = layoutInflater.inflate(R.layout.item_video, viewGroup, false);
        }
        return new DMViewHolder(view);
    }

    public void onBindViewHolder(DMViewHolder dMViewHolder, int i) {
        int i2 = this.mCurrentLimit;
        int i3 = i + 1;
        if (i2 == i3 && i2 <= this.mDmVideosList.size() && !this.mIsLoading) {
            this.mLoadMoreProgress.setVisibility(0);
            this.mIsLoading = true;
            loadThumbnail(0, i3);
        }
        DmVideo dmVideo = (DmVideo) this.mDmVideosList.get(i);
        if (this.mWhoIs.equalsIgnoreCase(home.TAG)) {
            if (dmVideo.getItemVieType() == 2) {
                dMViewHolder.bindTopContainer();
            } else if (dmVideo.getItemVieType() == 3) {
                dMViewHolder.bindSearchButton();
            } else if (dmVideo.getItemVieType() == 1) {
                try {
                        //dMViewHolder.bindNativeAd();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                dMViewHolder.bindVideo((DmVideo) this.mDmVideosList.get(i));
            }
        } else if (dmVideo.getItemVieType() != 1) {
            dMViewHolder.bindVideo((DmVideo) this.mDmVideosList.get(i));
        } else {
            //dMViewHolder.bindAd();
        }
    }

    private void loadThumbnail(final int i, final int i2) {
        if (i <= 2) {
            try {
                if (i2 < this.mDmVideosList.size()) {
                    if (this.mDmVideosList.get(i2) == null) {
                        loadThumbnail(i, i2 + 1);
                    } else {
                        Glide.with(this.mContext).load(((DmVideo) this.mDmVideosList.get(i2)).getThumbnail_480_url()).downloadOnly(new SimpleTarget<File>() {
                            public void onResourceReady(File file, GlideAnimation<? super File> glideAnimation) {
                                DailymotionVideoAdapter.this.loadThumbnail(i + 1, i2 + 1);
                            }

                            public void onLoadFailed(Exception exc, Drawable drawable) {
                                DailymotionVideoAdapter.this.loadThumbnail(i + 1, i2 + 1);
                            }
                        });
                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                loadThumbnail(i + 1, i2 + 1);
            }
        }
        this.mIsLoading = false;
        this.mCurrentLimit += 10;
        notifyItemChanged(i2 - i);
        this.mLoadMoreProgress.setVisibility(8);
    }

    public int getItemViewType(int i) {
        return ((DmVideo) this.mDmVideosList.get(i)).getItemVieType();
    }

    public int getItemCount() {
        return this.mCurrentLimit <= this.mDmVideosList.size() ? this.mCurrentLimit : this.mDmVideosList.size();
    }

    private void showRateUsIfHaveTo() {
        try {
            if (new UtilMethods(this.mContext).shouldShowRateUsDialog()) {
                ((MainActivity) this.mContext).rateUs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
