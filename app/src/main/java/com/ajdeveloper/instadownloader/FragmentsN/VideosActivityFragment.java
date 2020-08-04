package com.ajdeveloper.instadownloader.FragmentsN;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.ajdeveloper.instadownloader.NaviDrawer;
import com.ajdeveloper.instadownloader.R;
import com.ajdeveloper.instadownloader.Utils.AdsUtil;
import com.ajdeveloper.instadownloader.app.AVD;
import com.ajdeveloper.instadownloader.model.dailymotion.ChannelsList;
import com.ajdeveloper.instadownloader.model.dailymotion.DmChannel;

@SuppressWarnings("WrongConstant")
public class VideosActivityFragment extends Fragment {
    private static final String TAG = "VideosActivity";
    @BindView(R.id.bottomNavigationVideosActivity)
    BottomNavigationView mBottomNavigationView;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    @BindView(R.id.tabLayoutVideosActivity)
    TabLayout mTabLayout;
    @BindView(R.id.viewPagerVideosActivity)
    ViewPager mViewPager;

    public VideosActivityFragment() {}

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
        ((Activity) context).startActivityForResult(new Intent(context, VideosActivityFragment.class), 111);
    }

    /* access modifiers changed from: protected */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_videos, container, false);
        ButterKnife.bind(this, view);
//        loadInterstitialAd("ad_videos");
        if (AVD.getINSTANCE().getDmChannels() == null || AVD.getINSTANCE().getDmChannels().size() == 0) {
            ((NaviDrawer) getActivity()).getDmChannelsList(new Callback<ChannelsList>() {
                public void onResponse(Call<ChannelsList> call, Response<ChannelsList> response) {
                    if (((ChannelsList) response.body()).getChannelsList() == null || ((ChannelsList) response.body()).getChannelsList().size() == 0) {
                        Toast.makeText(getContext(), "No Videos Found!", 0).show();
                        return;
                    }
                    AVD.getINSTANCE().setDmChannels(new LinkedList(((ChannelsList) response.body()).getChannelsList()));
                    VideosActivityFragment.this.setPagerAdapter();
                }

                public void onFailure(Call<ChannelsList> call, Throwable th) {
                    VideosActivityFragment videosActivity = VideosActivityFragment.this;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Error! ");
                    sb.append(th.getMessage());
                    Toast.makeText(getContext(), sb.toString(), 0).show();
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
//                    VideosActivityFragment.this.onBackPressed();
                }
                return false;
            }
        });
        return view;
    }

    private void loadInterstitialAd(String str) {
        AdsUtil.getInstance(getContext()).loadInterstitialAd(TAG, str, new AdsUtil.AdsCallBack() {
            public void onAdFailedToLoad(String str, String str2) {
            }
        });
    }

    /* access modifiers changed from: private */
    public void setPagerAdapter() {
        this.mTabLayout.setupWithViewPager(this.mViewPager);
        this.mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
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
}
