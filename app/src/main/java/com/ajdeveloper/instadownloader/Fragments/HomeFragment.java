package com.ajdeveloper.instadownloader.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ajdeveloper.instadownloader.Interfaces.CustomCallbackListener;
import com.ajdeveloper.instadownloader.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private CustomCallbackListener callbackListener;

    public HomeFragment(CustomCallbackListener callbackListener) {
        // Required empty public constructor
        this.callbackListener = callbackListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate videodownloader layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        setListener(view.findViewById(R.id.cv_url), 1);
        setListener(view.findViewById(R.id.cv_browse), 2);
        setListener(view.findViewById(R.id.cv_fb), 3);
        setListener(view.findViewById(R.id.cv_insta), 4);
        setListener(view.findViewById(R.id.cv_wa), 5);
        setListener(view.findViewById(R.id.cv_dm), 6);
        setListener(view.findViewById(R.id.cv_vimeo), 7);
        setListener(view.findViewById(R.id.cv_twitter), 8);
        setListener(view.findViewById(R.id.cv_saved), 9);
    }

    private void setListener(View view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (position == 6) {
//                    BrowserActivity.start(getContext(), "https://www.dailymotion.com");
//                    return;
//                }
//                if (position == 7) {
//                    BrowserActivity.start(getContext(), "https://vimeo.com/watch");
//                    return;
//                }
//                if (position == 8) {
//                    BrowserActivity.start(getContext(), "https://www.twitter.com");
//                    return;
//                }
//                if (position == 9) {
//                    callbackListener.onItemClicked(6);
//                }
                callbackListener.onItemClicked(position);
            }
        });
    }

}
