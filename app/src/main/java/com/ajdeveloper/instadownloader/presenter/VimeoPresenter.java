package com.ajdeveloper.instadownloader.presenter;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import com.ajdeveloper.instadownloader.VimeoView;
import com.ajdeveloper.instadownloader.entity.VideoEntity;
import com.ajdeveloper.instadownloader.entity.VideoEntityJson;
import com.ajdeveloper.instadownloader.model.DownloadModel;

public class VimeoPresenter {
    private Context context;
    DownloadModel downloadModel;
    private ArrayList<VideoEntity> listVideo = new ArrayList<>();
    private VimeoView vimeoView;

    public VimeoPresenter(Context context2) {
        this.context = context2;
    }

    public void setView(VimeoView vimeoView2) {
        this.vimeoView = vimeoView2;
    }

    public void getVideoList(String str) {
        this.downloadModel.getVideoList(str).subscribe(new Observer<List<VideoEntityJson>>() {
            public void onComplete() {
            }

            public void onError(Throwable th) {
            }

            public void onSubscribe(Disposable disposable) {
            }

            public void onNext(List<VideoEntityJson> list) {
                VimeoPresenter.this.vimeoView.setVideoList(list);
            }
        });
    }

    public void downloadVideo(String str, String str2, String str3) {
        this.downloadModel.downloadVideo(str, str2, str3);
    }
}
