package com.ajdeveloper.instadownloader.Fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ajdeveloper.instadownloader.NaviDrawer;
import com.ajdeveloper.instadownloader.R;
import com.ajdeveloper.instadownloader.Utils.Constants;
import com.ajdeveloper.instadownloader.Utils.Utilities;

/**
 * A simple {@link Fragment} subclass.
 */
public class SocialFragment extends Fragment {

    public SocialFragment() {

    }

    private NaviDrawer naviDrawer;
    @SuppressLint("ValidFragment")
    public SocialFragment(NaviDrawer naviDrawer) {
        this.naviDrawer = naviDrawer;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate videodownloader layout for this fragment
        return inflater.inflate(R.layout.fragment_social, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView ivURL = view.findViewById(R.id.iv_url);
        ImageView ivFb = view.findViewById(R.id.iv_fb);
        ImageView ivDM = view.findViewById(R.id.iv_dm);
        setListener(ivFb, Constants.FB);
        setDownloadListener(ivURL, "Enter URL to download from:");
        setDownloadListener(ivDM, "Enter DailyMotion URL to download from:");

    }

    private void setDownloadListener(ImageView imageView, final String text) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.changeFragment(naviDrawer, new DownloaderFragment(text));
            }
        });
    }

    private void setListener(ImageView imageView, final String url) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.changeFragment(naviDrawer, new BrowserFragment(Constants.FB));
//                startActivity(new Intent(naviDrawer, BrowserActivity.class).putExtra("URL", url));
            }
        });
    }

}
