package com.ajdeveloper.instadownloader.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.ajdeveloper.instadownloader.R;
import com.ajdeveloper.instadownloader.model.HLS.Format;

public class QualityList extends RecyclerView.Adapter<QualityListAdapter.QualityListHolder> {
    /* access modifiers changed from: private */
    public RecyclerCallBack mCallBack;
    private Context mContext;
    private List<Format> mList;
    private String mTag;

    public class QualityListHolder extends RecyclerView.ViewHolder {
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
        void onItemClicked(Format format);
    }

    public QualityList(Context context, List<Format> list, String str, RecyclerCallBack recyclerCallBack) {
        this.mContext = context;
        this.mList = list;
        this.mCallBack = recyclerCallBack;
        this.mTag = str;
    }

    public QualityListAdapter.QualityListHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new QualityListAdapter.QualityListHolder(LayoutInflater.from(this.mContext).inflate(R.layout.item_quality_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QualityListAdapter.QualityListHolder qualityListHolder, int i) {
        final Format format = (Format) this.mList.get(i);
        qualityListHolder.mTvTitle.setText(format.getFormatId());
        qualityListHolder.mImgIcDownload.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                QualityList.this.mCallBack.onItemClicked(format);
            }
        });
    }

    public int getItemCount() {
        return this.mList.size();
    }
}
