package com.ajdeveloper.instadownloader.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import com.ajdeveloper.instadownloader.Adapters.QualityListAdapter;
import com.ajdeveloper.instadownloader.R;

public class BSUtils {
    private Context mContext;

    public interface BSCallBack {
        void onBsHidden();

        void onDownloadClicked(String str);
    }

    public BSUtils(Context context) {
        this.mContext = context;
    }

    public void showSimpleSheet(String str, String str2, final BSCallBack bSCallBack) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this.mContext);
        View inflate = ((Activity) this.mContext).getLayoutInflater().inflate(R.layout.layout_download_sheet_simple, null, false);
        bottomSheetDialog.setContentView(inflate);
        Button button = (Button) inflate.findViewById(R.id.btnDownloadSheetSimple);
        TextView textView = (TextView) inflate.findViewById(R.id.tvTitleLayoutDownloadSheetSimple);
        TextView textView2 = (TextView) inflate.findViewById(R.id.tvSizeDownloadSheetSimple);
        LinearLayout linearLayout = (LinearLayout) inflate.findViewById(R.id.layoutFbBannerContainerSheetSingle);
        AdView adView = (AdView) inflate.findViewById(R.id.adViewBanner);
        textView.setText(str);
        if (str2 != null && !TextUtils.isEmpty(str2)) {
            textView2.setText(str2);
        }
        button.setOnClickListener(new OnClickListener() {
            @Override
            public final void onClick(View view) {
                bottomSheetDialog.dismiss();
                bSCallBack.onDownloadClicked("");
            }
        });
        bottomSheetDialog.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
                bSCallBack.onBsHidden();
            }
        });
//        if (FirebaseRemoteConfig.getInstance().getBoolean("fb_banner_quality_list")) {
//            loadFbBannerAd(linearLayout);
//        } else {
            loadAdmobBanner(adView);
//        }
        bottomSheetDialog.show();
    }

    private void loadFbBannerAd(LinearLayout linearLayout) {
//        try {
//            if (!SharedPref.getInstance(this.mContext).getSpBoolean("remove_ads", false)) {
//                com.facebook.ads.AdView adView = new com.facebook.ads.AdView(this.mContext, this.mContext.getResources().getString(R.string.fb_banner_id), AdSize.BANNER_HEIGHT_50);
//                linearLayout.addView(adView);
//                adView.loadAd();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void showListSheet(String str, String str2, ArrayList<String> arrayList, String str3, final BSCallBack bSCallBack) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this.mContext, R.style.BsDialog);
        View inflate = ((Activity) this.mContext).getLayoutInflater().inflate(R.layout.layout_download_sheet_list, null, false);
        bottomSheetDialog.setContentView(inflate);
        TextView textView = (TextView) inflate.findViewById(R.id.tvTitleLayoutDownloadSheetList);
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.recyclerDownloadSheetList);
        LinearLayout linearLayout = (LinearLayout) inflate.findViewById(R.id.layoutFbBannerContainerSheetList);
        AdView adView = (AdView) inflate.findViewById(R.id.adViewBanner);
        textView.setText(str);
        QualityListAdapter qualityListAdapter = new QualityListAdapter(this.mContext, arrayList, str3, new QualityListAdapter.RecyclerCallBack() {
            public void onItemClicked(String str) {
                bottomSheetDialog.dismiss();
                bSCallBack.onDownloadClicked(str);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this.mContext));
        recyclerView.setAdapter(qualityListAdapter);
        bottomSheetDialog.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
                bSCallBack.onBsHidden();
            }
        });
//        if (FirebaseRemoteConfig.getInstance().getBoolean("fb_banner_quality_list")) {
//            loadFbBannerAd(linearLayout);
//        } else {
            loadAdmobBanner(adView);
//        }
        bottomSheetDialog.show();
    }

    private void loadAdmobBanner(AdView adView) {
        try {
            AdsUtil.getInstance(this.mContext).loadBannerAd(adView, "admob_banner_quality_list", new AdsUtil.AdsCallBack() {
                public void onAdFailedToLoad(String str, String str2) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
