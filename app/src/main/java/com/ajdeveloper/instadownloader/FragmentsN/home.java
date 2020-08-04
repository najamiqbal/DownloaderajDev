package com.ajdeveloper.instadownloader.FragmentsN;

import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;
import com.ajdeveloper.instadownloader.Activities.BaseActivity;
import com.ajdeveloper.instadownloader.Adapters.DailymotionVideoAdapter;
import com.ajdeveloper.instadownloader.Download_HLS.DownloadService;
import com.ajdeveloper.instadownloader.Downloader.Constants;
import com.ajdeveloper.instadownloader.Interface.CustomItemClickListener;
import com.ajdeveloper.instadownloader.Interface.DailymotionApiInterface;
import com.ajdeveloper.instadownloader.Interface.VideoApiInterface;
import com.ajdeveloper.instadownloader.Network.RetrofitClientInstance;
import com.ajdeveloper.instadownloader.R;
import com.ajdeveloper.instadownloader.Utils.BSUtils_D;
import com.ajdeveloper.instadownloader.Utils.DialogUtils;
import com.ajdeveloper.instadownloader.Utils.UtilMethods;
import com.ajdeveloper.instadownloader.app.AVD;
import com.ajdeveloper.instadownloader.database.SharedPref;
import com.ajdeveloper.instadownloader.model.HLS.Example;
import com.ajdeveloper.instadownloader.model.HLS.Format;
import com.ajdeveloper.instadownloader.model.dailymotion.DmChannel;
import com.ajdeveloper.instadownloader.model.dailymotion.DmVideo;
import com.ajdeveloper.instadownloader.model.dailymotion.VideosList;

@SuppressWarnings("WrongConstant")
public class home extends Fragment {
    private static final String[] DM_QUERY_LIST = {"getFeaturedVideos", "getTrendingVideos", "getVideos"};
    public static final String TAG = "home";
    public static home sInstance;
    VideoApiInterface apiInterface;
    DailymotionApiInterface apiService;
    public DailymotionVideoAdapter mAdapter;
    LinkedList<DmVideo> mComboVideoDmVideo;
    Context mContext;
    @BindView(R.id.progressLoadMoreHome)
    ProgressBar mProgressBarLoadMore;
    ProgressBar progressBar;
    public RecyclerView recyclerView;
    View rootView;

    public static home newInstance() {
        return new home();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public Context getContext() {
        return this.mContext;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.rootView = layoutInflater.inflate(R.layout.fragment_home, viewGroup, false);
        ButterKnife.bind(this, this.rootView);
        this.apiInterface = (VideoApiInterface) RetrofitClientInstance.getRetrofitInstance().create(VideoApiInterface.class);
        sInstance = this;
        initView();
        this.mContext = getActivity();
        try {
            if (AVD.getINSTANCE().getTrendingList() != null) {
                if (AVD.getINSTANCE().getTrendingList().size() != 0) {
                    setAdapter(AVD.getINSTANCE().getTrendingList());
                    return this.rootView;
                }
            }
            loadTrendings("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.rootView;
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    public void loadTrendings(String str) {
        this.apiService = (DailymotionApiInterface) new Builder().baseUrl("https://api.dailymotion.com/").addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build().create(DailymotionApiInterface.class);
        HashMap hashMap = new HashMap();
        hashMap.put("fields", "channel,country,created_time,duration,id,language,likes_total,thumbnail_180_url,title,views_total");
        hashMap.put("sort", "visited");
        hashMap.put("page", str);
        hashMap.put("limit", "100");
        getCurrentCall(hashMap).enqueue(new Callback<VideosList>() {
            public void onResponse(Call<VideosList> call, Response<VideosList> response) {
                if (response.body() == null || ((VideosList) response.body()).getVideosList() == null || ((VideosList) response.body()).getVideosList().size() <= 0) {
                    home.this.showToast("No Videos Found!");
                    return;
                }
                AVD.getINSTANCE().getTrendingList().addAll(new ArrayList(((VideosList) response.body()).getVideosList()));
                home.this.setAdapter(AVD.getINSTANCE().getTrendingList());
                home.this.checkIndex();
            }

            public void onFailure(Call<VideosList> call, Throwable th) {
                home.this.showToast("Failed! ");
            }
        });
    }

    /* access modifiers changed from: private */
    public void checkIndex() {
        int spInt = SharedPref.getInstance(getContext()).getSpInt("key_dm_query_index", 0) + 1;
        if (spInt >= 3) {
            spInt = 0;
        }
        SharedPref.getInstance(getContext()).setSpInt("key_dm_query_index", spInt);
    }

    private Call<VideosList> getCurrentCall(HashMap<String, String> hashMap) {
        if (AVD.getINSTANCE().getDmChannels() == null || AVD.getINSTANCE().getDmChannels().size() <= 0) {
            String[] strArr = {"fun", "music", "news", "comedy", "people", "tech"};
            return this.apiService.getVideosByChannel(strArr[new Random().nextInt(strArr.length)], hashMap);
        }
        return this.apiService.getVideosByChannel(((DmChannel) AVD.getINSTANCE().getDmChannels().get(new Random().nextInt(AVD.getINSTANCE().getDmChannels().size()))).getId(), hashMap);
    }

    /* access modifiers changed from: private */
    public void setAdapter(ArrayList<DmVideo> arrayList) {
        this.mComboVideoDmVideo = new LinkedList<>();
        this.mComboVideoDmVideo.addAll(AVD.getINSTANCE().getTrendingList());
        Collections.shuffle(this.mComboVideoDmVideo);
        DmVideo dmVideo = new DmVideo();
        dmVideo.setItemVieType(2);
        DmVideo dmVideo2 = new DmVideo();
        dmVideo2.setItemVieType(3);
        this.mComboVideoDmVideo.add(0, dmVideo);
        this.mComboVideoDmVideo.add(1, dmVideo2);
        int i = 3;
        for (int i2 = i; i2 < this.mComboVideoDmVideo.size(); i2 += i) {
            DmVideo dmVideo3 = new DmVideo();
            dmVideo3.setItemVieType(1);
            this.mComboVideoDmVideo.add(i2, dmVideo3);
        }
        DailymotionVideoAdapter dailymotionVideoAdapter = new DailymotionVideoAdapter(getContext(), this.mComboVideoDmVideo, new CustomItemClickListener() {
            public void onItemClick(View view, final int i) {
                if (view == null) {
                    ((BaseActivity) home.this.getActivity()).getStoragePermission(new BaseActivity.PermissionCallBack() {
                        public void permissionGranted() {
                            home home = home.this;
                            StringBuilder sb = new StringBuilder();
                            sb.append("https://www.dailymotion.com/video/");
                            sb.append(((DmVideo) home.this.mComboVideoDmVideo.get(i)).getId());
                            home.getVideosList(sb.toString());
                        }
                    });
                }
            }

            public void onShareClick(View view, int i) {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setAction("android.intent.action.SEND");
                StringBuilder sb = new StringBuilder();
                sb.append("https://www.dailymotion.com/video/");
                sb.append(((DmVideo) home.this.mComboVideoDmVideo.get(i)).getId());
                intent.putExtra("android.intent.extra.TEXT", sb.toString());
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.SUBJECT", ((DmVideo) home.this.mComboVideoDmVideo.get(i)).getTitle());
                home.this.startActivity(intent);
            }
        }, TAG, this.mProgressBarLoadMore);
        this.mAdapter = dailymotionVideoAdapter;
        this.recyclerView.setAdapter(this.mAdapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.progressBar.setVisibility(4);
    }

    /* access modifiers changed from: private */
    public void getVideosList(String str) {
        DialogUtils.showSimpleProgressDialog(getActivity());
        this.apiInterface.getUrl(str).enqueue(new Callback<Example>() {
            public void onResponse(Call<Example> call, Response<Example> response) {
                DialogUtils.closeProgressDialog();
                if (response != null) {
                    Example example = (Example) response.body();
                    if (example != null) {
                        home.this.manageResponse(example);
                    } else {
                        home.this.showToast("Something went wrong");
                    }
                } else {
                    home.this.showToast("Check Internet Connection");
                }
            }

            public void onFailure(Call<Example> call, Throwable th) {
                DialogUtils.closeProgressDialog();
                Log.i("Video ", th.getMessage());
            }
        });
    }

    /* access modifiers changed from: private */
    public void manageResponse(Example example) {
        if (example != null) {
            List<Format> formats = example.getInfo().getFormats();
            ArrayList arrayList = new ArrayList();
            for (Format format : formats) {
                String formatId = format.getFormatId();
                if (formatId.contains("hls-144-0")) {
                    format.setFormatId("144");
                    arrayList.add(format);
                } else if (formatId.contains("hls-240-0")) {
                    format.setFormatId("240");
                    arrayList.add(format);
                } else if (formatId.contains("hls-380-0")) {
                    format.setFormatId("380");
                    arrayList.add(format);
                } else if (formatId.contains("hls-480-0")) {
                    format.setFormatId("480");
                    arrayList.add(format);
                } else if (formatId.contains("hls-720-0")) {
                    format.setFormatId("720");
                    arrayList.add(format);
                }
            }
            showDM_DownloadSheet(arrayList, example.getInfo().getDescription());
            return;
        }
        Toast.makeText(this.mContext, "SomeThing Wrong Try Again ", 0).show();
    }

    private void showDM_DownloadSheet(List<Format> list, String str) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        final StringBuilder sb12 = new StringBuilder();
        sb12.append("Video_");
        sb12.append(simpleDateFormat.format(new Date()));
        final String sb123 = sb12.toString();
        if (!getActivity().isFinishing()) {
            new BSUtils_D(getContext()).showListSheet(str, "", list, TAG, new BSUtils_D.BSCallBack() {
                public void onBsHidden() {
                }

                public void onDownloadClicked(Format format) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(Environment.getExternalStorageDirectory());
                    sb.append(File.separator);
                    sb.append("Download/FreeDownloader");
                    sb.append(File.separator);
                    String sb2 = sb.toString();
                    Intent intent = new Intent(home.this.getContext(), DownloadService.class);
                    new UtilMethods(home.this.getContext()).showCustomToast("Download Started...", 1);
                    intent.putExtra("URL", format.getUrl());
                    intent.putExtra("TITLE", sb123);
                    intent.putExtra("PATH", Constants.Parent + Constants.VIDEOS);
                    if (VERSION.SDK_INT >= 26) {
                        home.this.mContext.startForegroundService(intent);
                    } else {
                        home.this.mContext.startService(intent);
                    }
                }
            });
        }
    }

    private void initView() {
        this.progressBar = (ProgressBar) this.rootView.findViewById(R.id.progressBar);
        this.recyclerView = (RecyclerView) this.rootView.findViewById(R.id.recyclerView);
    }

    /* access modifiers changed from: private */
    public void showToast(String str) {
        try {
            if (getContext() != null) {
                Toast.makeText(getContext(), str, 0).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}
