package com.ajdeveloper.instadownloader.Fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import com.ajdeveloper.instadownloader.Activities.ImageViewerActivity;
import com.ajdeveloper.instadownloader.Adapters.GridAdapter;
import com.ajdeveloper.instadownloader.Downloader.Constants;
import com.ajdeveloper.instadownloader.Interfaces.CustomCallbackListener;
import com.ajdeveloper.instadownloader.R;
import com.ajdeveloper.instadownloader.Utils.FileUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class WAFragment extends Fragment {


    public WAFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate videodownloader layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wa, container, false);
        Context context = getContext();
        initViews(view, context);
        return view;
    }

    private ArrayList<File> files;
    private GridAdapter gridAdapter;

    private void initViews(View view, final Context context) {
        files = FileUtils.getFilesList(Constants.Parent + Constants.WASTATUSDIR);
        GridView gridView = view.findViewById(R.id.grid_view);
        gridAdapter = new GridAdapter(context, files, true);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageViewerActivity.customImageViewer(context, files, position, new CustomCallbackListener() {
                    @Override
                    public void onItemClicked(int msg) {
                        Toast.makeText(context, "File Deleted", Toast.LENGTH_SHORT).show();
                        update();
                    }
                });
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
            update();
    }

    private void update() {
        if (gridAdapter != null) {
            files = FileUtils.getFilesList(Constants.Parent + Constants.IMAGES);
//        if (files.size() != gridAdapter.getCount())
            gridAdapter.notifyDataSetChanged(files);
            //BC for Files Updates
        }
    }

    public int getCount() {
        if (gridAdapter != null)
            return gridAdapter.getCount();
        return 0;
    }

}
