package com.ajdeveloper.instadownloader.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.ajdeveloper.instadownloader.FragmentsN.VideosFragment;
import com.ajdeveloper.instadownloader.R;
import com.ajdeveloper.instadownloader.Utils.AdsUtil;
import com.ajdeveloper.instadownloader.app.AVD;
import com.ajdeveloper.instadownloader.model.dailymotion.ChannelsList;
import com.ajdeveloper.instadownloader.model.dailymotion.DmChannel;

@SuppressWarnings("WrongConstant")
public class VideosActivity extends BaseActivity {
    private static final String TAG = "VideosActivity";
    @BindView(R.id.bottomNavigationVideosActivity)
    BottomNavigationView mBottomNavigationView;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    @BindView(R.id.tabLayoutVideosActivity)
    TabLayout mTabLayout;
    @BindView(R.id.viewPagerVideosActivity)
    ViewPager mViewPager;

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public Fragment getItem(int i) {
            return VideosFragment.newInstance((DmChannel) AVD.getINSTANCE().getDmChannels().get(i), i);
        }

        public int getCount() {
            return AVD.getINSTANCE().getDmChannels().size();
        }

        public CharSequence getPageTitle(int i) {
            return ((DmChannel) AVD.getINSTANCE().getDmChannels().get(i)).getName();
        }
    }

    public static void start(Context context) {
        ((Activity) context).startActivityForResult(new Intent(context, VideosActivity.class), 111);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_videos);
        ButterKnife.bind(this);
        loadInterstitialAd("ad_videos");
        if (AVD.getINSTANCE().getDmChannels() == null || AVD.getINSTANCE().getDmChannels().size() == 0) {
            getDmChannelsList(new Callback<ChannelsList>() {
                public void onResponse(Call<ChannelsList> call, Response<ChannelsList> response) {
                    if (((ChannelsList) response.body()).getChannelsList() == null || ((ChannelsList) response.body()).getChannelsList().size() == 0) {
                        Toast.makeText(VideosActivity.this, "No Videos Found!", 0).show();
                        return;
                    }
                    AVD.getINSTANCE().setDmChannels(new LinkedList(((ChannelsList) response.body()).getChannelsList()));
                    VideosActivity.this.setPagerAdapter();
                }

                public void onFailure(Call<ChannelsList> call, Throwable th) {
                    VideosActivity videosActivity = VideosActivity.this;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Error! ");
                    sb.append(th.getMessage());
                    Toast.makeText(videosActivity, sb.toString(), 0).show();
                }
            });
        } else {
            setPagerAdapter();
        }
        this.mBottomNavigationView.setItemIconTintList(null);
        this.mBottomNavigationView.setSelectedItemId(R.id.menuItemVideosVideosActivity);
        this.mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menuItemHomeVideosActivity) {
                    VideosActivity.this.onBackPressed();
                }
                return false;
            }
        });
    }

    private void loadInterstitialAd(String str) {
        AdsUtil.getInstance(this).loadInterstitialAd(TAG, str, new AdsUtil.AdsCallBack() {
            public void onAdFailedToLoad(String str, String str2) {
            }
        });
    }

    /* access modifiers changed from: private */
    public void setPagerAdapter() {
        this.mTabLayout.setupWithViewPager(this.mViewPager);
        this.mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        this.mViewPager.setAdapter(this.mSectionsPagerAdapter);
        this.mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrollStateChanged(int i) {
            }

            public void onPageSelected(int i) {
            }

            public void onPageScrolled(int i, float f, int i2) {
                EventBus.getDefault().post(new Object());
            }
        });
        this.mViewPager.setCurrentItem(0);
    }

    public void onBackPressed() {
        setResult(-1);
        super.onBackPressed();
    }
}
