package com.ajdeveloper.instadownloader.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import com.ajdeveloper.instadownloader.R;

import static com.ajdeveloper.instadownloader.Activities.ImageViewerActivity.hidethis;


public class ImageViewerAdaptor extends PagerAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<File> files;
    private boolean flag;
    private float x, y;
    private boolean check = false;
    private File file;

    public ImageViewerAdaptor(Context context, List<File> files) {
        flag = false;
        mContext = context;
        this.files = files;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @NonNull
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.rowforpageradapter, container, false);
        ImageView imageView = itemView.findViewById(R.id.imageView);
        ImageView btn_play = itemView.findViewById(R.id.btn_play);
        file = files.get(position);
        if (file.getName().endsWith("mp4")) {
            btn_play.setVisibility(View.VISIBLE);
            btn_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        playFile(position);
//                        VideoPlayerActivity.customVideoPlayer(mContext, files, position);
                    } catch (Exception ignored) {}
                }
            });
        }
        Glide.with(mContext).load(file).into(imageView);
        try {
            imageView.setOnTouchListener(new ImageMatrixTouchHandler(mContext) {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            x = event.getX();
                            y = event.getY();
                            check = true;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (!((event.getX() < x + 10 && event.getX() > x - 10) || (event.getY() < y + 10 && event.getY() > y + 10))) {
                                check = false;
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (check) {
                                if (!flag) {
                                    hidethis.setVisibility(View.INVISIBLE);
                                    flag = true;
                                } else {
                                    hidethis.setVisibility(View.VISIBLE);
                                    flag = false;
                                }
                            }
                            break;
                    }
                    return super.onTouch(view, event);
                }
            });
        }catch (Exception ignored){}
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }

    private void playFile(int position) {
        File file = files.get(position);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        //Uri uri = Uri.fromFile(file1);
        Uri uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".my.package.name.provider", file);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "video/*");
        try {
            mContext.startActivity(intent);
        } catch (Exception ee) {
            Toast.makeText(mContext, "Something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }
}

