package com.ajdeveloper.instadownloader.FragmentsN;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.ajdeveloper.instadownloader.R;

public class AppIntroFragment extends Fragment {
    @BindView(R.id.imgIcCountAppIntroFragment)
    ImageView mImgIcCount;
    @BindView(R.id.imgHelpScreenThumbnailAppIntro)
    ImageView mImgThumbnail;
    @BindView(R.id.tvTitleAppIntroFragment)
    TextView mTvTitle;

    public static Fragment getIntstance(Integer num, String str, int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("args_res_id", num);
        bundle.putString("args_title", str);
        bundle.putInt("args_count_res_id", i);
        AppIntroFragment appIntroFragment = new AppIntroFragment();
        appIntroFragment.setArguments(bundle);
        return appIntroFragment;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_app_intro, viewGroup, false);
        ButterKnife.bind(this, inflate);
        if (getArguments() != null) {
            int i = getArguments().getInt("args_res_id");
            int i2 = getArguments().getInt("args_count_res_id");
            this.mTvTitle.setText(getArguments().getString("args_title"));
            this.mImgThumbnail.setImageResource(i);
            this.mImgIcCount.setImageResource(i2);
        }
        return inflate;
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }
}
