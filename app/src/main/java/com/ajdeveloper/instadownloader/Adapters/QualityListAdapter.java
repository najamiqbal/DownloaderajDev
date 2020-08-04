package com.ajdeveloper.instadownloader.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.ajdeveloper.instadownloader.R;

public class QualityListAdapter extends RecyclerView.Adapter<QualityListAdapter.QualityListHolder> {
    private RecyclerCallBack mCallBack;
    private Context mContext;
    private ArrayList<String> mList;
    private String mTag;

    public static class QualityListHolder extends RecyclerView.ViewHolder {
        /* access modifiers changed from: private */
        public ImageView mImgIcDownload;
        private TextView mTvSize;
        public TextView mTvTitle;

        public QualityListHolder(View view) {
            super(view);
            this.mTvTitle = (TextView) view.findViewById(R.id.tvQualityTitleItemList);
            this.mTvSize = (TextView) view.findViewById(R.id.tvSizeItemList);
            this.mImgIcDownload = (ImageView) view.findViewById(R.id.imgIcDownloadItemList);
        }
    }

    public interface RecyclerCallBack {
        void onItemClicked(String str);
    }

    public QualityListAdapter(Context context, ArrayList<String> arrayList, String str, RecyclerCallBack recyclerCallBack) {
        this.mContext = context;
        this.mList = arrayList;
        this.mCallBack = recyclerCallBack;
        this.mTag = str;
    }

    public QualityListHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new QualityListHolder(LayoutInflater.from(this.mContext).inflate(R.layout.item_quality_list, viewGroup, false));
    }

    public void onBindViewHolder(QualityListHolder qualityListHolder, final int i) {
        final String str = (String) this.mList.get(i);
        qualityListHolder.mTvTitle.setText(str);
        qualityListHolder.mImgIcDownload.setOnClickListener(new OnClickListener() {
//            private final /* synthetic */ int f$1 = i;
//            private final /* synthetic */ String f$2;
            public final void onClick(View view) {
                QualityListAdapter.lambda$onBindViewHolder$0(QualityListAdapter.this, i, str, view);
            }
        });
    }

    public static /* synthetic */ void lambda$onBindViewHolder$0(QualityListAdapter qualityListAdapter, int i, String str, View view) {
        if (qualityListAdapter.mTag.equalsIgnoreCase("tag_vimeo")) {
            qualityListAdapter.mCallBack.onItemClicked(String.valueOf(i));
        } else {
            qualityListAdapter.mCallBack.onItemClicked(str);
        }
    }

    public int getItemCount() {
        return this.mList.size();
    }
}
