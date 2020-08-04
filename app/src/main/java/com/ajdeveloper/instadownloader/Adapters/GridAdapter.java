package com.ajdeveloper.instadownloader.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.ajdeveloper.instadownloader.Downloader.Constants;
import com.ajdeveloper.instadownloader.R;
import com.ajdeveloper.instadownloader.Utils.Utilities;

public class GridAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<File> filesList;
    private boolean isWAFragment;
    private LayoutInflater inflater;
    private int size;

    public GridAdapter(Context context, ArrayList<File> filesList, boolean isWAFragment) {
        this.context = context;
        this.filesList = filesList;
        this.isWAFragment = isWAFragment;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.e("FilesListSize", size + "");
        for (int i = 0; i < filesList.size(); i++) {
            if (!isValidMediaFile(filesList.get(i))) {
                this.filesList.remove(i);
            }
        }
        size = this.filesList.size();
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public File getItem(int position) {
        return filesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"InflateParams", "ViewHolder"})
    @Override
    public View getView(int position, View view, ViewGroup parent) {
//        if (view == null)
        view = inflater.inflate(R.layout.grid_view_item, null);
        ImageView ivGrid = view.findViewById(R.id.iv_grid);
        ImageView ivGridPlay = view.findViewById(R.id.iv_grid_play);
        ImageView ivDownload = view.findViewById(R.id.iv_download);
        final File tempFile = filesList.get(position);
        if (tempFile.getAbsolutePath().endsWith(".mp4"))
            ivGridPlay.setVisibility(View.VISIBLE);
        if (isWAFragment) {
            ivDownload.setVisibility(View.VISIBLE);
            ivDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utilities.showDialog(context, "Save Status!", "Do you want to save this WhatsApp Status?", "YES", "NO", true, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    File myDir = new File(Constants.Parent + Constants.IMAGES);
                                    if (!myDir.exists())
                                        myDir.mkdirs();
                                    try {
                                        FileUtils.copyFileToDirectory(tempFile, myDir);
                                        Toast.makeText(context, "Status saved!", Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        Toast.makeText(context, "Error saving status!", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    });
                }
            });
        }
        Glide.with(context).load(tempFile).into(ivGrid);
        return view;
    }

    public void notifyDataSetChanged(ArrayList<File> files) {
        filesList = files;
        notifyAdapter();
    }

    private void notifyAdapter() {
        size = filesList.size();
        notifyDataSetChanged();
    }

    private boolean isValidMediaFile(File file) {
        return (file.getName().endsWith(".jpg") ||
                file.getName().endsWith(".gif") ||
                file.getName().endsWith(".mp4"));
    }

}
